package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
//    注入模板引擎
    private TemplateEngine templateEngine;

    //发送文本试试
    @Test
    public void testMail(){
        mailClient.sendMail("1811546213@qq.com","hello","xiaojian");
    }

    //发送html试试
    @Test
    public void testHtmlMail(){
        //传给模板的属性
        Context context = new Context();
        context.setVariable("msg","xiaojian");
        //使用模板引擎渲染并返回html
        String message = templateEngine.process("/mail/demo.html", context);
        System.out.println(message);

        mailClient.sendMail("1811546213@qq.com","html",message);
    }


}
