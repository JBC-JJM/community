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
}