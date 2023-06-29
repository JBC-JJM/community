package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Result;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    @PostMapping("/like")
    @ResponseBody
    public Result like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId, entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map map = new HashMap<String, Object>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //点赞：系统发消息
        if (likeCount == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)//类型
                    .setEntityId(entityId)//帖子/评论的id
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);

            eventProducer.fireEvent(event);
        }

        if(entityType==ENTITY_TYPE_POST){
            //帖子热度变化: 点赞帖子
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, entityId);
        }

        return new Result(0, map, null);
    }
}
