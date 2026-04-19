<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.entity.Reader" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<html>
<head>
    <title>个人信息</title>
    <link rel="stylesheet" href="../css/userinfo.css">
    <style>
        /* 全局重置 - 新增：清除body默认边距 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", sans-serif;
        }

        /* 修复核心：移除body顶部padding，调整背景色应用范围 */
        body {
            background-color: #e8f4f8;
            /* 原代码：padding: 40px 0; 导致导航栏上方空白，改为只给内容区加间距 */
            padding: 0;
            margin: 0; /* 清除body默认margin */
        }

        /* 导航栏样式优化：移除不必要的padding，确保顶栏贴顶 */
        .top-nav {
            width: 100%;
            height: 60px;
            background: #fff;
            border-bottom: 1px solid #eee;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            position: sticky;
            top: 0;
            z-index: 999;
            display: flex;
            align-items: center;
            padding: 0 20px;
            /* 新增：确保导航栏无顶部偏移 */
            margin-top: 0;
        }
        .nav-menu { display: flex; flex: 1; }
        .nav-menu a {
            text-decoration: none;
            color: #333;
            font-size: 16px;
            margin: 0 20px;
            padding: 8px 12px;
            border-radius: 4px;
        }
        .nav-menu a:hover, .nav-menu a.active {
            background-color: #409eff;
            color: #fff;
        }

        /* 主容器 - 新增顶部padding，替代body的全局padding */
        .user-container {
            max-width: 600px;
            margin: 0 auto;
            padding: 40px 20px; /* 把body的padding移到内容区，保证内容不贴顶 */
        }

        /* 页面标题 */
        .user-title {
            font-size: 24px;
            color: #333;
            font-weight: 600;
            margin-bottom: 25px;
            padding-bottom: 12px;
            border-bottom: 2px solid #e8f4f8;
            position: relative;
        }

        .user-title::after {
            content: "";
            width: 40px;
            height: 3px;
            background-color: #6b7280;
            position: absolute;
            left: 0;
            bottom: -2px;
            border-radius: 2px;
        }

        /* 信息卡片 - 核心美化 */
        .user-card {
            background-color: #ffffff;
            border-radius: 12px;
            padding: 35px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
            border: 1px solid #f0f0f0;
        }

        /* 信息行 - 每行布局 */
        .user-info-item {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px dashed #f0f0f0;
        }

        /* 最后一行去掉下划线 */
        .user-info-item:last-child {
            border-bottom: none;
        }

        /* 标签样式 */
        .user-info-item label {
            width: 100px;
            font-size: 15px;
            color: #666;
            font-weight: 500;
        }

        /* 内容样式 */
        .user-info-item span {
            font-size: 15px;
            color: #333;
            flex: 1;
        }

        /* 空值文本样式（新增，统一空值显示） */
        .empty-text {
            color: #999;
            font-style: italic;
        }

        /* 状态标签美化 */
        .status-tag {
            display: inline-block;
            padding: 4px 12px;
            background-color: #e8f4f8;
            color: #4299e1;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 500;
        }

        .btn-return {
            background-image: linear-gradient(145deg,#b6b9ba,#f4f6f8);
            color: #333;
        }

        .btn-return:hover {
            box-shadow:6px 6px 12px #a8abac, -6px -6px 12px #ffffff;
            background-image: linear-gradient(145deg,#a8abac,#e8eaec);

        }

        .btn-info { background: #17a2b8; color: white; }
        .action-buttons {
            margin: 30px 0 20px 0;
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .action-btn {
            padding: 14px 32px;
            font-size: 16px;
            font-weight: 500;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            min-width: 160px;
            text-align: center;
        }

        .btn-info {
            background: linear-gradient(145deg, #17a2b8, #138496);
            color: white;
            box-shadow: 0 4px 10px rgba(23, 162, 184, 0.3);
        }

        .btn-info:hover {
            background: linear-gradient(145deg, #138496, #0f6b7a);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(23, 162, 184, 0.4);
        }

        .btn-warning {
            background: linear-gradient(145deg, #ffc107, #e0a800);
            color: #333;
            box-shadow: 0 4px 10px rgba(255, 193, 7, 0.3);
        }

        .btn-warning:hover {
            background: linear-gradient(145deg, #e0a800, #c98f00);
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(255, 193, 7, 0.4);
        }

    </style>
</head>
<body>

<div class="top-nav">
    <div class="nav-menu">
        <a href="${pageContext.request.contextPath}/reader/main.jsp">首页</a >
        <a href="${pageContext.request.contextPath}/borrow/rank.jsp">借阅排行</a >
        <a href="${pageContext.request.contextPath}/book/list.jsp">图书列表</a >
        <a href="${pageContext.request.contextPath}/reader/myinfo.jsp" class="active">我的信息</a >
        <a href="${pageContext.request.contextPath}/borrow/record.jsp" >我的借阅记录</a >
    </div>
</div>

<div class="user-container">
    <h2 class="user-title">个人中心</h2>

    <div class="user-card">
        <%
            // 从 session 取出登录的读者（正确写法！）
            Reader reader = (Reader) session.getAttribute("loginReader");

            // 安全判断
            if (reader == null) {
                response.sendRedirect("../login.jsp");
                return;
            }
        %>

        <div class="user-info-item">
            <label>读者ID：</label>
            <span><%= reader.getReaderId() %></span>
        </div>

        <div class="user-info-item">
            <label>姓名：</label>
            <span><%= reader.getReaderName() %></span>
        </div>

        <div class="user-info-item">
            <label>学院：</label>
            <span><%= reader.getCollege() != null ? reader.getCollege() : "未填写" %></span>
        </div>

        <div class="user-info-item">
            <label>读者类型：</label>
            <span><%= reader.getReaderCategory() == 1 ? "学生" : (reader.getReaderCategory() == 2 ? "教师" : "其他") %></span>
        </div>

        <div class="user-info-item">
            <label>电话：</label>
            <span><%= reader.getReaderPhone() != null ? reader.getReaderPhone() : "未填写" %></span>
        </div>

        <%-- 最大借阅时长 --%>
        <div class="user-info-item">
            <label>最大借阅时长：</label>
            <span>
        <%
            // 1. 获取值 + 空值/0值判断
            int maxBorrowTime = reader.getMaxBorrowTime();
            if (maxBorrowTime > 0) {
                out.print(maxBorrowTime + " 天");
            } else {
                out.print("<span class='empty-text'>未设置</span>");
            }
        %>
    </span>
        </div>

        <%-- 最大借阅数量 --%>
        <div class="user-info-item">
            <label>最大借阅数量：</label>
            <span>
        <%
            int maxBorrowCount = reader.getMaxBorrowCount();
            if (maxBorrowCount > 0) {
                out.print(maxBorrowCount + " 本");
            } else {
                out.print("<span class='empty-text'>未设置</span>");
            }
        %>
    </span>
        </div>

        <%-- 创建时间 --%>
        <div class="user-info-item">
            <label>创建时间：</label>
            <span>
        <%
            Date createTime = reader.getCreateTime();
            if (createTime != null) {
                // 2. 实例化格式化对象（避免重复创建）
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                out.print(sdf.format(createTime));
            } else {
                out.print("<span class='empty-text'>未记录</span>");
            }
        %>
    </span>
        </div>

        <%-- 更新时间 --%>
        <div class="user-info-item">
            <label>更新时间：</label>
            <span>
        <%
            Date updateTime = reader.getUpdateTime();
            if (updateTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                out.print(sdf.format(updateTime));
            } else {
                out.print("<span class='empty-text'>未记录</span>");
            }
        %>
    </span>
        </div>

        <div class="user-info-item">
            <label>账户状态：</label>
            <span class="status-tag">
                正常</span>
        </div>

        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/reader/updateInfo.jsp"
               class="action-btn btn-info">
                修改基本信息
            </a >
            <a href="${pageContext.request.contextPath}/reader/changePassword.jsp"
               class="action-btn btn-warning">
                修改密码
            </a >
        </div>
    </div>


    </div>
    <div style="text-align:center; margin-top:30px;">
        <a href=" " class="btn btn-return">返回首页</a >
    </div>
</div>
</body>
</html>