package com.hjf.mapper;


import com.hjf.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import tools.jackson.core.ObjectReadContext;

import java.util.List;

@Mapper
public interface ArticleMapper{

    /*
    * 查询所有文章
    * */
    List<Article> selectList();

    /*
    * 根据id查询文章
    * */
    Article selectById(Long id);

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
    void deleteById(Long  id);

    /*
    * 查询文章总数
    * */
    Long count();

    List<Article> list(String  title);

    List<Article> getByTagName(String tag);
}
