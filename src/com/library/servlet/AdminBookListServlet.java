package com.library.servlet;

import com.library.dao.AdminBookDao;
import com.library.entity.Book;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminBookListServlet extends HttpServlet {

    private AdminBookDao adminBookDao = new AdminBookDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            List<Book> books = adminBookDao.getAllBooks();

            System.out.println("【AdminBookListServlet】查询到图书数量: " + books.size());
            if (!books.isEmpty()) {
                System.out.println("第一本书: " + books.get(0).getBookName());
            }

            Gson gson = new Gson();
            String json = gson.toJson(books);
            resp.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"查询图书列表失败: " + e.getMessage() + "\"}");
        }
    }
}