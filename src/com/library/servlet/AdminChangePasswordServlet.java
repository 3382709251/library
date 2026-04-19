package com.library.servlet;

import com.library.dao.AdminDao;
import com.library.entity.Admin;
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


public class AdminChangePasswordServlet extends HttpServlet {

    // 完全复刻读者版的成员变量声明方式
    private final AdminDao adminDao = new AdminDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String oldPassword = req.getParameter("oldPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        System.out.println("接收参数：oldPassword=" + oldPassword + ", newPassword=" + newPassword + ", confirmPassword=" + confirmPassword);

        // 编码和响应格式完全和读者版一致
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取当前登录管理员（对应读者版的loginReader）
            HttpSession session = req.getSession();
            Admin loginAdmin = (Admin) session.getAttribute("loginAdmin");

            // 2. 未登录校验（和读者版逻辑一致）
            if (loginAdmin == null) {
                result.put("success", false);
                result.put("message", "请先登录！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 4. 非空校验（复刻读者版的校验逻辑）
            if (oldPassword == null || newPassword == null || confirmPassword == null ||
                    oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "所有密码字段不能为空！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 5. 新密码一致性校验（和读者版一致）
            if (!newPassword.equals(confirmPassword)) {
                result.put("success", false);
                result.put("message", "两次输入的新密码不一致！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 6. 新密码长度校验（和读者版一致）
            if (newPassword.trim().length() < 6) {
                result.put("success", false);
                result.put("message", "新密码长度不能少于6位！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 7. 执行修改密码（对应读者版的changePassword
            String adminPwd = loginAdmin.getLoginPassword();

            if (adminPwd == null) {
                result.put("success", false);
                result.put("message", "当前账号密码信息异常，请重新登录！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }
            boolean oldPwdCorrect = false;
            if (adminPwd != null) {
                oldPwdCorrect = adminPwd.equals(oldPassword.trim());
            }
// 新增：打印调试信息
            System.out.println("会话中管理员密码：" + adminPwd + "，前端传入旧密码：" + oldPassword);
            if (!oldPwdCorrect) {
                result.put("success", false);
                result.put("message", "旧密码错误，修改失败！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 调用DAO更新密码
            // 替换掉原来的旧密码校验逻辑，直接这一行代码
            boolean success = adminDao.changePassword(
                    loginAdmin.getAdminId(),
                    oldPassword.trim(),
                    newPassword.trim()
            );

            // 8. 返回结果（和读者版提示文案一致）
            if (success) {
                result.put("success", true);
                result.put("message", "密码修改成功！");
                // 可选：如果需要和读者版一样不退出登录，注释下面这行
                // session.invalidate(); // 强制退出登录（如需保留登录则删除）
            } else {
                result.put("success", false);
                result.put("message", "密码修改失败，请稍后重试！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统异常，请稍后重试！");
        }

        // 最终返回JSON结果（和读者版一致）
        objectMapper.writeValue(resp.getWriter(), result);
    }
}