package com.library.servlet;

import com.library.dao.BookDao;
import com.library.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 仅处理图书详情，无复杂逻辑
 */
 // 直接映射/bookDetail
public class BookDetailServlet extends HttpServlet {
    private BookDao bookDao;

    @Override
    public void init() throws ServletException {
        super.init();
        bookDao = new BookDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. 获取图书ID
        String bookId = request.getParameter("bookId");
        if (bookId == null || bookId.trim().isEmpty()) {
            // 跳转到列表页，避免错误
            response.sendRedirect(request.getContextPath() + "/bookList");
            return;
        }

        // 2. 查询图书
        Book book = bookDao.findBookById(bookId);
        if (book == null) {
            // 无数据也跳回列表
            response.sendRedirect(request.getContextPath() + "/bookList");
            return;
        }

        // 3. 转发到详情页（确认detail.jsp在web/book/下）
        request.setAttribute("book", book);
        request.getRequestDispatcher("/book/detail.jsp").forward(request, response);
    }
}