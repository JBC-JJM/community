package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        rows = userService.updatePassword(150, "hello");
        System.out.println(rows);
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
        String tit="ff489f9cb62a4c239c3fcbc792e02bdd";
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(tit);
        System.out.println(loginTicket);
        loginTicketMapper.updateStatusTicket(tit,0);
    }

    @Test
    public void testDate() {
//        参考: https://blog.csdn.net/weixin_45948234/article/details/112178525
        long timeMillis = System.currentTimeMillis();
        timeMillis+=3600*1000;  //以毫秒计算
        System.out.println("没有格式化的时间戳" + timeMillis);

        Date date = new Date(timeMillis);
        System.out.println("强转的时间戳"+date);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        System.out.println("格式化的时间戳" + formatter.format(timeMillis));
    }
}