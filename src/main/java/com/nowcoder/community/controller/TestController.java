package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityUtil;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Session;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("/setCookie")
    public void setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        cookie.setPath("/community/test");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return;
    }

    @GetMapping("/getAllCookie")
    public void getAllCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cook : cookies) {
            if("code".equals(cook.getName())){
                System.out.println(cook.getValue());
            }
        }
        System.out.println(Arrays.toString(cookies));
    }

    @GetMapping("/getCookie")
    public void getCookie(@CookieValue("code") String code) {
        System.out.println(code);
    }


    @GetMapping("/setSession")
    public void setSession(HttpSession session){
        session.setAttribute("code",CommunityUtil.generateUUID());
        return;
    }

    @GetMapping("/getSession")
    public void getSession(HttpSession session){
        Object code = session.getAttribute("code");
        System.out.println(code);
        return;
    }

}
