package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//工具类大多都是可重用的bean
@Component
public class MailClient {

    //单独的配置该类的输出日志，比如平时其他类要是出错不会输出自定义的信息，
    // 这和aop不太一样，这里只是输出日志，没法处理
    private Logger logger = LoggerFactory.getLogger(MailClient.class);

    //使用springBoot的JavaMailSender，他会加载yaml的信息
    @Autowired
    private JavaMailSender mailSender;

    //该bean的主要方法
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("jumpupchen@163.com");//谁发信息，需要和配置文件的一致
            helper.setTo(to);//发给谁
            helper.setSubject(subject);//标题
            helper.setText(content,true);//内容+开启html模式
            mailSender.send(helper.getMimeMessage());//发送
        } catch (MessagingException e) {
            logger.error("发送邮件失败"+e.getMessage()); //日志输出
        }
    }
}
