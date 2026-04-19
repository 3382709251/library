package com.library.servlet;

import com.library.dao.AdminBookDao;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class AdminBookStatusServlet extends HttpServlet {

    private final AdminBookDao adminBookDao = new AdminBookDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String bookId = request.getParameter("bookId");
        String status = request.getParameter("status");

        System.out.println("=== AdminBookStatusServlet 被调用 ===");
        System.out.println("bookId = " + bookId + ", status = " + status);

        if (bookId == null || bookId.trim().isEmpty() || status == null) {
            out.print("失败：参数不能为空");
            return;
        }

        try {
            boolean success = adminBookDao.updateBookStatus(bookId, status);
            if (success) {
                out.print("成功：图书已" + status);
            } else {
                out.print("失败：更新失败，图书可能不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("失败：系统异常");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}