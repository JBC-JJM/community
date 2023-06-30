package com.nowcoder.community.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    @Autowired
    MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    //订阅多个通知主题:评论、关注、点赞
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleConsumerMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息为空");
            return;
        }

        // 解析event在放到message
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式有误");
            return;
        }

        // 发送站内通知
        //message：消息整体
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setStatus(0);

        //content：消息内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            //map的遍历
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
            //低效
//            for(String key:event.getData().keySet()){
//                Object value = event.getData().get(key);
//            }
        }

        message.setContent(JSON.toJSONString(content));
        messageService.insertMessage(message);
    }


    @Autowired
    private ElasticsearchService searchService;

    @Autowired
    private DiscussPostService discussPostService;

    //es：发帖、更新帖子
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handleConsumerPUBLISH(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息为空");
            return;
        }
        // 解析event在放到discussPost
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式有误");
            return;
        }

        //插入或者更新es：感觉就是es和mysql的一致性的问题
        DiscussPost post = discussPostService.findPostById(event.getEntityId());
        searchService.saveDiscussPost(post);

    }

    //es:删除帖子
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息为空");
            return;
        }
        // 解析event在放到discussPost
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式有误");
            return;
        }

        //插入或者更新es：感觉就是es和mysql的一致性的问题
        searchService.delete(event.getEntityId());
    }


    @Value("${wk.image.command}")
    private String wkImageCommand;

    @Value("${wk.image.storage}")

    private String wkImageStorage;
    // 消费分享事件
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShareMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败: " + e.getMessage());
        }
    }
}
