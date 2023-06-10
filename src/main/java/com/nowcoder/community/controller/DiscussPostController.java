package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Result;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

//异步请求的控制器，返回的一般只有数据
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加帖子，有手就行
     * @param title 标题
     * @param content 内容
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Result addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return new Result(403, null, "没有登入");
        }
        DiscussPost discussPost = new DiscussPost(user.getId(), title, content, new Date());
        discussPost.setType(0);
        discussPost.setStatus(0);
        discussPost.setCommentCount(0);
        discussPost.setScore(0.0);
        int count = discussPostService.insertPost(discussPost);
        return new Result(0, null, "插入评论成功");
    }

    @Autowired
    private UserService userService;

    /**
     * 显示详细内容，有手就行
     * @param discussPostId 帖子id
     * @param model
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model) {
        // 帖子
        DiscussPost post = discussPostService.findPostById(discussPostId);
        // 找作者,这里进行了两次查询表，可以说是比较低效的，可以在dao层使用联表查询，是业务就重叠了
        //到时使用reids优化
        User user = userService.selectById(post.getUserId());

        model.addAttribute("post", post);
        model.addAttribute("user", user);

        return "/site/discuss-detail";
    }
}
