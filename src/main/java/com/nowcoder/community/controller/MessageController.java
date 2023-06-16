package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.Result;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

//私信
@Controller
public class MessageController {

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

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

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
            messageService.updateStatus(ids);
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
}
