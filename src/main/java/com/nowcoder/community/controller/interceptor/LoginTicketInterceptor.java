package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截后获取用户对象
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取得ticket，根据ticket查询用户的数据
        String ticket = CookieUtil.getValue(request, "ticket");
        LoginTicket loginTicket = userService.findLoginTicket(ticket);
        User user = userService.selectById(loginTicket.getUserId());
        //判空
        if (loginTicket != null && user != null) {
            //有效性、时效
            //date类型比较 https://blog.csdn.net/chenpp666/article/details/125172674
            if (loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //将用户数据放到ThreadLocal
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (hostHolder.getUser() == null || modelAndView == null) {
            return;
        }
        modelAndView.addObject("loginUser", hostHolder.getUser());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clean();
    }
}
