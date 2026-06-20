package com.hjf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/*
* 分页结果类
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {
    private Long total; //总记录数
    private List rows; //当前页数据列表
}
