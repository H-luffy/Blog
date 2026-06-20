package com.hjf.mapper;

import com.hjf.entity.Syster;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysterMapper {
    Syster getUsernameAndPassword(Syster syster);
}
