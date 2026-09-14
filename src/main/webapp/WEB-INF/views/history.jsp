<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>첨삭 이력</title>
    <style>
        body { font-family: 'Segoe UI', Malgun Gothic, sans-serif; background: #f4f6f8; margin: 0; padding: 32px 16px; }
        .container { max-width: 900px; margin: 0 auto; background: #fff; border-radius: 12px; padding: 28px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
        .top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .top-bar h1 { font-size: 20px; margin: 0; }
        .top-bar a { color: #2563eb; text-decoration: none; font-size: 14px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { text-align: left; padding: 10px 8px; border-bottom: 1px solid #eee; font-size: 14px; }
        th { color: #888; font-weight: 600; font-size: 13px; }
        tr:hover { background: #fafbfc; }
        .score { font-weight: 700; }
        .badge { padding: 2px 8px; border-radius: 12px; font-size: 12px; }
        .badge-analyzing { background: #fff7e6; color: #a15c00; }
        .badge-failed { background: #fdecec; color: #b42318; }
        .badge-done { background: #e7f4ec; color: #1a7f47; }
        .empty { color: #888; text-align: center; padding: 40px 0; }
        .pagination { margin-top: 20px; text-align: center; }
        .pagination a, .pagination span { display: inline-block; padding: 6px 12px; margin: 0 2px; border-radius: 6px; font-size: 14px; text-decoration: none; color: #333; }
        .pagination a:hover { background: #eef2ff; }
        .pagination .current { background: #2563eb; color: #fff; }
    </style>
</head>
<body>
<div class="container">
    <div class="top-bar">
        <h1>첨삭 이력</h1>
        <a href="/upload">새로 업로드</a>
    </div>

    <c:choose>
        <c:when test="${empty items}">
            <div class="empty">업로드된 파일이 없습니다.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                <tr>
                    <th>파일명</th>
                    <th>업로드일</th>
                    <th>종합점수</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td>${item.originalFilename}</td>
                        <td>${item.uploadedAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${item.overallScore == null}">
                                    <span class="badge badge-analyzing">분석 중</span>
                                </c:when>
                                <c:when test="${item.overallScore == -1}">
                                    <span class="badge badge-failed">실패</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="score">${item.overallScore}</span>점
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><a href="/reviews/${item.fileId}">상세보기</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:choose>
                        <c:when test="${p == currentPage}">
                            <span class="current">${p}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="/reviews?page=${p}">${p}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
