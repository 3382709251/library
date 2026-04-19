package com.library.servlet;

import com.library.entity.Reader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class MyInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置编码
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        Reader loginReader = (Reader) session.getAttribute("loginReader");

        // 未登录跳登录页
        if (loginReader == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // ✅ 关键：把用户信息放入 request，JSP才能拿到
        req.setAttribute("reader", loginReader);

        // 转发到个人信息页面
        req.getRequestDispatcher("/reader/myinfo.jsp").forward(req, resp);
    }
}