package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.UserForm;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

import static com.nowcoder.community.util.CommunityConstant.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //激活码邮件相关
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    //激活码的url相关
    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //注册
    @Override
    public Map<String, Object> register(User user) {

        Map<String, Object> map = new HashMap();

        //检验为空,这个本来应该是数据库的活
        if (user == null) {
            throw new RuntimeException("用户为空！！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        //检验账号存在
        if (userMapper.selectByName(user.getUsername()) != null) {
            map.put("usernameMsg", "账号已存在!");
            return map;
        }
        if (userMapper.selectByEmail(user.getEmail()) != null) {
            map.put("emailMsg", "邮箱已存在!");
            return map;
        }

//        注册：封装数据给前端(用户填的+业务改造)，调用dao
        String salt = CommunityUtil.generateUUID().substring(0, 5);//截取0到4，不包括5
        String password = CommunityUtil.MD5(user.getPassword() + salt);
        String activationCode = CommunityUtil.generateUUID();
        String headerUrl = String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));

        User newUser = new User(user.getUsername(), password, salt, user.getEmail(), 0, 0, activationCode, headerUrl, new Date());
        int count = userMapper.insertUser(newUser);


//        发送激活码邮件
        Context context = new Context();
        context.setVariable("email", newUser.getEmail());
        //激活的url(id+激活码)由activation控制
        String url = domain + contextPath + "/activation/" + newUser.getId() + "/" + newUser.getActivationCode();
        context.setVariable("url", url);
        //使用模板引擎渲染并返回html
        String message = templateEngine.process("/mail/activation.html", context);
        mailClient.sendMail(user.getEmail(), "牛客激活邮件", message);

        //sql查询结>0
        if (count > 0) {
            System.out.println(newUser);
            map.put("code", "success");
            return map;
        }
        return null;
    }

    //激活
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        //判断激活码,需要注意的是不可以使用！=
        if (user.getActivationCode() == null ||
                !user.getActivationCode().equals(code)) {
            return ACTIVATION_FAILURE;
        }
        //重复激活
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }
        //激活他
        userMapper.updateStatus(userId, 1);
        return ACTIVATION_SUCCESS;
    }


    @Override
    public User selectById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public User selectByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public User selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public boolean insertUser(User user) {
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateStatus(int id, int status) {
        return userMapper.updateStatus(id, status) > 0;
    }

    @Override
    public boolean updateHeader(int id, String headerUrl) {
        return userMapper.updateHeader(id, headerUrl) > 0;
    }

    @Override
    public boolean updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password) > 0;
    }


    @Autowired
    private LoginTicketMapper loginTicketMapper;

    /**
     * 其中时长是时间戳，以秒为单位
     * @param userForm 将前端传来的数据封装一下：名称+密码+时长+记住我
     * @return
     */
    @Override
    public Map<String, Object> login(UserForm userForm) {
        Map<String, Object> map = new HashMap<>();
        //判空
        //一般引用对象使用null ， 这也是为什么使用Integer不使用int的原因，可以偷懒
        //String判空 StringUtils.isBlank 或 ==null/“ ”
        //String判相等  "abc".equals()
        if (StringUtils.isBlank(userForm.getUsername())) {
            map.put("usernameMsg", "用户名不可为空");
            return map;
        }
        if (StringUtils.isBlank(userForm.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        //验证 存在\激活？
        User user = userMapper.selectByName(userForm.getUsername());
        if (user == null || user.getStatus() == 0) {
            map.put("usernameMsg", "账号不存在或者没激活");
            return map;
        }
        String password=CommunityUtil.MD5(userForm.getPassword()+user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误!");
            return map;
        }

        //干活：生成登入凭证并insert
        //凭证
        String ticket = CommunityUtil.generateUUID();
        //有效时长，注意时间戳以毫秒为单位
        Date date = new Date(System.currentTimeMillis() + userForm.getExpiredSecond()*1000);
        //封装为实体搞到dao
        LoginTicket loginTicket = new LoginTicket(user.getId(), ticket, 0, date);
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",ticket);
        return map;
    }

    @Override
    public void logout(String ticket) {
        loginTicketMapper.updateStatusTicket(ticket,1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }
}
