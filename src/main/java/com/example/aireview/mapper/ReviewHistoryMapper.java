package com.example.aireview.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.aireview.dto.ReviewHistoryItem;

@Mapper
public interface ReviewHistoryMapper {

    List<ReviewHistoryItem> findPage(@Param("offset") int offset, @Param("size") int size);

    long countAll();
}
