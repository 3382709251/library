package com.library.servlet;

import com.library.dao.BookDao;
import com.library.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BookListServlet extends HttpServlet {
    private BookDao bookDao;

    @Override
    public void init() throws ServletException {
        super.init();
        bookDao = new BookDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 编码设置
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 2. 获取搜索/筛选参数
        String bookType = request.getParameter("bookType");
        String keyword = request.getParameter("keyword");

        // 3. 处理空参数
        if (bookType != null && bookType.trim().isEmpty()) bookType = null;
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;

        // 4. 查询数据
        List<Book> bookList;
        if (bookType == null && keyword == null) {
            bookList = bookDao.findAllBooks();
        } else {
            bookList = bookDao.getBooksByFilter(bookType, keyword);
        }

        // 5. 存入request
        request.setAttribute("bookList", bookList);
        request.setAttribute("selectedBookType", bookType);
        request.setAttribute("inputKeyword", keyword);

        // 6. 转发到list.jsp（重点：确认你的list.jsp在web/book/下）
        // 绝对路径，100%不会错
        request.getRequestDispatcher("/book/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}