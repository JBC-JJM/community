package com.nowcoder.community.service.impl;

import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Override
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    @Override
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //得到时间区间
        long startMillis = start.getTime();//转为时间戳
        long EndMillis = end.getTime();
        List<String> keyList = new ArrayList<>();

        while (startMillis <= EndMillis) {
            String key = RedisKeyUtil.getUVKey(dateFormat.format(startMillis));//转为日期
            keyList.add(key);
            startMillis = startMillis + 24 * 3600 * 1000;
        }

        String redisKey = RedisKeyUtil.getUVKey(dateFormat.format(start), dateFormat.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    @Override
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    @Override
    public long calculateDAU(Date start, Date end) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        //得到时间区间
        long startMillis = start.getTime();//转为时间戳
        long EndMillis = end.getTime();
        List<byte[]> keyList = new ArrayList<>();

        while (startMillis <= EndMillis) {
            String key = RedisKeyUtil.getDAUKey(dateFormat.format(startMillis));//转为日期
            keyList.add(key.getBytes());
            startMillis = startMillis + 24 * 3600 * 1000;//加一天
        }

        // 进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(dateFormat.format(start), dateFormat.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });

    }
}
