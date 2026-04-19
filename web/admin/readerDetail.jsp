<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>读者借阅详情</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; }
        .card { box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .fine-amount { font-weight: bold; }
        .table th { background-color: #f1f3f5; }
        .status-applied { color: #ffc107; font-weight: bold; } /* 已申请归还 */
        .status-confirmed { color: #198754; font-weight: bold; } /* 已确认归还 */
        /* 修复：逾期天数分级样式（区分15-30天、超30天） */
        .fine-orange { color: #fd7e14; } /* 15-30天橙色 */
        .fine-red { color: #dc3545; } /* 超30天红色 */
        .fine-normal { color: #6c757d; } /* 0-15天灰色（新增，兼容正常状态） */
        .due-date-orange { color: #fd7e14; font-weight: bold; } /* 15-30天逾期提醒 */
        .due-date-red { color: #dc3545; font-weight: bold; } /* 超30天逾期提醒 */
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>读者借阅详情</h2>
        <a href="${pageContext.request.contextPath}/admin/readerList.jsp" class="btn btn-outline-secondary">← 返回读者列表</a>
    </div>

    <!-- 读者信息卡片 -->
    <div class="card mb-4">
        <div class="card-header bg-light">
            <h5 class="mb-0">读者信息</h5>
        </div>
        <div class="card-body">
            <div class="row">
                <div class="col-md-6">
                    <p><strong>读者ID：</strong> <span id="showId" class="fs-5"></span></p>
                </div>
                <div class="col-md-6">
                    <p><strong>姓名：</strong> <span id="showName" class="fs-5"></span></p>
                </div>
                <div class="col-md-12 mt-2">
                    <p><strong>读者总罚款金额：</strong> <span id="totalFine" class="fs-5 fine-amount">¥0.00</span></p>
                </div>
            </div>
        </div>
    </div>

    <!-- 借阅记录 -->
    <div class="card">
        <div class="card-header bg-light">
            <h5 class="mb-0">借阅记录</h5>
        </div>
        <div class="card-body p-0">
            <table class="table table-hover mb-0">
                <thead>
                <tr>
                    <th>图书名称</th>
                    <th>借阅时间</th>
                    <th>应还时间</th>
                    <th>逾期天数</th> <!-- 新增：逾期天数列 -->
                    <th>归还申请状态</th>
                    <th class="text-end">单本罚款金额</th> <!-- 修改：列名更清晰 -->
                    <th class="text-center">操作</th>
                </tr>
                </thead>
                <tbody id="borrowTableBody"></tbody>
            </table>
        </div>
    </div>
</div>

<script>
    (function() {
        const basePath = '${pageContext.request.contextPath}';
        const urlParams = new URLSearchParams(window.location.search);
        const readerId = urlParams.get('readerId');

        if (!readerId) {
            alert("读者ID不能为空！");
            window.location.href = basePath + '/admin/readerList.jsp';
            return;
        }

        document.getElementById('showId').textContent = readerId;

        // 请求后端接口获取读者借阅记录
        fetch(basePath + '/admin/readerDetail?readerId=' + encodeURIComponent(readerId))
            .then(res => {
                if (!res.ok) throw new Error('接口请求失败：' + res.status);
                return res.json();
            })
            .then(data => {
                // 修改：后端返回数据结构调整为 {records: [], totalFine: 0}
                const records = data.records || [];
                const totalFine = data.totalFine || 0;

                console.log("📥 收到借阅记录数量:", records.length);
                console.log("📥 读者总罚款:", totalFine);

                // 展示总罚款
                document.getElementById('totalFine').textContent = '¥' + parseFloat(totalFine).toFixed(2);
                // 给总罚款加颜色（超30天总罚款标红，15-30天标橙）
                if (totalFine > 20) {
                    document.getElementById('totalFine').classList.add('fine-red');
                } else if (totalFine > 0) {
                    document.getElementById('totalFine').classList.add('fine-orange');
                }

                if (records.length > 0) {
                    const first = records[0];
                    document.getElementById('showName').textContent = first.readerName || first.reader_name || '未知';
                }

                const tbody = document.getElementById('borrowTableBody');
                tbody.innerHTML = '';

                if (records.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">该读者暂无借阅记录</td></tr>'; // 修改：列数从6→7
                    return;
                }

                // 日期格式化函数（只定义一次，全局使用）
                function formatDate(dateStr) {
                    if (!dateStr) return '';
                    const date = new Date(dateStr);
                    if (isNaN(date.getTime())) return dateStr; // 兼容无效日期
                    return date.toLocaleDateString('zh-CN', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                    });
                }

                // 计算应还时间（30天）
                function getDueDate(borrowDateStr) {
                    if (!borrowDateStr) return '未知应还时间';
                    let borrowDate = new Date(borrowDateStr);
                    if (isNaN(borrowDate.getTime())) return '未知应还时间';
                    borrowDate.setTime(borrowDate.getTime() + 30 * 24 * 60 * 60 * 1000);
                    return formatDate(borrowDate);
                }

                // 新增：计算逾期天数和罚款金额
                function calculateFine(borrowDateStr, returnDateStr) {
                    // 已归还则罚款为0
                    if (returnDateStr) return { overdueDays: 0, fineAmount: 0, fineClass: '' };

                    const borrowDate = new Date(borrowDateStr);
                    const dueDate = new Date(borrowDate);
                    dueDate.setTime(dueDate.getTime() + 30 * 24 * 60 * 60 * 1000); // 应还时间=借阅+30天
                    const now = new Date();

                    if (isNaN(borrowDate.getTime())) return { overdueDays: 0, fineAmount: 0, fineClass: '' };

                    // 计算逾期天数（当前时间 - 应还时间）
                    const overdueMs = now - dueDate;
                    const overdueDays = Math.ceil(overdueMs / (24 * 60 * 60 * 1000));
                    let fineAmount = 0;
                    let calcFineClass = ''; // 重命名：避免和外层变量冲突

                    if (overdueDays <= 0) {
                        fineAmount = 0;
                    } else if (overdueDays <= 15) {
                        fineAmount = 5;
                        calcFineClass = 'fine-orange';
                    } else if (overdueDays <= 30) {
                        fineAmount = 5 + (overdueDays - 15);
                        calcFineClass = 'fine-orange';
                    } else {
                        fineAmount = 30;
                        calcFineClass = 'fine-red';
                    }

                    // 确保金额为正数
                    fineAmount = Math.max(0, fineAmount);
                    return { overdueDays, fineAmount, calcFineClass, dueDate: formatDate(dueDate) };
                }

                records.forEach(function(record) {
                    console.log('=== 单条完整记录 ===', record);
                    console.log('所有字段名:', Object.keys(record));

                    // 更健壮地获取 returnApplyStatus
                    let returnApplyStatus = 0;
                    const rawStatus = record.returnApplyStatus || record.return_apply_status || record["returnApplyStatus"] || record.returnApplystatus;
                    if (rawStatus != null && rawStatus !== '') {
                        returnApplyStatus = Number(rawStatus) || 0;
                    }

                    console.log(`记录ID=${record.id} | 原始rawStatus=${rawStatus} | 处理后 returnApplyStatus = ${returnApplyStatus}`);

                    // 新增：计算罚款和逾期天数
                    const { overdueDays, fineAmount, calcFineClass, dueDate } = calculateFine(
                        record.borrow_date || record.borrowTime,
                        record.return_date || record.returnDate || record.returnTime
                    );

                    // 修改：优先使用计算后的罚款，兼容数据库字段
                    let fine = record.fine_amount || fineAmount;
                    // 同步更新到record（供后续使用）
                    record.fine_amount = fine;

                    let recordId = record.id;
                    let bookId = String(record.bookId || record.book_id || record.bookID || '').trim();

                    // 处理状态显示
                    let statusText = '';
                    if (returnApplyStatus === 1) {
                        statusText = '<span class="status-applied">已申请归还（待确认）</span>';
                    } else if (returnApplyStatus === 2) {
                        statusText = '<span class="status-confirmed">已确认归还</span>';
                    } else {
                        statusText = '未申请';
                    }

                    // 处理应还时间样式
                    let dueDateHtml = '';
                    if (record.return_date || record.returnDate || record.returnTime) {
                        dueDateHtml = '已归还（' + formatDate(record.return_date || record.returnDate || record.returnTime) + '）';
                    } else {
                        // 新增：应还时间加颜色
                        dueDateHtml = `<span class="${calcFineClass.replace('fine-', 'due-date-')}">${dueDate}</span>`;
                    }

                    // 处理按钮 - 使用 data 属性 + 事件委托
                    let operateBtn = '';
                    if (returnApplyStatus === 1) {
                        let safeBookId = String(bookId || '').trim();
                        operateBtn = '<button class="btn btn-sm btn-success confirm-return-btn" ' +
                            'data-record-id="' + recordId + '" ' +
                            'data-book-id="' + safeBookId + '">确认归还</button>';

                        console.log(`✅ 记录 ${recordId} 生成按钮，bookId="${safeBookId}"`);
                    } else {
                        operateBtn = '<span class="text-muted">无操作</span>';
                    }

                    // 构建行
                    var row = document.createElement('tr');
                    // 核心1：读取后端返回的真实逾期天数和罚款
                    var actualOverdueDays = record.overdueDays || 0;
                    var actualFine = record.fineAmount || 0;

                    // 核心2：根据逾期天数动态设置样式类（变量名改为 rowFineClass，避免重复）
                    var rowFineClass = 'fine-normal'; // 默认灰色
                    var dueDateClass = '';
                    if (actualOverdueDays >= 15 && actualOverdueDays <= 30) {
                        rowFineClass = 'fine-orange'; // 15-30天橙色
                        dueDateClass = 'due-date-orange';
                    } else if (actualOverdueDays > 30) {
                        rowFineClass = 'fine-red'; // 超30天红色
                        dueDateClass = 'due-date-red';
                    }

                    // 核心3：渲染行（关联样式类+真实数值）
                    row.innerHTML =
                        '<td>' + (record.bookName || record.book_name || '未知图书') + '</td>' +
                        '<td>' + formatDate(record.borrow_date || record.borrowTime) + '</td>' +
                        '<td class="' + dueDateClass + '">' + dueDateHtml + '</td>' + // 逾期日期加样式
                        '<td>' + (actualOverdueDays > 0 ? actualOverdueDays + ' 天' : '0 天') + '</td>' +
                        '<td>' + statusText + '</td>' +
                        '<td class="text-end fine-amount ' + rowFineClass + '">¥' + parseFloat(actualFine).toFixed(2) + '</td>' + // 罚款加颜色
                        '<td class="text-center">' + operateBtn + '</td>';
                    tbody.appendChild(row);
                });
            })
            .catch(function(err) {
                console.error('❌ 加载借阅记录失败：', err);
                alert("加载借阅记录失败，请刷新页面重试");
            });

        // 管理员确认归还函数（无修改，保留）
        window.confirmReturn = function(recordId, bookId) {
            bookId = String(bookId || '').trim();

            console.log(`[前端] confirmReturn 被调用 → recordId=${recordId}, bookId="${bookId}"`);

            if (!recordId || !bookId) {
                alert("参数错误：记录ID或图书ID为空！");
                return;
            }

            if (!confirm('确认该读者已线下归还图书？\n确认后将更新归还状态和图书库存！')) {
                return;
            }

            const basePath = '${pageContext.request.contextPath}';

            const xhr = new XMLHttpRequest();
            xhr.open('POST', basePath + '/admin/confirmReturnBook', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        try {
                            const result = JSON.parse(xhr.responseText);
                            console.log('[前端] 后端返回结果:', result);
                            if (result.success) {
                                alert('✅ 确认归还成功！');
                                location.reload();
                            } else {
                                alert('❌ 确认失败：' + (result.message || '未知错误'));
                            }
                        } catch(e) {
                            alert('返回数据格式错误');
                        }
                    } else {
                        alert('请求失败，状态码: ' + xhr.status);
                    }
                }
            };

            const params = 'recordId=' + recordId + '&bookId=' + encodeURIComponent(bookId);
            console.log('[前端] 发送的参数:', params);
            xhr.send(params);
        };
        // 事件委托 - 统一处理所有确认归还按钮点击（无修改，保留）
        document.addEventListener('click', function(e) {
            if (e.target.classList.contains('confirm-return-btn')) {
                const recordId = e.target.getAttribute('data-record-id');
                const bookId = e.target.getAttribute('data-book-id');

                console.log(`按钮被点击 → recordId=${recordId}, bookId=${bookId}`);

                if (!confirm('确认该读者已线下归还图书？\n确认后将更新归还状态和图书库存！')) {
                    return;
                }

                const basePath = '${pageContext.request.contextPath}';

                const xhr = new XMLHttpRequest();
                xhr.open('POST', basePath + '/admin/confirmReturnBook', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            try {
                                const result = JSON.parse(xhr.responseText);
                                if (result.success) {
                                    alert('✅ 确认归还成功！');
                                    location.reload();
                                } else {
                                    alert('❌ 确认失败：' + (result.message || '未知错误'));
                                }
                            } catch(e) {
                                alert('返回数据格式错误');
                            }
                        } else {
                            alert('请求失败，状态码: ' + xhr.status);
                        }
                    }
                };

                xhr.send('recordId=' + recordId + '&bookId=' + encodeURIComponent(bookId));
            }
        });
    })();
</script>
</body>
</html>