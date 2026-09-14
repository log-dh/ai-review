package com.example.aireview.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.aireview.domain.ResumeText;

@Mapper
public interface ResumeTextMapper {

    void insert(ResumeText resumeText);

    ResumeText findByFileId(@Param("fileId") Long fileId);
}
