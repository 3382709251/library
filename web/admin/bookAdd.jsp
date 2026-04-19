<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("loginAdmin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>新增图书 - 管理员后台</title>
    <style>
        * { margin:0; padding:0; box-sizing:border-box; font-family:"微软雅黑",sans-serif; }
        .top-nav { height:60px; background:#fff; border-bottom:1px solid #eee; box-shadow:0 2px 5px rgba(0,0,0,0.1);
            display:flex; align-items:center; padding:0 20px; position:sticky; top:0; z-index:999; }
        .nav-menu a { margin:0 20px; padding:8px 12px; color:#333; text-decoration:none; border-radius:4px; }
        .nav-menu a:hover, .nav-menu a.active { background:#67c23a; color:#fff; }
        .main-content { width:1200px; margin:30px auto; }
        .card { background:#fff; border:1px solid #eee; border-radius:8px; padding:30px; max-width:800px; margin:0 auto; }
        .form-group { margin-bottom:20px; }
        label { display:block; margin-bottom:8px; font-weight:bold; }
        input, select, textarea { width:100%; padding:10px; border:1px solid #ddd; border-radius:4px; font-size:14px; }
        .btn { padding:12px 30px; border:none; border-radius:4px; cursor:pointer; font-size:16px; }
        .btn-primary { background:#67c23a; color:#fff; }
        .btn-cancel { background:#999; color:#fff; margin-left:10px; }
        .preview-img { max-width:200px; max-height:200px; margin-top:10px; border:1px solid #ddd; }
    </style>
</head>
<body>
<div class="top-nav">
    <div class="nav-menu">
        <a href=" ">首页</a >
        <a href="${pageContext.request.contextPath}/admin/readerList.jsp">读者管理</a >
        <a href="${pageContext.request.contextPath}/admin/bookList.jsp">图书管理</a >
        <a href="${pageContext.request.contextPath}/logout" style="color:#e74c3c; margin-left:20px;">退出登录</a >
    </div>
</div>

<div class="main-content">
    <div class="card">
        <h2>新增图书</h2>
        <form id="addBookForm" enctype="multipart/form-data">
            <div class="form-group">
                <label>图书ID（唯一）</label>
                <input type="text" id="bookId" name="bookId" required>
            </div>
            <div class="form-group">
                <label>ISBN</label>
                <input type="text" id="isbn" name="isbn">
            </div>
            <div class="form-group">
                <label>书名</label>
                <input type="text" id="bookName" name="bookName" required>
            </div>
            <div class="form-group">
                <label>作者</label>
                <input type="text" id="author" name="author" required>
            </div>
            <div class="form-group">
                <label>出版社</label>
                <input type="text" id="publisher" name="publisher">
            </div>
            <div class="form-group">
                <label>出版年份</label>
                <input type="number" id="publishYear" name="publishYear">
            </div>
            <div class="form-group">
                <label>图书类型</label>
                <input type="text" id="bookType" name="bookType" placeholder="例如：小说、科技、教育">
            </div>
            <div class="form-group">
                <label>总数量</label>
                <input type="number" id="totalCount" name="totalCount" value="1" required>
            </div>
            <div class="form-group">
                <label>封面图片</label>
                <input type="file" id="bookImage" name="bookImage" accept="image/*">
                <div id="imagePreview"></div>
            </div>

            <div style="margin-top:30px;">
                <button type="button" class="btn btn-primary" onclick="submitAddBook()">保存新增</button>
                <button type="button" class="btn btn-cancel" onclick="history.back()">取消</button>
            </div>
        </form>
    </div>
</div>

<script>
    const basePath = '${pageContext.request.contextPath}';

    // 图片预览
    document.getElementById('bookImage').addEventListener('change', function(e) {
        const preview = document.getElementById('imagePreview');
        preview.innerHTML = '';
        if (e.target.files && e.target.files[0]) {
            const reader = new FileReader();
            reader.onload = function(ev) {
                preview.innerHTML = `< img src="${ev.target.result}" class="preview-img">`;
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });

    function submitAddBook() {
        const formData = new FormData();
        formData.append("bookId", document.getElementById("bookId").value);
        formData.append("isbn", document.getElementById("isbn").value);
        formData.append("bookName", document.getElementById("bookName").value);
        formData.append("author", document.getElementById("author").value);
        formData.append("publisher", document.getElementById("publisher").value);
        formData.append("publishYear", document.getElementById("publishYear").value);
        formData.append("bookType", document.getElementById("bookType").value);
        formData.append("totalCount", document.getElementById("totalCount").value);

        const imageFile = document.getElementById("bookImage").files[0];
        if (imageFile) formData.append("bookImage", imageFile);

        const xhr = new XMLHttpRequest();
        xhr.open("POST", basePath + "/admin/bookAdd", true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    alert("新增图书成功！");
                    window.location.href = basePath + "/admin/bookList.jsp";
                } else {
                    alert("新增失败：" + xhr.responseText);
                }
            }
        };
        xhr.send(formData);
    }
</script>
</body>
</html>