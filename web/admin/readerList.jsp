<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>读者管理</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; font-family: "微软雅黑", sans-serif; }
        .navbar { background-color: #198754 !important; }
        .table thead { background-color: #198754; color: white; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold fs-4" href="${pageContext.request.contextPath}/admin/readerList.jsp">
            📚 读者 - 管理后台
        </a>
        <div class="navbar-nav ms-auto">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/bookList.jsp">图书管理</a>
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/readerList.jsp">读者管理</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/changePassword.jsp">修改密码</a >
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">退出登录</a>
        </div>
    </div>
</nav>

<div class="container mt-4">
    <h2 class="mb-4">读者管理</h2>
    <div class="row mb-3">
        <div class="col-md-6">
            <input type="text" id="searchInput" class="form-control" placeholder="搜索读者ID / 姓名  / 学院">
        </div>
    </div>

    <table class="table table-hover align-middle">
        <thead>
        <tr>
            <th>读者ID</th>
            <th>姓名</th>
            <th>学院</th>
            <th>手机号</th>
            <th>读者类别</th>
            <th>最大借阅数</th>
            <th>账户罚款</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody id="readerTableBody"></tbody>
    </table>
</div>

<script>
    let readersData = [];
    const basePath = '${pageContext.request.contextPath}';

    function loadReaderList() {
        console.log("🚀 开始请求读者列表...");
        fetch(basePath + '/admin/readerList') // 这个是接口路径（返回JSON），无需改
            .then(response => {
                console.log("响应状态码:", response.status);
                if (!response.ok) throw new Error('HTTP ' + response.status);
                return response.json();
            })
            .then(readers => {
                console.log("✅ 成功获取 " + readers.length + " 条读者数据");
                if (readers.length > 0) {
                    console.log("第一位读者:", readers[0]);
                }
                readersData = readers || [];
                renderReaderTable(readers);
            })
            .catch(err => console.error("请求失败:", err));
    }

    function renderReaderTable(readers) {
        const tbody = document.getElementById('readerTableBody');
        tbody.innerHTML = '';

        if (readers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-5">暂无读者数据</td></tr>`;
            return;
        }

        for (let i = 0; i < readers.length; i++) {
            const r = readers[i];
            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + (r.readerId || r.Reader_ID || r.reader_id || '') + '</td>' +
                '<td>' + (r.readerName || r.reader_name || '') + '</td>' +
                '<td>' + (r.college || '') + '</td>' +
                '<td>' + (r.readerphone || r.readerPhone || '') + '</td>' +
                '<td>' + (r.readerCategory || r.Reader_Category || '') + '</td>' +
                '<td>' + (r.maxBorrowCount || r.Max_borrow_count || 0) + '</td>' +
                '<td>¥' + (r.accountMoney || r.Account_Money || 0).toFixed(2) + '</td>' +
                '<td>' +
                '<button class="btn btn-sm btn-primary detail-btn" data-index="' + i + '">查看借阅详情</button>' +
                '</td>';
            tbody.appendChild(tr);
        }
    }

    // 修正：跳转路径添加 .jsp 后缀，指向JSP页面而非接口
    document.getElementById('readerTableBody').addEventListener('click', function(e) {
        const btn = e.target.closest('button');
        if (!btn) return;

        const index = parseInt(btn.getAttribute('data-index'));
        if (isNaN(index) || !readersData[index]) return;

        const reader = readersData[index];
        const readerId = reader.readerId || reader.Reader_ID || reader.reader_id || '';

        if (!readerId) {
            alert("无法获取读者ID！");
            return;
        }

        console.log("📌 准备跳转，读者ID =", readerId);
        // 关键修改：添加 .jsp 后缀，跳转到JSP页面
        window.location.href = basePath + '/admin/readerDetail.jsp?readerId=' + encodeURIComponent(readerId);
    });

    // 搜索功能
    document.getElementById('searchInput').addEventListener('input', function() {
        const keyword = this.value.toLowerCase().trim();
        if (!keyword) {
            renderReaderTable(readersData);
            return;
        }

        const filtered = readersData.filter(r =>
            (r.readerId && r.readerId.toLowerCase().includes(keyword)) ||
            (r.readerName && r.readerName.toLowerCase().includes(keyword)) ||
            (r.readerphone && r.readerphone.includes(keyword)) ||
            (r.college && r.college.toLowerCase().includes(keyword))
        );
        renderReaderTable(filtered);
    });

    // 初始化加载
    window.addEventListener('load', loadReaderList);
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>