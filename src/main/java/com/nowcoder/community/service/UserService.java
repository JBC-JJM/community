package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

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
}
