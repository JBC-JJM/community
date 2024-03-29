package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

//添加评论
//异步请求的控制器，返回的一般只有数据
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Comment comment) {

        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        int count = commentService.addComment(comment);

        //系统发通知通知接收者
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())//评论的类型
                .setEntityId(comment.getEntityId())//评论的帖子/评论的id
                .setData("postId", discussPostId);//帖子id，因为评论也在帖子下的

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);


        //触发添加es评论数量事件:kafka，对es来说插入和更改是同一种事件
       if(comment.getEntityType()==ENTITY_TYPE_POST){
           event = new Event()
                   .setTopic(TOPIC_PUBLISH)
                   .setUserId(user.getId())
                   .setEntityType(ENTITY_TYPE_POST)
                   .setEntityId(discussPostId);
           eventProducer.fireEvent(event);
       }

        if(comment.getEntityType()==ENTITY_TYPE_POST){
            //帖子热度变化：评论帖子
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }


        return "redirect:/discuss/detail/" + discussPostId;
    }
}
