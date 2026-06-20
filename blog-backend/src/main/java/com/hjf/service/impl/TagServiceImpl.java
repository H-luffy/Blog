package com.hjf.service.impl;

import com.hjf.entity.Tag;
import com.hjf.mapper.TagMapper;
import com.hjf.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getAllTags() {
        return tagMapper.selectAll();
    }
}