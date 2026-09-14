package com.example.aireview.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.example.aireview.domain.Suggestion;

/** 원문 텍스트에서 각 수정 제안이 발췌한 구절을 찾아 &lt;mark&gt; 태그로 감싼 안전한 HTML을 생성한다. */
@Service
public class TextHighlighter {

    public String highlight(String text, List<Suggestion> suggestions) {
        if (text == null) {
            return "";
        }
        if (suggestions == null || suggestions.isEmpty()) {
            return HtmlUtils.htmlEscape(text);
        }

        List<int[]> ranges = findNonOverlappingRanges(text, suggestions);
        if (ranges.isEmpty()) {
            return HtmlUtils.htmlEscape(text);
        }

        StringBuilder html = new StringBuilder();
        int cursor = 0;
        for (int[] range : ranges) {
            int start = range[0];
            int end = range[1];
            int idx = range[2];
            html.append(HtmlUtils.htmlEscape(text.substring(cursor, start)));
            html.append("<mark class=\"highlight\" id=\"mark-").append(idx).append("\" data-idx=\"").append(idx).append("\">");
            html.append(HtmlUtils.htmlEscape(text.substring(start, end)));
            html.append("</mark>");
            cursor = end;
        }
        html.append(HtmlUtils.htmlEscape(text.substring(cursor)));
        return html.toString();
    }

    private List<int[]> findNonOverlappingRanges(String text, List<Suggestion> suggestions) {
        List<int[]> ranges = new ArrayList<>();
        for (int i = 0; i < suggestions.size(); i++) {
            String excerpt = suggestions.get(i).getOriginalExcerpt();
            if (excerpt == null || excerpt.isBlank()) {
                continue;
            }
            int searchFrom = 0;
            int matchStart = -1;
            while (true) {
                int candidate = text.indexOf(excerpt, searchFrom);
                if (candidate < 0) {
                    break;
                }
                if (!overlapsExisting(ranges, candidate, candidate + excerpt.length())) {
                    matchStart = candidate;
                    break;
                }
                searchFrom = candidate + 1;
            }
            if (matchStart >= 0) {
                ranges.add(new int[]{matchStart, matchStart + excerpt.length(), i});
            }
        }
        ranges.sort((a, b) -> Integer.compare(a[0], b[0]));
        return ranges;
    }

    private boolean overlapsExisting(List<int[]> ranges, int start, int end) {
        for (int[] range : ranges) {
            if (start < range[1] && end > range[0]) {
                return true;
            }
        }
        return false;
    }
}
