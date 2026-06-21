package com.hjf.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hjf.entity.Article;
import com.hjf.entity.ArticleTag;
import com.hjf.entity.PageResult;
import com.hjf.entity.Tag;
import com.hjf.mapper.ArticleMapper;
import com.hjf.service.ArticleService;
import com.hjf.service.ArticleTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = articleMapper.selectList();
        return articles;
    }

    @Override
    public Article getArticleById(Long id) {
        Article article = articleMapper.selectById(id);
        // 查询标签
        List<Tag> tags = articleTagService.getTagsByArticleId(id);
        article.setTags(tags);
        return article;
    }

    @Override
    public void add(Article article) {
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.add(article);
        // 保存标签关联
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            // 获取刚插入的文章ID
            Long articleId = article.getId();
            List<Long> tagIds = article.getTags().stream()
                    .map(Tag::getId)
                    .toList();
            articleTagService.saveTags(articleId, tagIds);
        }
    }

    @Override
    public void update(Article article) {
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
        // 更新标签关联
        if (article.getTags() != null) {
            List<Long> tagIds = article.getTags().stream()
                    .map(Tag::getId)
                    .toList();
            articleTagService.saveTags(article.getId(), tagIds);
        }
    }

    @Override
    public void deleteById(Long id) {
        // 先删除标签关联
        articleTagService.removeByArticleId(id);
        articleMapper.deleteById(id);
    }

    @Override
    public PageResult page(Integer page, Integer pageSize, String title) {
        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.list(title);
        Page<Article> p = (Page<Article>) articleList;
        for (Article article : articleList){
            List<Tag> tags = articleTagService.getTagsByArticleId(article.getId());
            article.setTags(tags);
        }
        return new PageResult(p.getTotal(), p.getResult());
    }

    /*
    * 根据标签查询文章
    * */
    @Override
    public PageResult getByTag(Integer page, Integer pageSize, String tag) {
        PageHelper.startPage(page, pageSize);
        List<Article> articleList = articleMapper.getByTagName(tag);
        Page<Article> p = (Page<Article>) articleList;
        // 给每篇文章设置标签
        for (Article article : p.getResult()) {
            List<Tag> tags = articleTagService.getTagsByArticleId(article.getId());
            article.setTags(tags);
        }
        return new PageResult(p.getTotal(), p.getResult());
    }

}