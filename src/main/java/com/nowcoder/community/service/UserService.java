package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.entity.UserForm;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public interface UserService {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    boolean insertUser(User user);

    boolean updateStatus(int id, int status);

//    boolean updateHeader(int id, String headerUrl);

    /**
     * 更新密码，密码都是没加密的就行
     * @param id
     * @param password
     * @param oldPassword
     * @return
     */
    boolean updatePassword(int id, String password,String oldPassword);

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

    /**
     *
     * @param id
     * @param headerUrl ：web地址 = 域名（端口）+项目目录+user/header+文件名
     * @return
     */
    boolean updateHeader(int id,String headerUrl);

    /**
     * 查找当前用户的权限（角色）
     * @param userId
     * @return 权限集合
     */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
