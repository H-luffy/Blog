package com.hjf.service;

import com.hjf.entity.ArticleTag;
import com.hjf.entity.Tag;
import java.util.List;

public interface ArticleTagService {
    void saveTags(Long articleId, List<Long> tagIds);
    void removeByArticleId(Long articleId);
    List<Tag> getTagsByArticleId(Long articleId);
}