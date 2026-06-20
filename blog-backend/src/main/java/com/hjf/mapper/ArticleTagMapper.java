package com.hjf.mapper;

import com.hjf.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ArticleTagMapper {
    void insert(ArticleTag articleTag);
    void deleteByArticleId(Long articleId);
    List<ArticleTag> selectByArticleId(Long articleId);
    List<ArticleTag> selectByTagId(Long tagId);
}