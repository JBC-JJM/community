package com.nowcoder.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    /**
     * 用户关注某个实体,同时实体被关注
     *
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(int userId, int entityType, int entityId);

    /**
     * 用户取消某个实体,同时实体被取消关注
     *
     * @param entityType
     * @param entityId
     * @param userId
     */
    public void unFollow(int entityType, int entityId, int userId);

    /**
     * 该用户关注某实体的的总数
     *
     * @param userId
     * @param entityType
     * @return
     */
    public long findFolloweeCount(int userId, int entityType);

    /**
     * 某实体(帖子/用户)被关注的总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityType, int entityId);

    /**
     * 用户是否已关注某实体
     *
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean hasFollowed(int userId, int entityType, int entityId);

    /**
     * 用户关注的人列表
     *
     * @param userId     当前用户
     * @param entityType 关注的类型
     * @param offset
     * @param limit
     * @return 关注的用户列表
     */
    public List<Map<String, Object>> findFollowees(int userId, int entityType, int offset, int limit);

    /**
     * 粉丝列表
     *
     * @param userId     up
     * @param entityType up的类型
     * @param offset
     * @param limit
     * @return 粉丝列表
     */
    public List<Map<String, Object>> findFollowers(int userId, int entityType, int offset, int limit);
}
