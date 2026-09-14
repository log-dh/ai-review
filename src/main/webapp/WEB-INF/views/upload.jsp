<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>자소서 첨삭 - 업로드</title>
    <style>
        body { font-family: 'Segoe UI', Malgun Gothic, sans-serif; background: #f4f6f8; margin: 0; padding: 40px 16px; }
        .container { max-width: 640px; margin: 0 auto; background: #fff; border-radius: 12px; padding: 32px; box-shadow: 0 2px 10px rgba(0,0,0,0.06); }
        h1 { font-size: 22px; margin-bottom: 4px; }
        p.desc { color: #666; margin-top: 0; margin-bottom: 24px; }
        .nav-link { float: right; font-size: 14px; }
        .drop-zone { border: 2px dashed #c7cdd4; border-radius: 8px; padding: 32px; text-align: center; color: #667; }
        input[type=file] { margin-top: 12px; }
        button { margin-top: 20px; background: #2563eb; color: #fff; border: none; padding: 10px 22px; border-radius: 6px; font-size: 15px; cursor: pointer; }
        button:disabled { background: #9db3d8; cursor: not-allowed; }
        #progressArea { display: none; margin-top: 24px; }
        .spinner { width: 22px; height: 22px; border: 3px solid #dbe3ee; border-top-color: #2563eb; border-radius: 50%; display: inline-block; animation: spin 0.8s linear infinite; vertical-align: middle; margin-right: 8px; }
        @keyframes spin { to { transform: rotate(360deg); } }
        .progress-bar-bg { background: #e5e9ef; border-radius: 6px; height: 10px; margin-top: 12px; overflow: hidden; }
        .progress-bar-fill { background: #2563eb; height: 100%; width: 0%; transition: width 0.4s ease; }
        #errorArea { display: none; margin-top: 20px; padding: 12px 16px; background: #fdecec; color: #b42318; border-radius: 8px; }
        #statusText { font-size: 14px; color: #444; }
    </style>
</head>
<body>
<div class="container">
    <a class="nav-link" href="/reviews">이력 보기</a>
    <h1>자기소개서 AI 첨삭</h1>
    <p class="desc">PDF 또는 DOCX 파일을 업로드하면 직무적합성 · 논리성 · 문장력 · 오탈자를 AI가 분석해드립니다.</p>

    <form id="uploadForm">
        <div class="drop-zone">
            <div>PDF / DOCX 파일 선택 (최대 20MB)</div>
            <input type="file" id="fileInput" name="file" accept=".pdf,.docx" required>
        </div>
        <button type="submit" id="submitBtn">업로드 및 분석 시작</button>
    </form>

    <div id="progressArea">
        <span class="spinner"></span><span id="statusText">업로드 중...</span>
        <div class="progress-bar-bg"><div class="progress-bar-fill" id="progressBar"></div></div>
    </div>

    <div id="errorArea"></div>
</div>

<script>
    const form = document.getElementById('uploadForm');
    const submitBtn = document.getElementById('submitBtn');
    const progressArea = document.getElementById('progressArea');
    const progressBar = document.getElementById('progressBar');
    const statusText = document.getElementById('statusText');
    const errorArea = document.getElementById('errorArea');

    function showError(message) {
        errorArea.style.display = 'block';
        errorArea.textContent = message;
        progressArea.style.display = 'none';
        submitBtn.disabled = false;
    }

    form.addEventListener('submit', function (e) {
        e.preventDefault();
        errorArea.style.display = 'none';
        const fileInput = document.getElementById('fileInput');
        if (!fileInput.files.length) {
            return;
        }

        submitBtn.disabled = true;
        progressArea.style.display = 'block';
        statusText.textContent = '업로드 중...';
        progressBar.style.width = '20%';

        const formData = new FormData();
        formData.append('file', fileInput.files[0]);

        fetch('/api/files', { method: 'POST', body: formData })
            .then(function (res) {
                if (!res.ok) {
                    return res.json().then(function (err) { throw new Error(err.message || '업로드에 실패했습니다.'); });
                }
                return res.json();
            })
            .then(function (data) {
                statusText.textContent = 'AI가 첨삭 중입니다...';
                progressBar.style.width = '60%';
                pollStatus(data.fileId);
            })
            .catch(function (err) {
                showError(err.message);
            });
    });

    function pollStatus(fileId) {
        fetch('/api/reviews/' + fileId + '/status')
            .then(function (res) { return res.json(); })
            .then(function (data) {
                if (data.status === 'DONE') {
                    progressBar.style.width = '100%';
                    statusText.textContent = '완료! 결과 페이지로 이동합니다...';
                    setTimeout(function () { window.location.href = '/reviews/' + fileId; }, 600);
                } else if (data.status === 'FAILED') {
                    showError('AI 첨삭에 실패했습니다: ' + (data.message || '알 수 없는 오류'));
                } else {
                    setTimeout(function () { pollStatus(fileId); }, 2000);
                }
            })
            .catch(function () {
                setTimeout(function () { pollStatus(fileId); }, 2000);
            });
    }
</script>
</body>
</html>
