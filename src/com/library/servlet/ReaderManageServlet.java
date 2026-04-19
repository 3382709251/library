package com.library.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ReaderManageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 校验登录状态：管理员或读者均可访问（根据需求调整）
        Object loginAdmin = request.getSession().getAttribute("loginAdmin");
        Object loginReader = request.getSession().getAttribute("loginReader");

        // 无任何登录会话 → 跳登录页
        if (loginAdmin == null && loginReader == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. 转发到读者管理页面（路径匹配目录结构：web/reader/manage.jsp）
        request.getRequestDispatcher("/reader/manage.jsp").forward(request, response);
    }

    // 若有POST请求，补充doPost方法
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}