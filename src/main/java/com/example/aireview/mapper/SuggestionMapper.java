package com.example.aireview.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.aireview.domain.Suggestion;

@Mapper
public interface SuggestionMapper {

    void insertAll(@Param("suggestions") List<Suggestion> suggestions);

    List<Suggestion> findByFeedbackId(@Param("feedbackId") Long feedbackId);
}
