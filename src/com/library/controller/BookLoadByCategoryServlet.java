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

public class BookLoadByCategoryServlet extends HttpServlet {

    private BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String category = req.getParameter("category");

        List<Book> books;
        if (category == null || category.trim().isEmpty()) {
            books = bookDao.findAllBooks();
        } else {
            books = bookDao.findByBookType(category);   // 使用我们刚加的方法
        }

        // 转为前端需要的格式 + 只返回前4本 + 统一封面
        List<Map<String, Object>> result = new ArrayList<>();
        for (Book b : books) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getBookId());
            map.put("name", b.getBookName());
            map.put("author", b.getAuthor());

            String status = (b.getBorrowedCount() < b.getTotalCount()) ? "可借" : "已借完";
            map.put("status", status);

            // ★★★★★ 关键修改 + 打印日志 ★★★★★
            String coverImg = (b.getBookImage() != null && !b.getBookImage().trim().isEmpty())
                    ? b.getBookImage().trim()
                    : "jinitaimei.png";

            map.put("coverImg", coverImg);

            // 打印日志，方便我们看到实际读取到的图片名
            System.out.println("图书 [" + b.getBookId() + "] " + b.getBookName()
                    + " → 使用的封面图片: " + coverImg);

            result.add(map);
        }

        if (result.size() > 12) {
            result = result.subList(0, 12);
        }

        String json = new Gson().toJson(result);
        resp.getWriter().write(json);
        // 限制首页只显示前 4 本
        if (result.size() > 12) {
            result = result.subList(0, 12);
        }

        System.out.println("【loadByCategory】category=" + category + "，返回 " + result.size() + " 本图书");
    }
}