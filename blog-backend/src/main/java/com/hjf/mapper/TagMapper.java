package com.hjf.mapper;

import com.hjf.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper {
    List<Tag> selectAll();
    Tag selectById(Long id);
    void insert(Tag tag);
    void deleteById(Long id);
}
