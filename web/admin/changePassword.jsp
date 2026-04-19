<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.entity.Admin" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>修改密码</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <title>修改密码</title>
    <style>
        .form-container { max-width: 480px; margin: 60px auto; padding: 40px; background: white; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
        .form-group { margin-bottom: 20px; }body { background-color: #f8f9fa; font-family: "微软雅黑", sans-serif; }
        .navbar { background-color: #198754 !important; }
        .table thead { background-color: #198754; color: white; }
        .card { box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .badge { font-size: 0.95em; }
        label { display: block; margin-bottom: 8px; font-weight: 500; }
        input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 15px; }
        .btn { padding: 12px 40px; background: #409eff; color: white; border: none; border-radius: 6px; cursor: pointer; margin-top: 10px; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold fs-4" href="${pageContext.request.contextPath}/admin/changePassword.jsp">
            📚 修改密码
        </a>
        <div class="navbar-nav ms-auto">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookList.jsp">图书管理</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/readerList.jsp">读者管理</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/changePassword.jsp">修改密码</a >
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="form-container">
    <h2>管理员修改密码</h2>
    <%
        // 从session获取当前登录的管理员
        Admin admin = (Admin) session.getAttribute("loginAdmin");
        if (admin == null) {
            response.sendRedirect("../login.jsp");
            return;
        }
    %>

    <form id="pwdForm">
        <div class="form-group">
            <label>旧密码</label>
            <input type="password" id="oldPassword" required>
        </div>
        <div class="form-group">
            <label>新密码</label>
            <input type="password" id="newPassword" required>
        </div>
        <div class="form-group">
            <label>确认新密码</label>
            <input type="password" id="confirmPassword" required>
        </div>

        <button type="button" class="btn" onclick="changePassword()">确认修改</button>
        <button type="button" class="btn" style="background:#6c757d;" onclick="history.back()">返回</button>
    </form>
</div>

<script>
    const basePath = '${pageContext.request.contextPath}';

    function changePassword() {
        const oldPassword = document.getElementById("oldPassword").value.trim();
        const newPassword = document.getElementById("newPassword").value.trim();
        const confirmPassword = document.getElementById("confirmPassword").value.trim();

        console.log("旧密码:", oldPassword);
        console.log("新密码:", newPassword);
        console.log("确认密码:", confirmPassword);

        if (!oldPassword || !newPassword || !confirmPassword) {
            alert("所有密码字段不能为空！");
            return;
        }
        if (newPassword !== confirmPassword) {
            alert("两次输入的新密码不一致！");
            return;
        }
        if (newPassword.length < 6) {
            alert("新密码长度不能少于6位！");
            return;
        }

        const params = new URLSearchParams();
        params.append("oldPassword", oldPassword);
        params.append("newPassword", newPassword);
        params.append("confirmPassword", confirmPassword);

        // 提交到 管理员修改密码 Servlet
        fetch(basePath + '/admin/changePassword', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        })
            .then(res => res.json())
            .then(result => {
                console.log("后端返回结果:", result);
                alert(result.message);
                if (result.success) {
                    // 清空输入框
                    document.getElementById("oldPassword").value = "";
                    document.getElementById("newPassword").value = "";
                    document.getElementById("confirmPassword").value = "";

                    // 修改成功后 退出登录 跳转到登录页
                    setTimeout(()=>{
                        location.href = basePath + "/login.jsp";
                    },1000);
                }
            })
            .catch(err => {
                console.error("请求失败:", err);
                alert("请求失败，请稍后重试");
            });
    }
</script>
</body>
</html>