<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图书馆管理系统-登录</title>
    <%--
        引入CSS样式文件：
        ${pageContext.request.contextPath}：动态获取项目上下文路径（避免写死路径导致404）
        /css/common.css：项目内CSS文件的相对路径，确保不同部署环境下样式都能加载
    --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
</head>
<body>
<!-- 登录框容器，用于样式统一封装 -->
<div class="login-box">
    <h2>系统登录</h2>
    <%--
        登录失败提示：
        后端Servlet通过request.setAttribute("error", "提示信息")传递错误信息
        此处判断若error不为空，则显示错误提示（如用户名密码错误、空值等）
    --%>
    <% if (request.getAttribute("error") != null) { %>
    <div class="error-tip"><%= request.getAttribute("error") %></div>
    <% } %>

    <%--
        登录表单：
        action：表单提交的目标地址，拼接上下文路径+Servlet映射路径（/login），确保请求能精准匹配到LoginServlet
        method="post"：POST请求更安全，避免参数暴露在URL中，且能传输中文（配合Servlet的UTF-8编码）
    --%>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <!-- 用户名输入项 -->
        <div class="form-item">
            <label>用户名：</label>
            <!--
                name="username"：后端通过request.getParameter("username")获取该值
                placeholder：输入提示
                required：HTML5前端非空校验，减少无效请求
            -->
            <input type="text" name="username" placeholder="请输入用户名" required>
        </div>
        <!-- 密码输入项 -->
        <div class="form-item">
            <label>密&nbsp;&nbsp;&nbsp;码：</label>
            <!--
                type="password"：密码框，输入内容隐藏
                name="password"：后端通过request.getParameter("password")获取该值
                required：前端非空校验
            -->
            <input type="password" name="password" placeholder="请输入密码" required>
        </div>
        <!-- 登录类型选择（读者/管理员） -->
        <div class="form-item">
            <label>登录类型：</label>
            <!--
                name="type"：后端通过request.getParameter("type")区分登录角色
                value="reader"：读者角色标识，checked表示默认选中
                value="admin"：管理员角色标识
            -->
            <input type="radio" name="type" value="reader" checked> 读者
            <input type="radio" name="type" value="admin"> 管理员
        </div>
        <!-- 提交按钮 -->
        <div class="form-btn">
            <!-- type="submit"：触发表单提交，将表单参数发送到目标Servlet -->
            <button type="submit">登录</button>
        </div>
    </form>
</div>
</body>
</html>