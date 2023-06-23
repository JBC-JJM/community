package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

//私信
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //会话
    @GetMapping("/letter/list")
    public String getLetterList(Page page, Model model) {

        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //数据查询封装
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message conversation : conversationList) {
                HashMap<String, Object> map = new HashMap<>();
                int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId());
                int letterCount = messageService.findLetterCount(conversation.getConversationId());
                //当前用户是发送还是接收？
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();

                map.put("conversation", conversation);
                map.put("unreadCount", letterUnreadCount);
                map.put("letterCount", letterCount);
                //毕竟查出来的数据只知道双方的会话，不知道是谁接收
                map.put("target", userService.selectById(targetId));
                conversations.add(map);
            }
        }

        // 查询总未读消息数量
        //1、私信未读数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //2、通知未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        model.addAttribute("conversations", conversations);
        return "/site/letter";
    }

    //某个会话的私信
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(Page page, Model model, @PathVariable String conversationId) {

        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        //数据封装
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.selectById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters", letters);
        // 私信目标，其实和上面的逻辑一样，当前用户是发送还是接收？
        model.addAttribute("target", getLetterTarget(conversationId));

        //更新私信的已读状态
        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        // 假如当前用户是某个会话的发送方，则对方为target
        // 假如当前用户是某个会话的接收方，则我方为target
        if (hostHolder.getUser().getId() == id0) {
            return userService.selectById(id1);
        } else {
            return userService.selectById(id0);
        }
    }

    /**
     * 获取Message列表的id
     *
     * @param letterList essage列表
     * @return id列表
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                //假如当前用户为接受者,注意的是已读状态只有接受者可以改，注意引用类型使用equals
                if (hostHolder.getUser().getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }

        return ids;
    }

    //发私信
    @PostMapping("/letter/send")
    @ResponseBody
    public Result sendLetter(String toName, String content) {

        Integer fromUserId = hostHolder.getUser().getId();
        Integer toUserId = userService.selectByName(toName).getId();
        if (toUserId == null) {
            return new Result(1, null, "目标用户不存在!");
        }

        //数据封装
        String conversationId = fromUserId <= toUserId ?
                fromUserId + "_" + toUserId
                : toUserId + "_" + fromUserId;

        Message message = new Message(fromUserId, toUserId, conversationId, content, 0, new Date());

        int count = messageService.insertMessage(message);

        return new Result(0, null, "发送私信成功");
    }


    /**
     * 返回封装的messageVO
     *
     * @param messageTopic 类型：评论/点赞/关注
     * @return
     */
    private Map<String, Object> getMessageVO(String messageTopic) {
        Integer userId = hostHolder.getUser().getId();
        //最新通知
        Message message = messageService.findLatestNotice(userId, messageTopic);
        Map<String, Object> messageVO = new HashMap<>();

        messageVO.put("message", message);//空的也不会报错
        messageVO.put("count", messageService.findNoticeCount(userId, messageTopic));
        messageVO.put("unread", messageService.findNoticeUnreadCount(userId, messageTopic));

        if (message != null) {
            //解析通知内容
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSON.parseObject(content, HashMap.class);

            messageVO.put("user", userService.selectById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            if (data.get("postId") != null) {
                messageVO.put("postId", data.get("postId"));//关注主题：没有帖子id
            }
        }

        return messageVO;
    }


    //显示系统通知
    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        Integer userId = hostHolder.getUser().getId();
        Map<String, Object> messageVO = null;
        //主题：评论
        messageVO = getMessageVO(TOPIC_COMMENT);
        model.addAttribute("commentNotice", messageVO);

        //主题：点赞
        messageVO = getMessageVO(TOPIC_LIKE);
        model.addAttribute("likeNotice", messageVO);

        //主题：关注
        messageVO = getMessageVO(TOPIC_FOLLOW);
        model.addAttribute("followNotice", messageVO);

        // 查询总未读消息数量
        //1、私信未读数量
        int letterUnreadCount = messageService.findLetterUnreadCount(userId, null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //2、通知未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(userId, null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }


    //ES的mapper
    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate searchTemplate;

    //显示系统通知详细页
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(Model model, Page page, @PathVariable("topic") String topic) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));

        //详细通知：分页
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 一条通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSON.parseObject(content, HashMap.class);

                map.put("user", userService.selectById((Integer) data.get("userId")));//关注：跳到用户
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));//评论/点赞：跳到帖子

                // 通知的系统用户（默认为1）
                map.put("fromUser", userService.selectById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        //设置已读

        List<Integer> noticeIds = getLetterIds(noticeList);
        if (!noticeIds.isEmpty()) {//集合判空
            int count = messageService.readMessage(noticeIds);
        }

        return "/site/notice-detail";
    }

}
