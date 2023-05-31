package com.nowcoder.community.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    public static String generateUUID(){

//        生成指定长度的字母和数字组成的随机组合字符串
//        https://blog.csdn.net/wang_jing_jing/article/details/119182699
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**springBoot整合了md5
     * @param key: password+salt
     * @return
     */
    public static String MD5(String key){
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}