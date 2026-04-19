package com.library.servlet;

import com.library.dao.ReaderDao;
import com.library.entity.Reader;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReaderChangePasswordServlet extends HttpServlet {

    private final ReaderDao readerDao = new ReaderDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        resp.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();


        try {
            HttpSession session = req.getSession();
            Reader loginReader = (Reader) session.getAttribute("loginReader");

            if (loginReader == null) {
                result.put("success", false);
                result.put("message", "请先登录！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            String oldPassword = req.getParameter("oldPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (oldPassword == null || newPassword == null || confirmPassword == null ||
                    oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "所有密码字段不能为空！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                result.put("success", false);
                result.put("message", "两次输入的新密码不一致！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            if (newPassword.trim().length() < 6) {
                result.put("success", false);
                result.put("message", "新密码长度不能少于6位！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 执行修改密码
            boolean success = readerDao.changePassword(
                    loginReader.getReaderId(),
                    oldPassword.trim(),
                    newPassword.trim()
            );

            if (success) {
                result.put("success", true);
                result.put("message", "密码修改成功！");
                // 不强制退出登录（按你的要求）
            } else {
                result.put("success", false);
                result.put("message", "旧密码错误，修改失败！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统异常，请稍后重试！");
        }

        objectMapper.writeValue(resp.getWriter(), result);
    }
}