package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

public interface UserService {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    boolean insertUser(User user);

    boolean updateStatus(int id, int status);

    boolean updateHeader(int id, String headerUrl);

    boolean updatePassword(int id, String password);
}
