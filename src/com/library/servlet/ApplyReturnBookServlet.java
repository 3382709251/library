package com.library.servlet;

import com.library.dao.BorrowDao;
import com.library.entity.Reader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DatabindException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ApplyReturnBookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // 1. 校验登录
            HttpSession session = req.getSession();
            Reader reader = (Reader) session.getAttribute("loginReader");
            if (reader == null) {
                result.put("success", false);
                result.put("message", "请先登录！");
                objectMapper.writeValue(out, result);
                return;
            }

            // 2. 获取参数
            String bookId = req.getParameter("bookId");
            if (bookId == null || bookId.isEmpty()) {
                result.put("success", false);
                result.put("message", "图书ID不能为空！");
                objectMapper.writeValue(out, result);
                return;
            }

            // 3. 业务逻辑
            BorrowDao borrowDao = new BorrowDao();
            boolean applySuccess = borrowDao.applyReturnBook(reader.getReaderId(), bookId);
            if (applySuccess) {
                result.put("success", true);
                result.put("message", "归还申请提交成功");
            } else {
                result.put("success", false);
                result.put("message", "提交失败：未找到未归还记录或已提交申请");
            }
            objectMapper.writeValue(out, result);

        } catch (DatabindException databindEx) { // 重命名变量，避免冲突
            // 修复：明确声明异常变量，确保作用域有效
            databindEx.printStackTrace(); // 此时变量databindEx有效
            out.write("{\"success\":false,\"message\":\"操作失败，请稍后重试！\"}");
        } catch (Exception ex) { // 统一重命名为ex，避免IDE识别问题
            ex.printStackTrace();
            out.write("{\"success\":false,\"message\":\"服务器内部错误！\"}");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}