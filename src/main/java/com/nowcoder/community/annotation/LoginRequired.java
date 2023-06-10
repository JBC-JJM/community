package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识需要登入才能访问的请求
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // 元注解，在方法和文件上用
@Retention(RetentionPolicy.RUNTIME) // 一直活着，在运行阶段这个注解也不消失
public @interface LoginRequired {
}
