<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>图书列表</title>
    <style>
        /* 全局样式重置 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", sans-serif;
        }

        .top-nav { width: 100%; height: 60px; background: #fff; border-bottom: 1px solid #eee; box-shadow: 0 2px 5px rgba(0,0,0,0.1); position: sticky; top: 0; z-index: 999; display: flex; align-items: center; padding: 0 20px; }
        .nav-menu { display: flex; flex: 1; }
        .nav-menu a { text-decoration: none; color: #333; font-size: 16px; margin: 0 20px; padding: 8px 12px; border-radius: 4px; }
        .nav-menu a:hover, .nav-menu a.active { background-color: #007bff; color: #fff; }

        body {
            background-color: #f8f9fa;
            padding: 20px 0;
        }

        /* 页面容器 - 居中+宽度限制 */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        /* 页面标题 */
        .page-title {
            text-align: center;
            color: #333;
            margin: 20px 0 30px;
            font-size: 28px;
            font-weight: 600;
            position: relative;
        }

        .page-title::after {
            content: "";
            width: 80px;
            height: 3px;
            background-color: #007bff;
            position: absolute;
            bottom: -10px;
            left: 50%;
            transform: translateX(-50%);
            border-radius: 2px;
        }

        /* 搜索+筛选栏 */
        .search-filter-bar {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            margin-bottom: 30px;
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            align-items: center;
        }

        /* 筛选下拉框 */
        .filter-select {
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            color: #333;
            outline: none;
            transition: border-color 0.3s;
        }

        .filter-select:focus {
            border-color: #007bff;
        }

        /* 搜索输入框 */
        .search-input {
            flex: 1;
            min-width: 200px;
            padding: 8px 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
            transition: border-color 0.3s;
        }

        .search-input:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.1);
        }

        /* 搜索按钮 */
        .search-btn {
            padding: 8px 20px;
            background-color: #007bff;
            color: #fff;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .search-btn:hover {
            background-color: #007bff;
        }

        /* 图书表格样式 */
        .book-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background-color: #fff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        }

        /* 表头样式 */
        .book-table th {
            background-color: #007bff;
            color: #fff;
            padding: 12px 15px;
            text-align: center;
            font-weight: 500;
            font-size: 14px;
            border: none;
        }

        /* 表格内容样式 */
        .book-table td {
            padding: 12px 15px;
            text-align: center;
            font-size: 14px;
            color: #333;
            border-bottom: 1px solid #f0f0f0;
        }

        /* 行hover效果 */
        .book-table tbody tr:hover {
            background-color: #f8f9fa;
            cursor: default;
        }

        /* 最后一行去掉下边框 */
        .book-table tbody tr:last-child td {
            border-bottom: none;
        }

        /* 空数据提示 */
        .empty-tip {
            padding: 40px 0;
            color: #999;
            font-size: 16px;
            text-align: center;
        }

        /* 返回按钮 */
        .back-btn {
            display: block;
            width: 120px;
            height: 40px;
            line-height: 40px;
            text-align: center;
            background-color: #007bff;
            color: #fff;
            text-decoration: none;
            border-radius: 4px;
            margin: 30px auto 0;
            font-size: 14px;
            transition: background-color 0.3s;
        }

        .back-btn:hover {
            background-color: #007bff;
        }

        /* 详情链接样式 */
        .detail-link {
            color: #007bff;
            text-decoration: none;
            font-size: 14px;
            transition: color 0.3s;
        }

        .detail-link:hover {
            color: #007bff;
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="top-nav">
    <div class="nav-menu">
        <a href="${pageContext.request.contextPath}/reader/main.jsp">首页</a >
        <a href="${pageContext.request.contextPath}/borrow/rank.jsp">借阅排行</a >
        <a href="${pageContext.request.contextPath}/book/list.jsp" class="active">图书列表</a >
        <a href="${pageContext.request.contextPath}/reader/myinfo.jsp">我的信息</a >
        <a href="${pageContext.request.contextPath}/borrow/record.jsp">我的借阅记录</a >
    </div>
</div>

<div class="container">
    <h2 class="page-title">图书馆图书列表</h2>

    <!-- 1. 搜索表单：提交到/bookList -->
    <div class="search-filter-bar">
        <form action="${pageContext.request.contextPath}/bookList" method="get">
            <select name="bookType" class="filter-select">
                <option value="">全部图书类型</option>
                <option value="java" ${param.bookType == 'java' ? 'selected' : ''}>java</option>
                <option value="计算机" ${param.bookType == '计算机' ? 'selected' : ''}>计算机</option>
                <option value="小说" ${param.bookType == '小说' ? 'selected' : ''}>小说</option>
                <option value="科技" ${param.bookType == '科技' ? 'selected' : ''}>科技</option>
                <option value="教育" ${param.bookType == '教育' ? 'selected' : ''}>教育</option>
                <option value="历史" ${param.bookType == '历史' ? 'selected' : ''}>历史</option>
                <option value="文学" ${param.bookType == '文学' ? 'selected' : ''}>文学</option>
                <option value="体育" ${param.bookType == '体育' ? 'selected' : ''}>体育</option>
                <option value="娱乐明星" ${param.bookType == '娱乐明星' ? 'selected' : ''}>娱乐明星</option>
                <option value="经济" ${param.bookType == '经济' ? 'selected' : ''}>经济</option>
                <option value="体育传记" ${param.bookType == '体育传记' ? 'selected' : ''}>体育传记</option>
                <option value="管理" ${param.bookType == '管理' ? 'selected' : ''}>管理</option>
                <option value="文学" ${param.bookType == '文学' ? 'selected' : ''}>文学</option>
            </select>

            <input type="text" name="keyword" class="search-input"
                   placeholder="请输入书名/作者/ISBN搜索..."
                   value="${param.keyword != null ? param.keyword : ''}">

            <button type="submit" class="search-btn">搜索</button>
            <a href="${pageContext.request.contextPath}/reader/main.jsp"
               style="background-color: #409eff; color: white; padding: 10px 24px; text-decoration: none; border-radius: 4px;">
                返回首页</a>
        </form>
    </div>

    <!-- 图书列表表格 -->
    <table class="book-table">
        <thead>
        <tr>
            <th>图书ID</th>
            <th>ISBN</th>
            <th>书名</th>
            <th>作者</th>
            <th>出版社</th>
            <th>出版年份</th>
            <th>图书类型</th>
            <th>总数量</th>
            <th>已借数量</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="book" items="${bookList}">
            <tr>
                <td>${book.bookId}</td>
                <td>${book.isbn}</td>
                <td>${book.bookName}</td>
                <td>${book.author}</td>
                <td>${book.publisher}</td>
                <td>${book.publishYear}</td>
                <td>${book.bookType}</td>
                <td>${book.totalCount}</td>
                <td>${book.borrowedCount}</td>
                <!-- 2. 详情链接：指向/bookDetail -->
                <td>
                    <a href="${pageContext.request.contextPath}/bookDetail?bookId=${book.bookId}" class="detail-link">详情</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty bookList}">
            <tr>
                <td colspan="10" class="empty-tip">暂无图书数据</td>
            </tr>
        </c:if>
        </tbody>
    </table>

    <!-- 3. 返回按钮：指向/bookList -->
    <a href="${pageContext.request.contextPath}/bookList" class="back-btn">返回</a>
</div>
</body>
</html>
