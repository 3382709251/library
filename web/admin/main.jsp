<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object loginAdmin = session.getAttribute("loginAdmin");
    if (loginAdmin == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>管理员后台 - 图书借阅管理系统</title>
    <style>
        /* 与读者端完全一致的样式（直接复制你reader/main.jsp的top-nav样式） */
        * { margin:0; padding:0; box-sizing:border-box; font-family:"微软雅黑",sans-serif; }
        .top-nav { height:60px; background:#fff; border-bottom:1px solid #eee; box-shadow:0 2px 5px rgba(0,0,0,0.1);
            display:flex; align-items:center; padding:0 20px; position:sticky; top:0; z-index:999; }
        .nav-menu a { margin:0 20px; padding:8px 12px; color:#333; text-decoration:none; border-radius:4px; }
        .nav-menu a:hover, .nav-menu a.active { background:#67c23a; color:#fff; }
        .main-content { width:1200px; margin:20px auto; display:flex; gap:20px; }
        .side-menu { width:220px; background:#fff; border:1px solid #eee; border-radius:8px; padding:15px; }
        .side-menu a { display:block; padding:12px; color:#333; text-decoration:none; border-radius:4px; }
        .side-menu a:hover, .side-menu a.active { background:#67c23a; color:#fff; }
        .content { flex:1; background:#fff; border:1px solid #eee; border-radius:8px; padding:20px; }
    </style>
</head>
<body>
<div class="top-nav">
    <div class="nav-menu">
        <a href=" " class="active">首页</a >
        <a href="${pageContext.request.contextPath}/admin/readerList.jsp">读者管理</a >
        <a href="${pageContext.request.contextPath}/admin/bookList.jsp">图书管理</a >
        <a href="${pageContext.request.contextPath}/logout" style="color:#e74c3c; margin-left:20px;">退出登录</a >
    </div>
</div>

<div class="main-content">
    <div class="side-menu">
        <a href="readerList.jsp" class="active">📋 读者信息管理</a >
        <a href="bookList.jsp">📚 图书信息管理</a >
        <a href="#">⏰ 借阅超期管理</a >
    </div>
    <div class="content">
        <h2>欢迎回来，管理员！</h2>
        <p>当前读者总数：<strong id="readerCount">加载中...</strong></p >
        <p>当前图书总数：<strong id="bookCount">加载中...</strong></p >
    </div>
</div>

<script>
    // 这里后续可以加统计接口
    console.log("管理员首页加载完成");
</script>
</body>
</html>