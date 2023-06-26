package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//帖子
@Mapper
public interface DiscussPostMapper {
//    查询所有帖子
    //使用了动态sql，发现没有Param，当个参数居然找不到
    List<DiscussPost> selectAll(@Param("userId") Integer userId);
//    查询一页数据，userId默认为空，支持查询某个用户的帖子
    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
//    查询总的数量，userId默认为空，支持查询某个用户的帖子的
    int selectDiscussPostsRows(@Param("userId") Integer userId);

    int insert (DiscussPost discussPost);

    DiscussPost selectDiscussPostById(Integer id);

    /**
     * 更新帖子的评论数量，不包括帖子评论的回复
     * @param id 帖子的id
     * @return
     */
    int updateCommentCount(@Param("id") Integer id, @Param("commentCount") Integer commentCount);

    int updateType(Integer id,Integer type);

    int updateStatus(Integer id,Integer status);
}
