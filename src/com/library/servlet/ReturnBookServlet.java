package com.library.servlet;

import com.library.dao.BorrowDao;
import com.library.entity.Reader;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ReturnBookServlet extends HttpServlet {

    private BorrowDao borrowDao = new BorrowDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = req.getSession(false);   // 不自动创建新session
            Reader reader = (Reader) session.getAttribute("loginReader");

            System.out.println("=== 归还请求开始 ===");
            System.out.println("读者登录状态: " + (reader != null ? "已登录" : "未登录"));

            if (reader == null) {
                result.put("success", false);
                result.put("message", "请先登录！");
                System.out.println("归还失败：未登录");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }

            String bookId = req.getParameter("bookId");
            System.out.println("请求归还的图书ID: " + bookId);

            if (bookId == null || bookId.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "图书ID不能为空！");
                System.out.println("归还失败：bookId为空");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }

            // 执行归还
            System.out.println("开始调用 borrowDao.returnBook...");
            boolean success = borrowDao.returnBook(reader.getReaderId(), bookId);
            System.out.println("returnBook 返回结果: " + success);

            if (success) {
                result.put("success", true);
                result.put("message", "归还成功！");
                System.out.println("归还成功！");
            } else {
                result.put("success", false);
                result.put("message", "归还失败！您可能没有借阅该书或已归还");
                System.out.println("归还失败 - Dao返回false");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("归还过程中发生异常: " + e.getMessage());
            result.put("success", false);
            result.put("message", "系统异常：" + e.getMessage());
        }

        String jsonResponse = new Gson().toJson(result);
        System.out.println("返回给前端的JSON: " + jsonResponse);
        resp.getWriter().write(jsonResponse);
    }
}