package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling//开启定时器
@EnableAsync//开启异步（注解使用多线程）
public class ThreadPoolConfig {

}
