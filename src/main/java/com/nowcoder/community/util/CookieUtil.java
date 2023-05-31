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
        Cookie[] cookies = request.getCookies();
        if(request==null|| StringUtils.isBlank(name)){
            throw new RuntimeException("请求对象为空或者名称为空");
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())){
                return cookie.getValue();
            }
        }
        return null;
    }
}
