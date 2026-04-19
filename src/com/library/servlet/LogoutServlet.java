package com.library.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        System.out.println("【LogoutServlet】 开始执行退出登录...");

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("【LogoutServlet】 Session 已成功清除");
        }

        // 关键：禁用浏览器缓存
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        String contextPath = req.getContextPath();
        System.out.println("【LogoutServlet】 重定向到登录页面: " + contextPath + "/login.jsp");

        resp.sendRedirect(contextPath + "/login.jsp");
    }
}