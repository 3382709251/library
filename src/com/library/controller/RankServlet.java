package com.library.controller;

import com.google.gson.Gson;
import com.library.dao.BookDao;
import com.library.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RankServlet extends HttpServlet {

    private BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String category = req.getParameter("category");

        List<Book> books;
        if (category == null || category.trim().isEmpty()) {
            books = bookDao.getBorrowRank();
        } else {
            books = bookDao.getBorrowRankByCategory(category);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Book b : books) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getBookId());
            map.put("name", b.getBookName());
            map.put("author", b.getAuthor());
            map.put("borrowCount", b.getBorrowedCount());
            map.put("coverImg", b.getBookImage() != null ? b.getBookImage() : "jinitaimei.png");
            result.add(map);
        }

        resp.getWriter().write(new Gson().toJson(result));
    }
}