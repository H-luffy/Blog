package com.hjf.controller;

import com.hjf.entity.Article;
import com.hjf.entity.PageResult;
import com.hjf.entity.Result;
import com.hjf.entity.Tag;
import com.hjf.service.ArticleService;
import com.hjf.service.ArticleTagService;
import com.hjf.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private TagService tagService;

    /*
    *根据id查询文章
    * */
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id){
        Article article = articleService.getArticleById(id);
        if (article != null){
            return Result.success(article);
        }
        return Result.fail("文章不存在");
    }

    /*
    * 新增文章
    * */
    @PostMapping
    public Result<Void> add(@RequestBody(required = false) Article article){
        if (article == null) {
            return Result.fail("缺少文章数据");
        }
        articleService.add(article);
        return Result.success();
    }

    /*
    * 修改文章
    * */
    @PutMapping
    public Result<Void> update(@RequestBody(required = false) Article article){
        if (article == null) {
            return Result.fail("缺少文章数据");
        }
        articleService.update(article);
        return Result.success();
    }

    /*
    * 删除文章
    * */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        articleService.deleteById(id);
        return Result.success();
    }

    /*
    * 分页查询
    * */
    @GetMapping
    public Result<PageResult> page(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10")Integer pageSize,
                                   String title){
        PageResult pageResult = articleService.page(page,pageSize, title);
        return Result.success(pageResult);

    }

    /*
    * 获取所有标签
    * */
    @GetMapping("/tags")
    public Result<List<Tag>> getAllTags(){
        List<Tag> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /*
    * 根据标签查询文章
    * */
    @GetMapping("/by-tag")
    public Result<PageResult> getByTag(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam String tag) {
        PageResult pageResult = articleService.getByTag(page, pageSize, tag);
        return Result.success(pageResult);
    }
}
