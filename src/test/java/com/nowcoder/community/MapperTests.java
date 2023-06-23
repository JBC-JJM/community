package com.nowcoder.community;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

import static com.nowcoder.community.util.CommunityConstant.TOPIC_COMMENT;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTests {

    @Autowired
    private UserService userService;

    @Test
    public void testSelectUser() {
        User user = userService.selectById(101);
        System.out.println(user);

        user = userService.selectByName("liubei");
        System.out.println(user);

        user = userService.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        boolean rows = userService.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void updateUser() {
        boolean rows = userService.updateStatus(150, 1);
        System.out.println(rows);

        rows = userService.updateHeader(150, "http://www.nowcoder.com/102.png");
        System.out.println(rows);

//        rows = userService.updatePassword(150, "hello");
//        System.out.println(rows);
    }


    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testSelectAll() {
        System.out.println(discussPostService.selectAll(101));

        System.out.println(discussPostService.discussPosts(null, 0, 3));

        System.out.println(discussPostService.discussPostsRows(0));
    }


    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testInsert() {
        DiscussPost discussPost = new DiscussPost(101, "ss", "ddd", 0, 0, new Date(), 12, 12.);
        discussPostMapper.insert(discussPost);
    }


    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket(
                12,
                CommunityUtil.generateUUID(),
                1,
                new Date(System.currentTimeMillis())
        );
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testselectByTicket() {
        String tit = "ff489f9cb62a4c239c3fcbc792e02bdd";
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(tit);
        System.out.println(loginTicket);
        loginTicketMapper.updateStatusTicket(tit, 0);
    }


    @Autowired
    private CommentService commentService;

    @Test
    public void testcommentService() {
        List<Comment> commentList = commentService.selectCommentByEntity(1, 228, 1, 5);
        System.out.println(commentList);

        int count = commentService.selectCountByEntity(1, 228);
        System.out.println(count);
    }


    @Autowired
    private MessageService messageService;

    @Test
    public void testMessageService() {
        List<Message> conversations = messageService.findConversations(111, 1, 5);
        System.out.println("会话列表" + conversations);

        int conversationCount = messageService.findConversationCount(111);
        System.out.println(conversationCount);

        List<Message> letters = messageService.findLetters("111_112", 1, 5);
        System.out.println("和112的私信" + letters);

        int letterCount = messageService.findLetterCount("111_112");
        System.out.println(letterCount);

        int letterUnreadCount = messageService.findLetterUnreadCount(131, "111_131");
        System.out.println(letterUnreadCount);
    }

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testMessageMapper() {
//        Message message = new Message();
//        message.setConversationId("111_117");
//        message.setContent("afd");
//        message.setCreateTime(new Date());
//        message.setFromId(111);
//        message.setToId(117);
//        message.setStatus(0);
//        messageMapper.insertMessage(message);
//
        List<Integer> ids = new ArrayList<>();
        ids.add(354);
        ids.add(353);
        messageMapper.updateStatus(ids, 1);
    }

    @Test
    public void testIN() {
        Integer a = 12;
        Integer b = new Integer(12);
        System.out.println(a.equals(b));
        System.out.println(b == 12);
    }


    @Test
    public void testTest(){
        Map<String, Object> messageVO = new HashMap<>();
        System.out.println(messageVO.get("tt"));
    }


}