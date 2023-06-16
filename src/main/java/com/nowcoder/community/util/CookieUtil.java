package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {
    /**
     * @param request 请求
     * @param name  cookie的属性名称
     * @return cookie的值
     */
    static public String getValue(HttpServletRequest request, String name) {

        //判断为空永远再最前面
        if(request==null|| name == null){
            throw new RuntimeException("请求对象为空或者名称为空");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
