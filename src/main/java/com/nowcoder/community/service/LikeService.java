package com.nowcoder.community.service;

public interface LikeService {
    /**
     * 用户点赞
     * @param userId
     * @param entityType 帖子/评论
     * @param entityId 具体id
     */
    public void like(int userId,int entityType,int entityId);

    /**
     * @param entityType
     * @param entityId
     * @return 点赞数量
     */
    public long findEntityLikeCount(int entityType,int entityId);

    /**
     * 某用户的点赞状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return 1：赞，0：没有
     */
    public int findEntityLikeStatus(int userId,int entityType,int entityId);

}
