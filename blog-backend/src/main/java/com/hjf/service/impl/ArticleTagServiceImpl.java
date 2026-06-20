package com.hjf.service.impl;

import com.hjf.entity.ArticleTag;
import com.hjf.entity.Tag;
import com.hjf.mapper.ArticleTagMapper;
import com.hjf.mapper.TagMapper;
import com.hjf.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleTagServiceImpl implements ArticleTagService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Override
    public void saveTags(Long articleId, List<Long> tagIds) {
        // 先删除旧关联
        articleTagMapper.deleteByArticleId(articleId);
        // 再插入新关联
        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                ArticleTag at = new ArticleTag(articleId, tagId);
                articleTagMapper.insert(at);
            }
        }
    }

    @Override
    public void removeByArticleId(Long articleId) {
        articleTagMapper.deleteByArticleId(articleId);
    }

    @Override
    public List<Tag> getTagsByArticleId(Long articleId) {
        List<ArticleTag> list = articleTagMapper.selectByArticleId(articleId);
        return list.stream()
                .map(at -> tagMapper.selectById(at.getTagId()))
                .collect(Collectors.toList());
    }
}