package com.nowcoder.community.service.impl;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;


import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    // 帖子列表缓存
    @Autowired
    private LoadingCache postListCache;

    // 帖子总数缓存
    @Autowired
    private LoadingCache postRowsCache;

    @Override
    public List<DiscussPost> selectAll(Integer userId) {
        return discussPostMapper.selectAll(userId);
    }

    @Override
    public List<DiscussPost> discussPosts(Integer userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {//缓存热门帖子:orderMode == 1
            return (List<DiscussPost>) postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB.");//没有缓存
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    @Override
    public int discussPostsRows(Integer userId) {
        if (userId == 0) {
            return (int) postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");//没有缓存
        return discussPostMapper.selectDiscussPostsRows(userId);
    }


    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public int insertPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new RuntimeException("数据为空");
        }

        //转义xml语言
        String title = HtmlUtils.htmlEscape(discussPost.getTitle());
        String content = HtmlUtils.htmlEscape(discussPost.getContent());
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(title));
        discussPost.setContent(sensitiveFilter.filter(content));
        return discussPostMapper.insert(discussPost);
    }

    @Override
    public DiscussPost findPostById(Integer id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(Integer id, Integer commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    @Override
    public int updateType(Integer id, Integer type) {
        return discussPostMapper.updateType(id, type);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        return discussPostMapper.updateStatus(id, status);
    }

    @Override
    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }
}
