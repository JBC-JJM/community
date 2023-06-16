package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.nowcoder.community.util.CommunityConstant.ENTITY_TYPE_POST;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int selectCountByEntity(Integer entityType, Integer entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new RuntimeException("评论为空");
        }

        String htmlEscape = HtmlUtils.htmlEscape(comment.getContent());//过滤xml
        String contentAfterFilter = sensitiveFilter.filter(htmlEscape);//过滤敏感词
        comment.setContent(contentAfterFilter);

        int rows = commentMapper.insertComment(comment);
        //假如评论的是帖子,更新帖子表的评论数
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost post = discussPostService.findPostById(comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), post.getCommentCount() + rows);
        }
        return rows;
    }
}
