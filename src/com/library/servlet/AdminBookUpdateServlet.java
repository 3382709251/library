package com.library.servlet;   // 修改为你的实际包名

import com.library.dao.AdminBookDao;
import com.library.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminBookUpdateServlet extends HttpServlet {

    private final AdminBookDao adminBookDao = new AdminBookDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        try {
            String bookId = request.getParameter("bookId");
            String bookName = request.getParameter("bookName");
            String author = request.getParameter("author");
            String publisher = request.getParameter("publisher");
            String bookType = request.getParameter("bookType");
            String totalCountStr = request.getParameter("totalCount");
            String priceStr = request.getParameter("price");
            String publishYearStr = request.getParameter("publishYear");
            String bookIntroduce = request.getParameter("bookIntroduce");

            System.out.println("【AdminBookUpdateServlet】收到参数 - bookId=" + bookId +
                    ", bookName=" + bookName + ", totalCount=" + totalCountStr);

            if (bookId == null || bookId.trim().isEmpty()) {
                response.getWriter().write("失败：图书ID不能为空");
                return;
            }

            int totalCount = Integer.parseInt(totalCountStr != null ? totalCountStr.trim() : "1");
            double price = priceStr != null && !priceStr.trim().isEmpty() ? Double.parseDouble(priceStr.trim()) : 0.0;
            int publishYear = publishYearStr != null && !publishYearStr.trim().isEmpty() ? Integer.parseInt(publishYearStr.trim()) : 2024;

            Book book = new Book();
            book.setBookId(bookId.trim());
            book.setBookName(bookName != null ? bookName.trim() : "");
            book.setAuthor(author != null ? author.trim() : "");
            book.setPublisher(publisher != null ? publisher.trim() : "");
            book.setBookType(bookType != null ? bookType.trim() : "");
            book.setTotalCount(totalCount);
            book.setPrice(price);
            book.setPublishYear(publishYear);
            book.setBookIntroduce(bookIntroduce != null ? bookIntroduce.trim() : "");

            boolean success = adminBookDao.updateBook(book);

            if (success) {
                response.getWriter().write("成功：图书信息修改成功！");
            } else {
                response.getWriter().write("失败：数据库更新失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("失败：系统异常 - " + e.getMessage());
        }
    }
}