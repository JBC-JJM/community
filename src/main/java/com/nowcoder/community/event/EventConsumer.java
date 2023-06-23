package com.nowcoder.community.event;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    @Autowired
    MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    //订阅多个主题
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleConsumerMessage(ConsumerRecord record) {
        if (record == null) {
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
}
