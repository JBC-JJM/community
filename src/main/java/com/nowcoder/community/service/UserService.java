package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.UserForm;

import java.util.Map;

public interface UserService {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    boolean insertUser(User user);

    boolean updateStatus(int id, int status);

    boolean updateHeader(int id, String headerUrl);

    boolean updatePassword(int id, String password);

    Map<String,Object> register(User user);

    /**
     * @param userId url的id
     * @param code 激活码
     */
    public int activation(int userId,String code);

    /**
     * 登陆
     * @param userForm 将前端传来的数据封装一下：名称+密码+时长+记住我
     * @return
     */
    Map<String,Object> login(UserForm userForm);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);
}
