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

public class ReaderUpdateInfoServlet extends HttpServlet {

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

            // 获取参数
            String readerName = req.getParameter("readerName");
            String college = req.getParameter("college");
            String readerPhone = req.getParameter("readerPhone");

            if (readerName == null || readerName.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "姓名不能为空！");
                objectMapper.writeValue(resp.getWriter(), result);
                return;
            }

            // 执行更新
            boolean success = readerDao.updateReaderInfo(
                    loginReader.getReaderId(),
                    readerName.trim(),
                    college != null ? college.trim() : null,
                    readerPhone != null ? readerPhone.trim() : null
            );

            if (success) {
                // 更新 Session 中的用户信息
                loginReader.setReaderName(readerName.trim());
                loginReader.setCollege(college != null ? college.trim() : null);
                loginReader.setReaderPhone(readerPhone != null ? readerPhone.trim() : null);
                session.setAttribute("loginReader", loginReader);

                result.put("success", true);
                result.put("message", "基本信息修改成功！");
            } else {
                result.put("success", false);
                result.put("message", "修改失败，请稍后重试！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统异常，请稍后重试！");
        }

        objectMapper.writeValue(resp.getWriter(), result);
    }
}