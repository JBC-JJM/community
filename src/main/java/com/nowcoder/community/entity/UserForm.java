package com.nowcoder.community.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Date;

/**
 * 前端的数据封装
 */
public class UserForm {
    private String username;
    private String password;
    private String verifycode;
    private Boolean rememberme;
    private Long expiredSecond;

    @Override
    public String toString() {
        return "UserForm{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", verifycode='" + verifycode + '\'' +
                ", rememberme=" + rememberme +
                ", expiredSecond=" + expiredSecond +
                '}';
    }

    public UserForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifycode() {
        return verifycode;
    }

    public void setVerifycode(String verifycode) {
        this.verifycode = verifycode;
    }

    public Boolean getRememberme() {
        return rememberme;
    }

    public void setRememberme(Boolean rememberme) {
        this.rememberme = rememberme;
    }

    public Long getExpiredSecond() {
        return expiredSecond;
    }

    public void setExpiredSecond(Long expiredSecond) {
        this.expiredSecond = expiredSecond;
    }

    public UserForm(String username, String password, String verifycode, Boolean rememberme, Long expiredSecond) {
        this.username = username;
        this.password = password;
        this.verifycode = verifycode;
        this.rememberme = rememberme;
        this.expiredSecond = expiredSecond;
    }
}
