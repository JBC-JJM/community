package com.nowcoder.community;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        redisTemplate.opsForValue().set("test:name", "zc");
        System.out.println(redisTemplate.opsForValue().get("test:name"));
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("zc");
        user.setPassword("1234");
        redisTemplate.opsForValue().set("test:user2", user);
        System.out.println(redisTemplate.opsForValue().get("test:user2"));
    }




    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testString2() {
        // 写入一条String数据
        stringRedisTemplate.opsForValue().set("verify:phone:13600527634", "124143");
        // 获取string数据
        Object name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }

    //类似json
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSaveUser2() throws IOException {
        // 创建对象
        User user = new User();
        user.setUsername("zc");
        user.setPassword("1234");

        //1、 手动序列化
        String json = mapper.writeValueAsString(user);
        // 写入数据
        stringRedisTemplate.opsForValue().set("user:200", json);

        //2、 获取数据
        String jsonUser = stringRedisTemplate.opsForValue().get("user:200");
        // 手动反序列化
        User user1 = mapper.readValue(jsonUser, User.class);

        System.out.println("user1 = " + user1);
    }


    @Test
    public void testHash(){
        stringRedisTemplate.opsForHash().put("user:400", "name", "虎哥");
        stringRedisTemplate.opsForHash().put("user:400", "age", "21");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("user:400");
        System.out.println("entries = " + entries);
    }

}
