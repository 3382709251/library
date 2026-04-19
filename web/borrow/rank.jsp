<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Object loginReader = session.getAttribute("loginReader");
    String readerName = "读者";
    if (loginReader != null) {
        readerName = ((com.library.entity.Reader) loginReader).getReaderName();
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>借阅排行 - 在线图书借阅平台</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: "微软雅黑", sans-serif; }

        body { background: #f5f5f5; }

        .top-nav {
            width: 100%; height: 60px; background: #fff; border-bottom: 1px solid #eee;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1); position: sticky; top: 0; z-index: 999;
            display: flex; align-items: center; padding: 0 20px;
        }
        .nav-menu a {
            text-decoration: none; color: #333; font-size: 16px; margin: 0 20px;
            padding: 8px 12px; border-radius: 4px;
        }
        .nav-menu a:hover, .nav-menu a.active { background-color: #7da0ca; color: #fff; }

        .main-content {
            width: 1200px; margin: 0 auto; display: flex; gap: 20px; padding: 20px 0;
        }
        .category-box {
            width: 200px; background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 15px;
        }
        .category-title {
            font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #333;
            padding-bottom: 8px; border-bottom: 2px solid #7da0ca;
        }
        .category-list li { margin: 10px 0; }
        .category-list a {
            text-decoration: none; color: #666; display: block; padding: 5px 0;
        }
        .category-list a:hover, .category-list a.active { color: #7da0ca; font-weight: bold; }

        .rank-display {
            flex: 1; background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 20px;
        }
        .rank-title {
            font-size: 22px; margin-bottom: 20px; color: #333;
        }

        .book-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 20px;
        }
        .book-card {
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s;
            position: relative;
            cursor: pointer;
        }
        .book-card:hover { transform: translateY(-6px); }

        .rank-number {
            position: absolute;
            top: 12px; left: 12px;
            background: linear-gradient(135deg, #7da0ca, #5483b3);
            color: white;
            width: 32px; height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 18px;
            z-index: 10;
        }
        .book-cover {
            width: 100%;
            height: 240px;
            background-size: cover;
            background-position: center;
            background-color: #f5f5f5;
        }
        .book-info {
            padding: 12px;
        }
        .book-name {
            font-size: 15px;
            font-weight: bold;
            margin-bottom: 6px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        .book-desc {
            font-size: 13px;
            color: #666;
        }
        .borrow-count {
            color: #6ebade;
            font-weight: bold;
            margin-top: 4px;
        }
    </style>
</head>
<body>

<!-- 顶部导航栏 -->
<div class="top-nav">
    <div class="nav-menu">
        <a href="${pageContext.request.contextPath}/reader/main.jsp">首页</a >
        <a href="${pageContext.request.contextPath}/borrow/rank.jsp" class="active">借阅排行</a >
        <a href="${pageContext.request.contextPath}/book/list.jsp">图书列表</a >
        <a href="${pageContext.request.contextPath}/reader/myinfo.jsp"><%= readerName %>的信息</a >
        <a href="${pageContext.request.contextPath}/borrow/record.jsp">我的借阅记录</a >
    </div>
</div>

<div class="main-content">
    <!-- 左侧分类栏 -->
    <div class="category-box">
        <div class="category-title">图书分类</div>
        <ul class="category-list" id="categoryList">
            <li><a href="javascript:void(0);" class="active" data-category="">全部</a ></li>
            <li><a href="javascript:void(0);" data-category="计算机">计算机</a ></li>
            <li><a href="javascript:void(0);" data-category="java">java</a ></li>
            <li><a href="javascript:void(0);" data-category="文学">文学</a ></li>
            <li><a href="javascript:void(0);" data-category="经济">经济</a ></li>
            <li><a href="javascript:void(0);" data-category="管理">管理</a ></li>
            <li><a href="javascript:void(0);" data-category="体育">体育</a ></li>
            <li><a href="javascript:void(0);" data-category="娱乐明星">娱乐明星</a ></li>
        </ul>
    </div>

    <!-- 右侧排行区 -->
    <div class="rank-display">
        <div class="rank-title" id="rankTitle">借阅排行榜 - 全部</div>
        <div class="book-grid" id="rankGrid"></div>
    </div>
</div>

<script>
    const basePath = '${pageContext.request.contextPath}';
    const rankGrid = document.getElementById('rankGrid');
    const rankTitle = document.getElementById('rankTitle');

    function loadRank(category = '') {
        const xhr = new XMLHttpRequest();
        let url = basePath + '/borrow/rank';
        if (category) {
            url += '?category=' + encodeURIComponent(category);
        }

        xhr.open('GET', url, true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    const books = JSON.parse(xhr.responseText);
                    renderRankCards(books, category);
                } catch(e) {
                    console.error("JSON解析失败", e);
                    rankGrid.innerHTML = '<p style="color:red;text-align:center;padding:40px;">数据加载失败</p >';
                }
            }
        };
        xhr.send();
    }

    function renderRankCards(books, category) {
        rankGrid.innerHTML = '';

        if (!books || books.length === 0) {
            rankGrid.innerHTML = '<p style="text-align:center;color:#999;padding:40px;">暂无借阅记录</p >';
            return;
        }

        books.forEach((book, index) => {
            const card = document.createElement('div');
            card.className = 'book-card';
            card.onclick = function() {
                window.location.href = basePath + '/bookDetail?bookId=' + book.id;
            };

            const cover = book.coverImg || 'jinitaimei.png';

            // 使用 createElement 方式，避免 JSP EL 解析问题
            const rankNum = document.createElement('div');
            rankNum.className = 'rank-number';
            rankNum.textContent = index + 1;

            const coverDiv = document.createElement('div');
            coverDiv.className = 'book-cover';
            coverDiv.style.backgroundImage = 'url("' + basePath + '/images/' + cover + '")';

            const infoDiv = document.createElement('div');
            infoDiv.className = 'book-info';
            infoDiv.innerHTML =
                '<div class="book-name">' + (book.name || '无书名') + '</div>' +
                '<div class="book-desc">' + (book.author || '无作者') + ' 著</div>' +
                '<div class="borrow-count">借阅量：' + (book.borrowCount || 0) + ' 次</div>';

            card.appendChild(rankNum);
            card.appendChild(coverDiv);
            card.appendChild(infoDiv);
            rankGrid.appendChild(card);
        });
    }

    // 分类点击
    document.querySelectorAll('#categoryList a').forEach(function(item) {
        item.addEventListener('click', function() {
            document.querySelectorAll('#categoryList a').forEach(function(a) {
                a.classList.remove('active');
            });
            this.classList.add('active');

            const category = this.dataset.category || '';
            rankTitle.textContent = category ? '借阅排行榜 - ' + this.textContent : '借阅排行榜 - 全部';
            loadRank(category);
        });
    });

    // 初始化加载全部排行
    window.onload = function() {
        loadRank('');
    };

</script>
</body>
</html>