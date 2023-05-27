package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//首先声明是使用spring的测试类
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoggerTests {

   private Logger logger = LoggerFactory.getLogger(LoggerTests.class);

   @Test
    public void loggerOut(){

       System.out.println(logger.getName());
       logger.debug("debug log");
       logger.info("info log");
       logger.warn("warn log");
       logger.error("error log");
   }
}
