<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.entity.BorrowRecord" %>
<%@ page import="com.library.entity.Book" %>
<%@ page import="com.library.dao.BorrowDao" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%
    // 1. 校验登录状态（增强：增加空指针判断）
    com.library.entity.Reader reader = (com.library.entity.Reader) session.getAttribute("loginReader");
    if (reader == null || reader.getReaderId() == null || reader.getReaderId().isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    // 2. 查询借阅记录（增强：捕获异常，避免查询失败导致页面崩溃）
    BorrowDao borrowDao = new BorrowDao();
    List<BorrowRecord> records = null;
    try {
        records = borrowDao.getBorrowRecordsByReader(reader.getReaderId());
    } catch (Exception e) {
        e.printStackTrace();
        // 异常时初始化空列表，避免页面报错
        records = new java.util.ArrayList<>();
    }

    // 3. 日期格式化（仅年月日）
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>我的借阅记录</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: "微软雅黑", sans-serif; }
        .top-nav { width: 100%; height: 60px; background: #fff; border-bottom: 1px solid #eee; box-shadow: 0 2px 5px rgba(0,0,0,0.1); position: sticky; top: 0; z-index: 999; display: flex; align-items: center; padding: 0 20px; }
        .nav-menu { display: flex; flex: 1; }
        .nav-menu a { text-decoration: none; color: #333; font-size: 16px; margin: 0 20px; padding: 8px 12px; border-radius: 4px; }
        .nav-menu a:hover, .nav-menu a.active { background-color: #409eff; color: #fff; }
        body { background: #f5f5f5; padding: 20px; }
        .container { max-width: 1200px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); overflow: hidden; }
        .header { background: #409eff; color: white; padding: 20px; text-align: center; font-size: 24px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #f8f8f8; }
        .status-borrowing { color: #409eff; font-weight: bold; }
        .status-returned { color: #909399; }
        .unknown-text { color: #999; } /* 新增：未知信息统一灰色 */
        .back-btn {
            display: inline-block;
            margin: 20px;
            padding: 12px 24px;
            background: #409eff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background 0.3s;
        }
        .back-btn:hover { background: #337ecc; } /* 新增：按钮hover效果 */
    </style>
</head>
<body>

<div class="top-nav">
    <div class="nav-menu">
        <a href="${pageContext.request.contextPath}/reader/main.jsp">首页</a>
        <a href="${pageContext.request.contextPath}/borrow/rank.jsp">借阅排行</a>
        <a href="${pageContext.request.contextPath}/book/list.jsp">图书列表</a>
        <a href="${pageContext.request.contextPath}/reader/myinfo.jsp">我的信息</a>
        <a href="${pageContext.request.contextPath}/borrow/record.jsp" class="active">我的借阅记录</a>
    </div>
</div>

<div class="container">
    <div class="header">我的借阅记录</div>

    <table>
        <thead>
        <tr>
            <th>图书ID</th>
            <th>图书名称</th>
            <th>作者</th>
            <th>借阅时间</th>
            <th>归还时间</th>
            <th>状态</th>
        </tr>
        </thead>
        <tbody>
        <% if (records == null || records.isEmpty()) { %>
        <tr>
            <td colspan="6" style="text-align:center; color:#999; padding:60px;">
                暂无借阅记录
            </td>
        </tr>
        <% } else {
            for (BorrowRecord r : records) {
                // 提前获取核心字段，减少重复判断
                Book book = r.getBook();
                String bookName = (book != null && book.getBookName() != null && !book.getBookName().isEmpty())
                        ? book.getBookName()
                        : (r.getBookId() != null ? "[" + r.getBookId() + "] 未知书名" : "未知图书");
                String author = (book != null && book.getAuthor() != null && !book.getAuthor().isEmpty())
                        ? book.getAuthor()
                        : "未知作者";
                String borrowTimeStr = (r.getBorrowTime() != null) ? sdf.format(r.getBorrowTime()) : "未知时间";
                String returnTimeStr = (r.getReturnTime() != null)
                        ? sdf.format(r.getReturnTime())
                        : "<span class='status-borrowing'>未归还</span>";
                String statusStr = (r.getReturnTime() != null)
                        ? "<span class='status-returned'>已归还</span>"
                        : "<span class='status-borrowing'>借阅中</span>";
        %>
        <tr>
            <td><%= r.getBookId() != null ? r.getBookId() : "未知ID" %></td>
            <td><%= bookName %><%-- 简化：直接使用提前封装的变量 --%></td>
            <td class="unknown-text"><%= author %><%-- 新增：未知作者灰色显示 --%></td>
            <td><%= borrowTimeStr %></td>
            <td><%= returnTimeStr %></td>
            <td><%= statusStr %></td>
        </tr>
        <% } } %>
        </tbody>
    </table>

    <a href="${pageContext.request.contextPath}/reader/main.jsp" class="back-btn">返回首页</a>
</div>

</body>
</html>