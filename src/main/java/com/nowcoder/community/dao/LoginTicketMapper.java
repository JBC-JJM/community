package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginTicketMapper {

    @Insert({"INSERT INTO login_ticket(user_id,ticket,`status`,expired) VALUES " +
            "(#{userId},#{ticket},#{status},#{expired})"})
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select("SELECT user_id,ticket,`status`,expired FROM login_ticket " +
            "WHERE ticket = #{ticket}")
    LoginTicket selectByTicket(String ticket);

    @Update("UPDATE login_ticket SET `status`=#{status} " +
            "WHERE ticket =#{ticket}")
    int updateStatusTicket(@Param("ticket") String ticket, @Param("status") int status);
}
