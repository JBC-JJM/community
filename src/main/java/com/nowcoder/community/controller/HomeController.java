package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Page page, Model model) {

        //总数量
        int rows = postService.discussPostsRows(0);
        page.setRows(rows);
        page.setPath("/index");

        //帖子列表+用户数据
        List<Map<String, Object>> discussPosts = new ArrayList();
        //帖子列表
        List<DiscussPost> postList = postService.discussPosts(0, page.getOffset(), page.getLimit());
        for (DiscussPost post : postList) {
            Integer userId = post.getUserId();  //根据userid找发表帖子的用户
            //用户数据
            User user = userService.selectById(userId);

            Map<String, Object> map = new HashMap();
            map.put("post", post);
            map.put("user", user);

            discussPosts.add(map);
        }

//在开发模板时：假如模板有错误，那么报错很难找到
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page", page);
        return "/index";
    }
}
