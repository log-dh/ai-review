package com.example.aireview.service;

import java.util.List;

import com.example.aireview.domain.Feedback;
import com.example.aireview.domain.Suggestion;

public record ParsedFeedback(Feedback feedback, List<Suggestion> suggestions) {
}
