<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>첨삭 결과 - ${resumeFile.originalFilename}</title>
    <style>
        body { font-family: 'Segoe UI', Malgun Gothic, sans-serif; background: #f4f6f8; margin: 0; padding: 32px 16px; }
        .container { max-width: 1080px; margin: 0 auto; }
        .top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .top-bar h1 { font-size: 20px; margin: 0; }
        .top-bar a { color: #2563eb; text-decoration: none; font-size: 14px; margin-left: 16px; }
        .columns { display: flex; gap: 20px; flex-wrap: wrap; }
        .card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); flex: 1 1 420px; }
        .card h2 { font-size: 16px; margin-top: 0; }
        .original-text { white-space: pre-wrap; font-size: 14px; line-height: 1.6; color: #333; max-height: 640px; overflow-y: auto; }
        .overall-score { font-size: 40px; font-weight: bold; color: #2563eb; }
        .category { margin-top: 18px; }
        .category-label { display: flex; justify-content: space-between; font-size: 14px; font-weight: 600; margin-bottom: 4px; }
        .bar-bg { background: #e5e9ef; border-radius: 6px; height: 10px; overflow: hidden; }
        .bar-fill { background: #2563eb; height: 100%; }
        .comment { font-size: 13px; color: #555; margin-top: 6px; line-height: 1.5; }
        .highlight { background: #fff3a3; border-radius: 3px; padding: 0 1px; scroll-margin: 80px; }
        .highlight.active { background: #ffd400; }
        .suggestions { margin-top: 24px; }
        .suggestions h2 { font-size: 16px; }
        .suggestion-card { border: 1px solid #e5e9ef; border-radius: 10px; padding: 14px 16px; margin-top: 12px; cursor: pointer; transition: border-color .15s, background .15s; }
        .suggestion-card:hover, .suggestion-card.active { border-color: #2563eb; background: #f5f8ff; }
        .category-tag { display: inline-block; background: #eef2ff; color: #4338ca; font-size: 12px; font-weight: 600; padding: 2px 8px; border-radius: 12px; margin-bottom: 8px; }
        .suggestion-original { font-size: 13px; color: #555; background: #fafafa; border-left: 3px solid #fff3a3; padding: 6px 10px; margin-bottom: 8px; white-space: pre-wrap; }
        .suggestion-fix { font-size: 14px; color: #111; margin-bottom: 6px; }
        .suggestion-fix b { color: #2563eb; }
        .suggestion-reason { font-size: 13px; color: #777; }
        .empty-suggestions { font-size: 13px; color: #777; margin-top: 12px; }
        .status-badge { display: inline-block; padding: 4px 10px; border-radius: 20px; font-size: 13px; font-weight: 600; }
        .status-analyzing { background: #fff7e6; color: #a15c00; }
        .status-failed { background: #fdecec; color: #b42318; }
        .status-done { background: #e7f4ec; color: #1a7f47; }
        .btn { display: inline-block; margin-top: 20px; background: #2563eb; color: #fff; text-decoration: none; padding: 9px 18px; border-radius: 6px; font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <div class="top-bar">
        <h1>${resumeFile.originalFilename}</h1>
        <div>
            <a href="/upload">새로 업로드</a>
            <a href="/reviews">이력 목록</a>
        </div>
    </div>

    <c:choose>
        <c:when test="${status == 'ANALYZING'}">
            <div class="card">
                <span class="status-badge status-analyzing">분석 중</span>
                <p>AI가 아직 첨삭 중입니다. 잠시 후 새로고침 해주세요.</p>
            </div>
        </c:when>
        <c:when test="${status == 'FAILED'}">
            <div class="card">
                <span class="status-badge status-failed">분석 실패</span>
                <p>${feedback.overallComment}</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="columns">
                <div class="card">
                    <h2>원문</h2>
                    <div class="original-text" id="original-text">${highlightedText}</div>
                </div>
                <div class="card">
                    <span class="status-badge status-done">분석 완료</span>
                    <h2>종합 평가</h2>
                    <div class="overall-score">${feedback.overallScore}<span style="font-size:16px;color:#888;">/100</span></div>
                    <p class="comment">${feedback.overallComment}</p>

                    <div class="category">
                        <div class="category-label"><span>직무 적합성</span><span>${feedback.jobFitScore}점</span></div>
                        <div class="bar-bg"><div class="bar-fill" style="width:${feedback.jobFitScore}%;"></div></div>
                        <div class="comment">${feedback.jobFitComment}</div>
                    </div>
                    <div class="category">
                        <div class="category-label"><span>논리성</span><span>${feedback.logicScore}점</span></div>
                        <div class="bar-bg"><div class="bar-fill" style="width:${feedback.logicScore}%;"></div></div>
                        <div class="comment">${feedback.logicComment}</div>
                    </div>
                    <div class="category">
                        <div class="category-label"><span>문장력</span><span>${feedback.writingScore}점</span></div>
                        <div class="bar-bg"><div class="bar-fill" style="width:${feedback.writingScore}%;"></div></div>
                        <div class="comment">${feedback.writingComment}</div>
                    </div>
                    <div class="category">
                        <div class="category-label"><span>오탈자</span><span>${feedback.typoScore}점</span></div>
                        <div class="bar-bg"><div class="bar-fill" style="width:${feedback.typoScore}%;"></div></div>
                        <div class="comment">${feedback.typoComment}</div>
                    </div>

                    <a class="btn" href="/api/reviews/${resumeFile.fileId}/pdf">PDF로 다운로드</a>
                </div>
            </div>

            <div class="card suggestions">
                <h2>수정 제안</h2>
                <c:choose>
                    <c:when test="${empty suggestions}">
                        <p class="empty-suggestions">특별히 수정할 부분을 찾지 못했습니다.</p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="s" items="${suggestions}" varStatus="loop">
                            <div class="suggestion-card" id="sugg-${loop.index}" data-idx="${loop.index}">
                                <span class="category-tag">
                                    <c:choose>
                                        <c:when test="${s.category == 'jobFit'}">직무 적합성</c:when>
                                        <c:when test="${s.category == 'logic'}">논리성</c:when>
                                        <c:when test="${s.category == 'writing'}">문장력</c:when>
                                        <c:when test="${s.category == 'typo'}">오탈자</c:when>
                                        <c:otherwise>기타</c:otherwise>
                                    </c:choose>
                                </span>
                                <div class="suggestion-original">"<c:out value="${s.originalExcerpt}"/>"</div>
                                <div class="suggestion-fix"><b>수정 제안:</b> <c:out value="${s.suggestionText}"/></div>
                                <div class="suggestion-reason"><c:out value="${s.reason}"/></div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

            <script>
                (function () {
                    var cards = document.querySelectorAll('.suggestion-card');
                    cards.forEach(function (card) {
                        card.addEventListener('click', function () {
                            var idx = card.getAttribute('data-idx');
                            var mark = document.getElementById('mark-' + idx);
                            if (mark) {
                                mark.scrollIntoView({ behavior: 'smooth', block: 'center' });
                                document.querySelectorAll('.highlight.active').forEach(function (el) { el.classList.remove('active'); });
                                mark.classList.add('active');
                            }
                        });
                    });

                    document.querySelectorAll('.highlight').forEach(function (mark) {
                        mark.addEventListener('click', function () {
                            var idx = mark.getAttribute('data-idx');
                            var card = document.getElementById('sugg-' + idx);
                            if (card) {
                                card.scrollIntoView({ behavior: 'smooth', block: 'center' });
                                document.querySelectorAll('.suggestion-card.active').forEach(function (el) { el.classList.remove('active'); });
                                card.classList.add('active');
                            }
                        });
                    });
                })();
            </script>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
