package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTests {

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
    public void testSelectAll(){
        System.out.println(discussPostService.selectAll(101));

        System.out.println(discussPostService.discussPosts(null,0,3));

        System.out.println(discussPostService.discussPostsRows(0));
    }
}
