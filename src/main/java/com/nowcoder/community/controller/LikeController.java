package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Result;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController{

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    //点赞
    @PostMapping("/like")
    @ResponseBody
    public Result like(int entityType, int entityId,int entityUserId) {
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId,entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map map = new HashMap<String, Object>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return new Result(0, map, null);
    }
}
