package com.nowcoder.community.service.impl;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> selectAll(Integer userId) {
        return discussPostMapper.selectAll(userId);
    }

    @Override
    public List<DiscussPost> discussPosts(Integer userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int discussPostsRows(Integer userId) {
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
        String content =HtmlUtils.htmlEscape(discussPost.getContent());
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(title));
        discussPost.setContent(sensitiveFilter.filter(content));
        return discussPostMapper.insert(discussPost);
    }

    @Override
    public DiscussPost findPostById(Integer id) {
        return discussPostMapper.selectDiscussPostById(id);
    }
}
