package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户接收的赞
    // like:user:userId -> int
    private static final String PREFIX_USER_LIKE = "like:user";

    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    private static final String PREFIX_FOLLOWEE = "followee";

    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }


    // 某个实体（帖子/用户）拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    private static final String PREFIX_FOLLOWER = "follower";

    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";

    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登入凭证
    private static final String PREFIX_TICKET = "ticket";

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }


    //缓存用户
    private static final String PREFIX_USER = "user";

    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
