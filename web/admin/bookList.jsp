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
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>图书信息管理 - 管理员后台</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <style>
        body { background-color: #f8f9fa; font-family: "微软雅黑", sans-serif; }
        .navbar { background-color: #198754 !important; }
        .table thead { background-color: #198754; color: white; }
        .card { box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .badge { font-size: 0.95em; }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold fs-4" href=" ">
            📚 图书借阅管理系统 - 管理后台
        </a >
        <div class="navbar-nav ms-auto">
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/bookList.jsp">图书管理</a >
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/readerList.jsp">读者管理</a >
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/changePassword.jsp">修改密码</a >
            <a class="nav-link" href="${pageContext.request.contextPath}/logout">退出登录</a >
        </div>
    </div>
</nav>

<div class="container mt-4">
    <div class="card">
        <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0">📖 图书信息管理</h4>
            <button class="btn btn-light fw-bold" onclick="showAddModal()">+ 新增图书</button>
        </div>

        <div class="card-body">
            <div class="input-group mb-3 w-50">
                <input type="text" id="searchInput" class="form-control" placeholder="搜索书名 / 作者 / 图书ID...">
                <button class="btn btn-success" onclick="loadBookList()">🔄 刷新</button>
            </div>

            <table class="table table-hover align-middle" id="bookTable">
                <thead>
                <tr>
                    <th>图书ID</th>
                    <th>书名</th>
                    <th>作者</th>
                    <th>出版社</th>
                    <th>类型</th>
                    <th>库存情况</th>
                    <th width="180">操作</th>
                </tr>
                </thead>
                <tbody id="bookTableBody"></tbody>
            </table>
            <!-- ====================== 新增图书模态框 ====================== -->
            <div class="modal fade" id="addBookModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header bg-success text-white">
                            <h5 class="modal-title">📚 新增图书</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="addBookForm" enctype="multipart/form-data">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">图书ID (book_id) <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" name="bookId" required placeholder="如：B001">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">ISBN <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" name="isbn" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">书名 <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" name="bookName" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">作者</label>
                                            <input type="text" class="form-control" name="author">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">出版社</label>
                                            <input type="text" class="form-control" name="publisher">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">出版年份</label>
                                            <input type="number" class="form-control" name="publishYear" value="2024">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">价格 (元)</label>
                                            <input type="number" step="0.01" class="form-control" name="price" value="0.00">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">图书类型</label>
                                            <input type="text" class="form-control" name="bookType">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">总库存 <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control" name="totalCount" value="1" min="1" required>
                                        </div>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">图书简介</label>
                                    <textarea class="form-control" name="bookIntroduce" rows="3"></textarea>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">封面图片</label>
                                    <input type="file" class="form-control" name="bookImage" accept="image/*" id="imageInput">
                                    <div id="imagePreview" class="mt-3 text-center">
                                        <img id="previewImg" style="max-width:100%; max-height:260px; display:none; border:1px solid #ddd; border-radius:8px;">
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-success" onclick="submitAddBook()">保存新增</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 编辑图书模态框 -->
            <div class="modal fade" id="editBookModal" tabindex="-1">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header bg-primary text-white">
                            <h5 class="modal-title">✏️ 编辑图书</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editBookForm">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">图书ID <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" id="editBookId" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">书名 <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" id="editBookName" required>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">作者</label>
                                            <input type="text" class="form-control" id="editAuthor">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">出版社</label>
                                            <input type="text" class="form-control" id="editPublisher">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">图书类型</label>
                                            <input type="text" class="form-control" id="editBookType">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">总库存</label>
                                            <input type="number" class="form-control" id="editTotalCount" min="1">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">价格 (元)</label>
                                            <input type="number" step="0.01" class="form-control" id="editPrice">
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">出版年份</label>
                                            <input type="number" class="form-control" id="editPublishYear">
                                        </div>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">图书简介</label>
                                    <textarea class="form-control" id="editBookIntroduce" rows="3"></textarea>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" onclick="submitEditBook()">保存修改</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let booksData = [];
    const basePath = '${pageContext.request.contextPath}';

    function loadBookList() {
        console.log("当前 basePath 值是：【" + basePath + "】");
        console.log("🚀 开始请求图书列表...");

        fetch(basePath + '/admin/bookList')
            .then(response => {
                console.log("响应状态码:", response.status);
                if (!response.ok) throw new Error('HTTP ' + response.status);
                return response.json();
            })
            .then(books => {
                console.log("✅ 成功获取 " + books.length + " 条数据");
                if (books.length > 0) console.log("第一本书:", books[0]);
                booksData = books || [];
                renderBookTable(books);
            })
            .catch(err => console.error("请求失败:", err));
    }

    function renderBookTable(books) {
        const tbody = document.getElementById('bookTableBody');
        tbody.innerHTML = '';

        if (books.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center text-muted py-5">暂无图书数据</td></tr>`;
            return;
        }

        for (let i = 0; i < books.length; i++) {
            const book = books[i];

            // ==================== 新增上下架状态处理 ====================
            const status = (book.bookStatus || book.book_status || '正常');
            const statusBadge = status === '已下架'
                ? '<span class="badge bg-danger">已下架</span>'
                : '<span class="badge bg-success">正常</span>';

            const statusBtnText = status === '已下架' ? '上架' : '下架';
            const statusBtnClass = status === '已下架' ? 'btn-success' : 'btn-warning';

            const tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + (book.bookId || book.id || book.book_id || '') + '</td>' +
                '<td>' + (book.bookName || book.book_name || '') + '</td>' +
                '<td>' + (book.author || '') + '</td>' +
                '<td>' + (book.publisher || '') + '</td>' +
                '<td>' + (book.bookType || '') + '</td>' +
                '<td>' +
                '<span class="badge bg-success">' + (book.totalCount != null ? book.totalCount : 0) + '</span> / ' +
                '<span class="badge bg-warning">' + (book.borrowedCount != null ? book.borrowedCount : 0) + '</span>' +
                '</td>' +
                '<td>' + statusBadge + '</td>' +                    // 新增状态列
                '<td>' +
                '<button class="btn btn-sm btn-outline-primary me-2" data-index="' + i + '" data-action="edit">编辑</button>' +
                '<button class="btn btn-sm ' + statusBtnClass + ' me-2 status-btn" data-index="' + i + '" data-action="status">'
                + statusBtnText + '</button>' +
                '<button class="btn btn-sm btn-outline-danger" data-index="' + i + '" data-action="delete">删除</button>' +
                '</td>';
            tbody.appendChild(tr);
        }
    }

    // ====================== 事件委托（只新增上下架分支，其他完全不动） ======================
    document.getElementById('bookTableBody').addEventListener('click', function(e) {
        const btn = e.target.closest('button');
        if (!btn) return;

        const index = parseInt(btn.getAttribute('data-index'));
        if (isNaN(index) || !booksData[index]) {
            console.error("无效的 index 或 booksData 不同步", index, booksData.length);
            return;
        }

        const book = booksData[index];

        if (btn.getAttribute('data-action') === 'delete') {
            let bookId = book.bookId || book.id || book.book_id || '';
            if (!bookId) {
                console.error("当前图书对象:", book);
                alert("无法获取图书ID，请刷新页面后重试！");
                return;
            }
            deleteBook(bookId, book.bookName || book.book_name || '未知图书');

        } else if (btn.getAttribute('data-action') === 'edit') {
            editBook(book);

        } else if (btn.getAttribute('data-action') === 'status') {
            let bookId = book.bookId || book.id || book.book_id || '';
            let currentStatus = book.bookStatus || book.book_status || '正常';
            let newStatus = (currentStatus === '已下架') ? '正常' : '已下架';
            let bookName = book.bookName || book.book_name || '未知图书';

            changeBookStatus(bookId, newStatus, bookName);
        }
    });

    // ====================== 上下架功能 ======================
    function changeBookStatus(bookId, newStatus, bookName) {
        console.log("changeBookStatus 被调用，参数:", { bookId, newStatus, bookName });

        const safeBookName = (bookName && bookName.trim() !== '') ? bookName : '未知图书';
        const safeNewStatus = newStatus || '未知状态';

        if (!confirm('确定要将图书【' + safeBookName + '】设置为【' + safeNewStatus + '】吗？')) {
            return;
        }

        fetch(basePath + '/admin/bookStatus', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: 'bookId=' + encodeURIComponent(bookId) + '&status=' + encodeURIComponent(safeNewStatus)
        })
            .then(res => res.text())
            .then(result => {
                if (result.includes("成功")) {
                    alert("✅ " + result);
                    loadBookList();
                } else {
                    alert("❌ " + result);
                }
            })
            .catch(err => console.error(err));
    }

    // ====================== 删除图书（最终修复版） ======================
    function deleteBook(bookId, bookName) {
        console.log("deleteBook 被调用，参数:", { bookId, bookName });

        const safeBookName = (bookName && bookName.trim() !== '') ? bookName : '未知图书';
        const safeBookId = bookId || '未知ID';

        if (!confirm('⚠️ 确定删除图书【' + safeBookName + '】(ID: ' + safeBookId + ') 吗？\n\n此操作不可恢复！')) {
            return;
        }

        fetch(basePath + '/admin/bookDelete', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
            body: 'bookId=' + encodeURIComponent(safeBookId)
        })
            .then(res => res.text())
            .then(result => {
                if (result.includes("成功")) {
                    alert("✅ 删除成功！");
                    loadBookList();
                } else {
                    alert("❌ " + result);
                }
            })
            .catch(err => console.error(err));
    }
    // ====================== 编辑图书（保持不变） ======================
    let currentEditBook = null;

    function editBook(book) {
        currentEditBook = book;

        document.getElementById('editBookId').value = book.bookId || book.id || book.book_id || '';
        document.getElementById('editBookName').value = book.bookName || book.book_name || '';
        document.getElementById('editAuthor').value = book.author || '';
        document.getElementById('editPublisher').value = book.publisher || '';
        document.getElementById('editBookType').value = book.bookType || '';
        document.getElementById('editTotalCount').value = book.totalCount || 1;
        document.getElementById('editPrice').value = book.price || 0;
        document.getElementById('editPublishYear').value = book.publishYear || 2024;
        document.getElementById('editBookIntroduce').value = book.bookIntroduce || '';

        new bootstrap.Modal(document.getElementById('editBookModal')).show();
    }

    function submitEditBook() {
        const bookId = document.getElementById('editBookId').value.trim();
        if (!bookId) {
            alert("图书ID不能为空！");
            return;
        }

        const params = new URLSearchParams();
        params.append("bookId", bookId);
        params.append("bookName", document.getElementById('editBookName').value || '');
        params.append("author", document.getElementById('editAuthor').value || '');
        params.append("publisher", document.getElementById('editPublisher').value || '');
        params.append("bookType", document.getElementById('editBookType').value || '');
        params.append("totalCount", document.getElementById('editTotalCount').value || '1');
        params.append("price", document.getElementById('editPrice').value || '0');
        params.append("publishYear", document.getElementById('editPublishYear').value || '2024');
        params.append("bookIntroduce", document.getElementById('editBookIntroduce').value || '');

        console.log("发送编辑请求，bookId =", bookId);

        fetch(basePath + '/AdminBookUpdateServlet?' + params.toString(), {
            method: 'POST'
        })
            .then(res => res.text())
            .then(result => {
                console.log("编辑返回:", result);
                if (result.includes("成功") || result.includes("success")) {
                    alert("✅ 修改成功！");
                    bootstrap.Modal.getInstance(document.getElementById('editBookModal')).hide();
                    loadBookList();
                } else {
                    alert("修改失败: " + result);
                }
            })
            .catch(err => {
                console.error(err);
                alert("请求失败");
            });
    }

    // ====================== 新增图书（保持不变） ======================
    function showAddModal() {
        const form = document.getElementById('addBookForm');
        if (form) form.reset();
        const preview = document.getElementById('previewImg');
        if (preview) preview.style.display = 'none';

        const modal = new bootstrap.Modal(document.getElementById('addBookModal'));
        modal.show();
    }

    // 图片预览
    document.addEventListener('DOMContentLoaded', function() {
        const imageInput = document.getElementById('imageInput');
        if (imageInput) {
            imageInput.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function(ev) {
                        const img = document.getElementById('previewImg');
                        if (img) {
                            img.src = ev.target.result;
                            img.style.display = 'block';
                        }
                    };
                    reader.readAsDataURL(file);
                }
            });
        }
    });

    function submitAddBook() {
        const form = document.getElementById('addBookForm');
        if (!form || !form.checkValidity()) {
            alert("请填写书名和总库存！");
            return;
        }

        const formData = new FormData(form);

        fetch(basePath + '/AdminBookAddServlet', {
            method: 'POST',
            body: formData
        })
            .then(res => res.text())
            .then(result => {
                if (result.includes("成功") || result.includes("success")) {
                    alert("✅ 新增图书成功！");
                    const modal = bootstrap.Modal.getInstance(document.getElementById('addBookModal'));
                    if (modal) modal.hide();
                    loadBookList();
                } else {
                    alert("新增失败: " + result);
                }
            })
            .catch(err => {
                console.error(err);
                alert("新增失败，请检查信息是否遗漏或者信息是否重复");
            });
    }

    // 初始化 + 搜索
    window.addEventListener('load', loadBookList);

    document.getElementById('searchInput').addEventListener('input', function() {
        const keyword = this.value.toLowerCase().trim();
        if (!keyword) {
            renderBookTable(booksData);
            return;
        }
        const filtered = booksData.filter(b =>
            (b.bookName && b.bookName.toLowerCase().includes(keyword)) ||
            (b.author && b.author.toLowerCase().includes(keyword))
        );
        renderBookTable(filtered);
    });
</script>
</body>
</html>