package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
}
