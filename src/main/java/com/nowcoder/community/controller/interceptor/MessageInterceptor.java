package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = hostHolder.getUser();
        if (user == null || modelAndView == null) {
            return;
        }
        // 查询总未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);//1、私信未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);//2、通知未读数量
        modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
    }
}
