package com.nowcoder.community.entity;

import java.util.Date;

public class LoginTicket {
    private Integer id;
    private Integer userId;
    private String ticket;
    private Integer status;

    public LoginTicket() {
    }

    /**
     * 时效：当前时间戳+有效时长
     */
    private Date expired;

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public LoginTicket(Integer userId, String ticket, Integer status, Date expired) {
        this.id = id;
        this.userId = userId;
        this.ticket = ticket;
        this.status = status;
        this.expired = expired;
    }
}
