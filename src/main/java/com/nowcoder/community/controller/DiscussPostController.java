package com.nowcoder.community.controller;

import com.nowcoder.community.entity.*;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//帖子
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService searchService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 添加帖子，有手就行
     *
     * @param title   标题
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


        // 触发发帖的es事件:kafka
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);


        return new Result(0, null, "插入评论成功");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 显示详细内容，手脚并用都不行
     *
     * @param discussPostId 帖子id
     * @param model
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Page page, Model model) {
        //1、 帖子
        DiscussPost post = discussPostService.findPostById(discussPostId);
        // 找作者,这里进行了两次查询表，可以说是比较低效的，可以在dao层使用联表查询，是业务就重叠了
        //到时使用reids优化
        User user = userService.selectById(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        //帖子点赞消息
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST, discussPostId);

        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);


        //前端的所需要的数据处理
        page.setLimit(5);
        page.setRows(post.getCommentCount());//直接拿帖子表的评论数
        page.setPath("/discuss/detail/" + discussPostId);

        //查询评论表对于帖子的评论
        List<Comment> commentList = commentService.selectCommentByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());
        //评论+回复
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();

                //2、评论
                commentVo.put("comment", comment);//评论
                commentVo.put("user", userService.selectById(comment.getUserId()));//作者

                //评论点赞消息
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                commentVo.put("likeStatus", likeStatus);


                //3、查询评论表对于评论的评论
                List<Comment> replyList = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复：对于评论的评论，所以嵌套到评论循环中
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();

                        replyVo.put("reply", reply);//回复
                        replyVo.put("user", userService.selectById(reply.getUserId()));//作者
                        User target = reply.getTargetId() == 0 ? null : userService.selectById(reply.getTargetId());
                        replyVo.put("target", target);//回复对象

                        //回复点赞消息
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }

                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                //总对象，变态对象
                commentVoList.add(commentVo);
            }
        }


        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }


    //置顶
    @PostMapping("/top")
    @ResponseBody
    public Result setTop(int discussPostId) {

        discussPostService.updateType(discussPostId, 1);

        // 触发发帖(更新)的es事件:kafka
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return new Result(0, null, "已置顶");
    }

    //加精
    @PostMapping("/wonderful")
    @ResponseBody
    public Result setWonderful(int discussPostId) {

        discussPostService.updateStatus(discussPostId, 1);

        // 触发发帖(更新)的es事件:kafka
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return new Result(0, null, "已加精");
    }

    @PostMapping("/delete")
    @ResponseBody
    public Result setDelete(int discussPostId) {

        discussPostService.updateStatus(discussPostId, 2);

        // 触发发帖(更新)的es事件:kafka
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return new Result(0, null, "已删除");
    }
}
