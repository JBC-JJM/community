package com.nowcoder.community.service;


import com.nowcoder.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    //    查询所有帖子,userId不为null则查询某个用户的帖子，需要注意的是userId是string，默认屏蔽拉雷的
    List<DiscussPost> selectAll(Integer userId);
    //    查询一页数据，userId默认为0，支持查询某个用户的帖子
    List<DiscussPost> discussPosts(Integer userId, int offset, int limit);
    //    查询总的数量，userId默认为0，支持查询某个用户的帖子的
    int discussPostsRows(Integer userId);

    //插入评论，由过滤xml和敏感词的功能
    int insertPost(DiscussPost discussPost);

    DiscussPost findPostById(Integer id);
}
