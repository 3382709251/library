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


public class Top3Servlet extends HttpServlet {

    private BookDao bookDao = new BookDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. 统一设置响应编码和类型，避免中文乱码
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 2. 初始化结果集合，避免空指针
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 只获取借阅排行前3名
            List<Book> top3Books = bookDao.getBorrowRankTop3();

            // 3. 判空处理：如果查询结果为空，直接返回空数组，避免遍历空集合
            if (top3Books != null && !top3Books.isEmpty()) {
                for (Book b : top3Books) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", b.getBookId());
                    map.put("name", b.getBookName());
                    map.put("author", b.getAuthor());

                    // 修复核心：int类型无需判null，直接取值即可
                    map.put("borrowCount", b.getBorrowedCount());

                    // 引用类型（String）正常判null
                    map.put("coverImg", b.getBookImage() != null ? b.getBookImage() : "jinitaimei.png");
                    result.add(map);
                }
            }
        } catch (Exception e) {
            // 4. 异常捕获：避免程序崩溃，返回友好提示（可选）
            e.printStackTrace();
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("code", 500);
            errorMap.put("msg", "获取借阅排行失败");
            result.add(errorMap);
        }

        // 5. 转换为JSON并返回
        resp.getWriter().write(new Gson().toJson(result));
    }
}