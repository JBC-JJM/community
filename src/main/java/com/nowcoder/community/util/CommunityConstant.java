package com.nowcoder.community.util;


public interface CommunityConstant {
    /**
     * 激活成功
     */
    static final int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    static final int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    static final int ACTIVATION_FAILURE = 2;

    /**
     * 默认登入凭证的超时时间
     */
    static final int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选记住我的登入凭证的超时时间
     */
    static final int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 评论对象实体类型: 帖子
     */
    static final int ENTITY_TYPE_POST = 1;

    /**
     * 评论对象实体类型: 评论
     */
    static final int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型: 用户
     */
    static final int ENTITY_TYPE_USER = 3;

    /**
     * 主题: 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题: 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题: 关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题: 发帖（更新帖子）
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 主题: 删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 主题: 分享（生成长图）
     */
    String TOPIC_SHARE = "share";

    /**
     * 系统用户ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限: 普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限: 管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限: 版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}