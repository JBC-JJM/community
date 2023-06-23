package com.nowcoder.community.service;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.domain.Page;


public interface ElasticsearchService {

    public void saveDiscussPost(DiscussPost post);

    public void delete(int id);

    /**
     * es 分页搜索帖子
     * @param keyWord 关键字
     * @param current 当前页，es以0开始
     * @param limit
     * @return es实现的page对象，可以遍历，已高亮部分内容
     */
    public Page<DiscussPost> searchDiscussPost(String keyWord, int current, int limit);
}
