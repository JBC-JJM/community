package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String getIndexPage(Page page, Model model,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {

        //总数量
        int rows = postService.discussPostsRows(0);
        page.setRows(rows);
        page.setPath("/index?orderMode=" + orderMode);

        //帖子列表+用户数据
        List<Map<String, Object>> discussPosts = new ArrayList();
        //帖子列表
        List<DiscussPost> postList = postService
                .discussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        for (DiscussPost post : postList) {
            Integer userId = post.getUserId();  //根据userid找发表帖子的用户
            //用户数据
            User user = userService.selectById(userId);

            //点赞消息
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());

            Map<String, Object> map = new HashMap();
            map.put("post", post);
            map.put("user", user);
            map.put("likeCount", likeCount);

            discussPosts.add(map);
        }

//在开发模板时：假如模板有错误，那么报错很难找到
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page", page);
        return "/index";
    }

    //这里是手动导向错误页面，异常页面会自动有springBoot加载，不过要再error下
    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        //异常页面
        return "/error/500";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
