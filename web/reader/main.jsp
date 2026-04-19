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
    <title>在线图书借阅平台 - 读者首页</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; font-family: "微软雅黑", "宋体", sans-serif; }
        .top-nav { width: 100%; height: 60px; background: #fff; border-bottom: 1px solid #eee; box-shadow: 0 2px 5px rgba(0,0,0,0.1); position: sticky; top: 0; z-index: 999; display: flex; align-items: center; padding: 0 20px; }
        .nav-menu { display: flex; flex: 1; }
        .nav-menu a { text-decoration: none; color: #333; font-size: 16px; margin: 0 20px; padding: 8px 12px; border-radius: 4px; }
        .nav-menu a:hover, .nav-menu a.active { background-color: #67c23a; color: #fff; }
        .user-info { display: flex; align-items: center; cursor: pointer; }
        .user-avatar { width: 40px; height: 40px; border-radius: 50%; background: #ddd; background-image: url("${pageContext.request.contextPath}/images/avatar.png"); background-size: cover; margin-right: 10px; }
        .user-name { font-size: 14px; color: #666; }

        .carousel {
            width: 100%;
            height: 400px;
            overflow: hidden;
            position: relative;
            margin: 10px 0;
            background: #f5f5f5;
            border: 1px solid #eee;
        }
        .carousel-inner { position: relative; width: 100%; height: 100%; }
        .carousel-item {
            position: absolute;
            top: 0; left: 0;
            width: 100%; height: 100%;
            opacity: 0;
            transition: opacity 0.6s ease;
        }
        .carousel-item.active { opacity: 1; z-index: 2; }
        .carousel-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            display: block;
        }
        .carousel-indicators {
            position: absolute;
            bottom: 20px; left: 50%; transform: translateX(-50%);
            display: flex; gap: 10px; z-index: 10;
        }
        .indicator {
            width: 12px; height: 12px; border-radius: 50%;
            background: rgba(255,255,255,0.7); cursor: pointer;
            transition: all 0.3s;
        }
        .indicator.active { background: #67c23a; transform: scale(1.2); }
        .main-content { width: 1200px; margin: 0 auto; display: flex; gap: 20px; padding: 20px 0; }
        .category-box { width: 200px; background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 15px; }
        .category-title { font-size: 18px; font-weight: bold; margin-bottom: 15px; color: #333; padding-bottom: 8px; border-bottom: 2px solid #67c23a; }
        .category-list { list-style: none; }
        .category-list li { margin: 10px 0; }
        .category-list li a { text-decoration: none; color: #666; font-size: 14px; padding: 5px 0; display: block; }
        .category-list li a:hover, .category-list li a.active { color: #67c23a; font-weight: bold; }

        .book-display { flex: 1; background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 20px; }
        .book-display-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .book-display-title { font-size: 18px; font-weight: bold; color: #333; }
        .more-btn { padding: 6px 12px; background: #67c23a; color: #fff; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; }
        .more-btn:hover { background: #5daf34; }
        .book-grid { display: grid; grid-template-columns: repeat(4,1fr); gap: 20px; }
        .book-card { border-radius: 4px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); transition: transform 0.3s; cursor: pointer; }
        .book-card:hover { transform: translateY(-5px); }
        .book-cover { width: 100%; height: 220px; background: #f5f5f5; background-size: cover; background-position: center; }
        .book-info { padding: 10px; }
        .book-name { font-size: 14px; color: #333; margin-bottom: 5px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .book-desc { font-size: 12px; color: #999; }
    </style>
</head>
<body>

<div class="top-nav">
    <div class="nav-menu">
        <a href=" " class="active">首页</a >
        <a href="${pageContext.request.contextPath}/borrow/rank.jsp">借阅排行</a >
        <a href="${pageContext.request.contextPath}/book/list.jsp">图书列表</a >
        <a href="${pageContext.request.contextPath}/reader/myinfo.jsp"><%= readerName %>的信息</a >
        <a href="${pageContext.request.contextPath}/borrow/record.jsp">我的借阅记录</a >
        <a href="${pageContext.request.contextPath}/logout"
           onclick="return confirm('确定要退出登录吗？');"
           style="margin-left: 20px;">
            退出登录
        </a >
    </div>
</div>

<!-- 轮播图 - 动态显示借阅排行前3名 -->
<div class="carousel" id="carousel">
    <!-- 由JS动态生成 -->
</div>

<div class="main-content">
    <div class="category-box">
        <div class="category-title">图书分类</div>
        <ul class="category-list">
            <li><a href="javascript:void(0);" class="active parent-category" data-category="计算机">计算机</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="经济">经济</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="文学">文学</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="管理">管理</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="java">java</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="体育传记">体育传记</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="体育">体育</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="计算机编程">计算机编程</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="人工智能">人工智能</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="古典文学">古典文学</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="外国文学">外国名著</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="投资理财">投资理财</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="自然科学">自然</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="心理学">心理</a ></li>
            <li><a href="javascript:void(0);" class="parent-category" data-category="学习方法">学习方法</a ></li>
        </ul>
    </div>

    <div class="book-display">
        <div class="book-display-header">
            <div class="book-display-title" id="bookTitle">计算机</div>
            <a href="${pageContext.request.contextPath}/book/list.jsp" class="more-btn">更多&gt;&gt;</a >
        </div>
        <div class="book-grid" id="bookGrid"></div>
    </div>
</div>

<script>
    console.log("=== 读者首页 JS 开始执行 ===");

    const basePath = '${pageContext.request.contextPath}';
    const bookGrid = document.getElementById('bookGrid');
    const bookTitle = document.getElementById('bookTitle');
    const moreBtn = document.getElementById('moreBtn');

    // ==================== 轮播图 - 最终干净美观版（点击整张图片跳转） ====================
    let top3BooksData = [];

    function loadTop3Carousel() {
        console.log("=== 开始请求轮播图前3名数据... ===");
        var xhr = new XMLHttpRequest();
        xhr.open('GET', basePath + '/borrow/top3', true);

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    var top3Books = JSON.parse(xhr.responseText);
                    top3BooksData = top3Books || [];
                    console.log("✅ 收到数据，共 " + top3BooksData.length + " 条");
                    renderTop3Carousel(top3BooksData);
                } catch(e) {
                    console.error("JSON解析失败", e);
                }
            }
        };
        xhr.send();
    }

    function renderTop3Carousel(books) {
        var carousel = document.getElementById('carousel');
        if (!carousel) return;

        var itemsHTML = '';
        var indicatorsHTML = '';

        for (var i = 0; i < books.length; i++) {
            var book = books[i];
            var bookName = book.name || book.bookName || '未知图书';
            var bookId = book.bookId || book.id || '';
            var cover = book.coverImg || book.cover || 'jinitaimei.png';

            // 清理可能的路径
            if (cover && cover.includes('/')) cover = cover.split('/').pop();

            itemsHTML += '<div class="carousel-item ' + (i===0 ? 'active' : '') + '" data-index="' + i + '">' +
                '<img src="' + basePath + '/images/' + cover + '" alt="' + bookName + '" ' +
                'style="width:100%; height:100%; object-fit:cover; cursor:pointer;" ' +
                'onerror="this.onerror=null; this.src=\'' + basePath + '/images/jinitaimei.png\';">' +
                '</div>';

            indicatorsHTML += '<div class="indicator ' + (i===0 ? 'active' : '') + '" data-index="' + i + '"></div>';
        }

        carousel.innerHTML =
            '<div class="carousel-inner">' + itemsHTML + '</div>' +
            '<div class="carousel-indicators">' + indicatorsHTML + '</div>';

        initCarouselFinal();
        console.log("✅ 干净轮播图已渲染（点击图片跳转）");
    }

    function initCarouselFinal() {
        var carousel = document.getElementById('carousel');
        var items = carousel.querySelectorAll('.carousel-item');
        var indicators = carousel.querySelectorAll('.indicator');
        var currentIndex = 0;
        var timer = null;

        function switchTo(index) {
            items.forEach(function(item) { item.classList.remove('active'); });
            indicators.forEach(function(ind) { ind.classList.remove('active'); });
            items[index].classList.add('active');
            indicators[index].classList.add('active');
            currentIndex = index;
        }

        // 自动轮播
        function startAuto() {
            if (timer) clearInterval(timer);
            timer = setInterval(function() {
                currentIndex = (currentIndex + 1) % items.length;
                switchTo(currentIndex);
            }, 4000);
        }
        startAuto();

        // 点击指示器切换
        indicators.forEach(function(ind, idx) {
            ind.addEventListener('click', function() {
                clearInterval(timer);
                switchTo(idx);
                startAuto();
            });
        });

        // === 核心：点击整张图片跳转 ===
        carousel.addEventListener('click', function(e) {
            var item = e.target.closest('.carousel-item');
            if (item) {
                var index = parseInt(item.getAttribute('data-index'));
                var book = top3BooksData[index];

                if (book && (book.bookId || book.id)) {
                    var bookId = book.bookId || book.id;
                    console.log("点击轮播图第 " + (index + 1) + " 张 → 书名: " + (book.name || book.bookName) + ", bookId: " + bookId);
                    window.location.href = basePath + '/bookDetail?bookId=' + bookId;
                }
            }
        });

        console.log("✅ 轮播图初始化完成（点击图片跳转版）");
    }
    // ==================== 图书加载（显示12张） ====================
    function loadBooks(category) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', basePath + '/book/loadByCategory?category=' + encodeURIComponent(category), true);

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                try {
                    let books = JSON.parse(xhr.responseText);
                    if (books.length > 12) books = books.slice(0, 12);
                    renderBookCards(books);
                } catch(e) {
                    console.error("JSON解析失败", e);
                }
            }
        };
        xhr.send();
    }

    function renderBookCards(books) {
        bookGrid.innerHTML = '';

        if (!books || books.length === 0) {
            bookGrid.innerHTML = '<p style="text-align:center;color:#999;">该分类暂无图书</p >';
            return;
        }

        books.forEach(book => {
            const card = document.createElement('div');
            card.className = 'book-card';

            card.onclick = function() {
                window.location.href = basePath + '/bookDetail?bookId=' + book.id;
            };

            const cover = book.coverImg || 'jinitaimei.png';
            const imgUrl = basePath + '/images/' + cover;

            const coverDiv = document.createElement('div');
            coverDiv.className = 'book-cover';
            coverDiv.style.backgroundImage = 'url("' + imgUrl + '")';
            coverDiv.style.height = '180px';
            coverDiv.style.backgroundSize = 'contain';
            coverDiv.style.backgroundColor = '#f5f5f5';

            const infoDiv = document.createElement('div');
            infoDiv.className = 'book-info';
            infoDiv.innerHTML =
                '<div class="book-name">' + (book.name || '无书名') + '</div>' +
                '<div class="book-desc">' + (book.author || '无作者') + ' 著 | ' + (book.status || '可借') + '</div>';

            card.appendChild(coverDiv);
            card.appendChild(infoDiv);
            bookGrid.appendChild(card);
        });
    }

    // 初始化
    window.onload = function() {
        loadTop3Carousel();   // 加载轮播图前3名
        loadBooks('java');    // 加载首页默认分类
    };

    // 分类点击
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('parent-category')) {
            document.querySelectorAll('.parent-category').forEach(p => p.classList.remove('active'));
            e.target.classList.add('active');

            const category = e.target.dataset.category;
            loadBooks(category);
            if (bookTitle) bookTitle.textContent = e.target.textContent.trim();
            if (moreBtn) moreBtn.href = basePath + '/book/list?category=' + encodeURIComponent(category);
        }
    });
</script>
</body>
</html>