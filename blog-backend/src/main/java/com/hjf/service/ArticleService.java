package com.hjf.service;

import com.hjf.entity.Article;
import com.hjf.entity.PageResult;

import java.util.List;

public interface ArticleService {
    /*
    * 查询所有文章
    * */
    List<Article> getAllArticles();

    /*
    * 根据ID查询文章
    * */
    Article getArticleById(Long id);

    /*
    * 新增文章
    * */
    void add(Article article);

    /*
    * 修改文章
    * */
    void update(Article article);

    /*
    * 删除文章
    * */
    void deleteById(Long id);

    /*
    * 分页查询
    * */
    PageResult page(Integer page, Integer pageSize, String title);

    PageResult getByTag(Integer page, Integer pageSize, String tag);
}
