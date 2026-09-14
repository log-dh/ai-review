package com.example.aireview.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.aireview.domain.Feedback;

@Mapper
public interface FeedbackMapper {

    void insert(Feedback feedback);

    Feedback findByFileId(@Param("fileId") Long fileId);
}
