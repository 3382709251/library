<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.entity.Book" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Book book = (Book) request.getAttribute("book");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>图书详情</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: "微软雅黑", sans-serif; }

        body { background-color: #f5f5f5; padding: 20px; }
        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .page-title {
            background: #002cb0;
            color: white;
            padding: 20px;
            text-align: center;
            font-size: 24px;
        }
        .book-detail {
            display: flex;
            padding: 30px;
            gap: 40px;
        }
        .book-cover {
            width: 280px;
            height: 380px;
            flex-shrink: 0;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            background-color: #e3e5e7;
        }
        .book-cover img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .book-info {
            flex: 1;
        }
        .info-item {
            display: flex;
            margin-bottom: 16px;
            font-size: 16px;
        }
        .info-label {
            width: 110px;
            color: #666;
            font-weight: bold;
        }
        .info-value {
            color: #333;
            flex: 1;
        }
        .intro-title {
            background: #f8f8f8;
            padding: 15px 30px;
            font-size: 18px;
            font-weight: bold;
            border-top: 1px solid #eee;
        }
        .intro-content {
            padding: 25px 30px;
            line-height: 1.8;
            color: #444;
            min-height: 120px;
        }
        .error-msg {
            text-align: center;
            padding: 60px 20px;
            color: #e74c3c;
            font-size: 18px;
        }
        .action-buttons button {
            width: 80px;
            height: 80px;
            border-radius: 16px;
            box-shadow: 8px 8px 16px #b6b9ba, -8px -8px 16px #fafafd;
            background-image: linear-gradient(145deg, #b6b9ba, #f4f6f8);

            padding: 0;
            font-size: 16px;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
            color: #002cb0;
        }

        .btn-borrow:disabled {
            background-color: #cccccc;
            color: #666;
            cursor: not-allowed;
            box-shadow: none;
            background-image: none;
        }

        .btn-return {
            background-image: linear-gradient(145deg,#b6b9ba,#f4f6f8);
            color: #333;
        }

        .btn-return:hover {
            box-shadow:6px 6px 12px #a8abac, -6px -6px 12px #ffffff;
            background-image: linear-gradient(145deg,#a8abac,#e8eaec);

        }

    </style>
</head>
<body>

<div class="container">
    <h2 class="page-title">图书详情</h2>

    <% if (book == null) { %>
    <div class="error-msg">未查询到该图书的详细信息</div>
    <% } else { %>
    <div class="book-detail">
        <!-- 左侧：图书封面 -->
        <div class="book-cover">
            <%
                String coverImg = (book.getBookImage() != null && !book.getBookImage().trim().isEmpty())
                        ? book.getBookImage() : "jinitaimei.png";
            %>
            <img src="${pageContext.request.contextPath}/images/<%= coverImg %>"
            alt="<%= book.getBookName() %>"
            onerror="this.src='${pageContext.request.contextPath}/images/jinitaimei.png';">
        </div>

        <!-- 右侧：图书信息 + 操作按钮 -->
        <div class="book-info">
            <!-- 原有的图书信息保持不变 ... -->
            <div class="info-item">
                <div class="info-label">图书ID：</div>
                <div class="info-value"><%= book.getBookId() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">图书ID：</div>
                <div class="info-value"><%= book.getBookId() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">ISBN编号：</div>
                <div class="info-value"><%= book.getIsbn() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">图书名称：</div>
                <div class="info-value"><%= book.getBookName() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">作者：</div>
                <div class="info-value"><%= book.getAuthor() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">出版社：</div>
                <div class="info-value"><%= book.getPublisher() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">出版年份：</div>
                <div class="info-value"><%= book.getPublishYear() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">图书类型：</div>
                <div class="info-value"><%= book.getBookType() %></div>
            </div>
            <div class="info-item">
                <div class="info-label">总数量：</div>
                <div class="info-value"><%= book.getTotalCount() %> 本</div>
            </div>
            <div class="info-item">
                <div class="info-label">已借数量：</div>
                <div class="info-value"><%= book.getBorrowedCount() %> 本</div>
            </div>
            <div class="info-item">
                <div class="info-label">剩余可借：</div>
                <div class="info-value"><%= book.getTotalCount() - book.getBorrowedCount() %> 本</div>
            </div>
            <div class="info-item">
                <div class="info-label">创建时间：</div>
                <div class="info-value">
                    <%= book.getCreateTime() != null ? sdf.format(book.getCreateTime()) : "未记录" %>
                </div>
            </div>
            <div class="info-item">
                <div class="info-label">更新时间：</div>
                <div class="info-value">
                    <%= book.getUpdateTime() != null ? sdf.format(book.getUpdateTime()) : "未记录" %>
                </div>
            </div>

            <!-- 新增：借阅操作按钮 -->
            <div class="action-buttons" style="margin-top: 30px; display: flex; gap: 15px;">
                <button onclick="borrowBook('<%= book.getBookId() %>')" class="btn btn-return"
                        <%= (book.getTotalCount() - book.getBorrowedCount() <= 0) ? "disabled style='background-color:#ccc;cursor:not-allowed;color:#666;'" : "" %>>借阅申请</button>

                <button onclick="returnBook('<%= book.getBookId() %>')" class="btn btn-return">归还申请</button>
                <button onclick="window.location.href='${pageContext.request.contextPath}/borrow/rank.jsp'" class="btn btn-return">排行</button>
                <button onclick="window.location.href='${pageContext.request.contextPath}/book/list.jsp'" class="btn btn-return">列表</button>
                <button onclick="window.location.href='${pageContext.request.contextPath}/reader/main.jsp'" class="btn btn-return">首页</button>
            </div>
        </div>
    </div>

    <!-- 图书简介保持不变 -->
    <div class="intro-title">图书简介：</div>
    <div class="intro-content">
        <%= book.getBookIntroduce() != null && !book.getBookIntroduce().trim().isEmpty()
                ? book.getBookIntroduce() : "暂无简介" %>

    </div>
    <% } %>
</div>

<script>
    // 一键借阅
    function borrowBook(bookId) {
        if (!confirm('确定要申请借阅这本书吗？')) return;

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '${pageContext.request.contextPath}/borrow/borrowBook', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const result = JSON.parse(xhr.responseText);
                    if (result.success) {
                        alert('申请借阅成功！请到图书管理员处核对信息！');
                        location.reload();   // 刷新页面更新剩余数量
                    } else {
                        alert('申请借阅失败：' + result.message);
                    }
                } else {
                    alert('请求失败，请稍后重试');
                }
            }
        };
        xhr.send('bookId=' + bookId);
    }

    // 一键归还
    function returnBook(bookId) {
        if (!confirm('确定要提交归还申请吗？提交后请线下归还图书，并等待管理员确认！')) return;

        const xhr = new XMLHttpRequest();
        xhr.open('POST', '${pageContext.request.contextPath}/borrow/applyReturnBook', true); // 新接口
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    const result = JSON.parse(xhr.responseText);
                    if (result.success) {
                        alert('归还申请提交成功！请到图书管理员处归还图书，等待管理员确认！');
                        location.reload();
                    } else {
                        alert('提交归还申请失败：' + result.message);
                    }
                } else {
                    alert('请求失败，请稍后重试');
                }
            }
        };
        xhr.send('bookId=' + bookId);
    }
</script>
</body>
</html>