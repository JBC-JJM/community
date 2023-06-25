package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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


    //快速转json
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
    }
}