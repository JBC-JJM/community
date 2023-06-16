package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//评论
@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(@Param("entityType") Integer entityType,
                                        @Param("entityId") Integer entityId,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    int selectCountByEntity(@Param("entityType") Integer entityType,
                            @Param("entityId") Integer entityId);

    int insertComment(Comment comment);
}
