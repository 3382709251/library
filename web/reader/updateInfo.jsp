<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.entity.Reader" %>
<html>
<head>
    <title>修改基本信息</title>
    <style>
        .form-container {
            max-width: 500px;
            margin: 60px auto;
            padding: 40px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #555;
        }
        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 15px;
        }
        .btn {
            padding: 12px 40px;
            background: #409eff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 10px;
            font-size: 16px;
        }
        .btn-secondary {
            background: #6c757d;
        }
    </style>
</head>
<body>
<div class="form-container">
    <h2>修改基本信息</h2>

    <%
        Reader reader = (Reader) session.getAttribute("loginReader");
        if (reader == null) {
            response.sendRedirect("../login.jsp");
            return;
        }
    %>

    <form id="infoForm" accept-charset="UTF-8">
        <div class="form-group">
            <label>姓名 *</label>
            <input type="text" id="readerName" value="<%= reader.getReaderName() %>" required>
        </div>
        <div class="form-group">
            <label>学院</label>
            <input type="text" id="college" value="<%= reader.getCollege() != null ? reader.getCollege() : "" %>">
        </div>
        <div class="form-group">
            <label>电话</label>
            <input type="text" id="readerPhone" value="<%= reader.getReaderPhone() != null ? reader.getReaderPhone() : "" %>">
        </div>

        <button type="button" class="btn" onclick="updateInfo()">保存修改</button>
        <button type="button" class="btn btn-secondary" onclick="history.back()">返回</button>
    </form>
</div>

<script>
    const basePath = '${pageContext.request.contextPath}';

    function updateInfo() {
        const readerName = document.getElementById("readerName").value.trim();
        const college = document.getElementById("college").value.trim();
        const readerPhone = document.getElementById("readerPhone").value.trim();

        console.log("准备修改基本信息:");
        console.log("姓名:", readerName);
        console.log("学院:", college);
        console.log("电话:", readerPhone);

        if (!readerName) {
            alert("姓名不能为空！");
            return;
        }

        // 使用 URLSearchParams 提交（与修改密码一致）
        const params = new URLSearchParams();
        params.append("readerName", readerName);
        params.append("college", college);
        params.append("readerPhone", readerPhone);

        console.log("准备发送的参数:", params.toString());

        fetch(basePath + '/reader/updateInfo', {
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
                    // 修改成功后返回个人信息页面
                    location.href = basePath + '/reader/myinfo.jsp';
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