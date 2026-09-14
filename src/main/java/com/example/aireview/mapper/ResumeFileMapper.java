package com.example.aireview.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.aireview.domain.ResumeFile;

@Mapper
public interface ResumeFileMapper {

    void insert(ResumeFile resumeFile);

    ResumeFile findById(@Param("fileId") Long fileId);
}
