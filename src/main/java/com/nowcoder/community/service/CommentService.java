package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentService {

    Comment findCommentById(Integer commentId);

    /**
     * @param entityType 评论的对象：帖子/评论的评论
     * @param entityId   评论的帖子的id
     * @return
     */
    List<Comment> selectCommentByEntity(Integer entityType,
                                        Integer entityId,
                                        Integer offset,
                                        Integer limit);

    /**
     * @param entityType 评论的对象：帖子/评论的评论
     * @param entityId   评论的帖子的id
     * @return
     */
    int selectCountByEntity(Integer entityType,
                            Integer entityId);

    int addComment(Comment comment);
}
