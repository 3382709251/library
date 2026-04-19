package com.library.servlet;

import com.library.dao.BorrowDao;
import com.library.dao.ReaderDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DatabindException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ConfirmReturnBookServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 新增：解决中文乱码
        resp.setContentType("application/json;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        Map<String, Object> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        System.out.println("=== ConfirmReturnBookServlet 被调用 ===");
        System.out.println("接收到的 recordIdStr = [" + req.getParameter("recordId") + "]");
        System.out.println("接收到的 bookId = [" + req.getParameter("bookId") + "]");
        try {
            // 1. 获取参数
            String recordIdStr = req.getParameter("recordId");
            String bookId = req.getParameter("bookId");
            if (recordIdStr == null || recordIdStr.isEmpty() || bookId == null || bookId.isEmpty()) {
                result.put("success", false);
                result.put("message", "参数不能为空！");
                objectMapper.writeValue(out, result);
                return;
            }

            int recordId = Integer.parseInt(recordIdStr);
            BorrowDao borrowDao = new BorrowDao();
            ReaderDao readerDao = new ReaderDao(); // 新增：读者DAO，用于更新总罚款

            // 2. 确认归还（原有逻辑）
            boolean confirmSuccess = borrowDao.confirmReturnBook(recordId, bookId);

            if (confirmSuccess) {
                // 新增：3. 获取该借阅记录的读者ID
                String readerId = borrowDao.getReaderIdByBorrowId(recordId);
                if (readerId != null) {
                    // 新增：4. 重新计算并更新读者总罚款
                    readerDao.calculateTotalFine(readerId);
                }
                result.put("success", true);
                result.put("message", "确认归还成功");
            } else {
                result.put("success", false);
                result.put("message", "确认失败：记录不存在或已确认");
            }
            objectMapper.writeValue(out, result);

        } catch (DatabindException databindEx) {
            databindEx.printStackTrace();
            out.write("{\"success\":false,\"message\":\"数据解析失败！\"}");
        } catch (NumberFormatException numEx) {
            numEx.printStackTrace();
            out.write("{\"success\":false,\"message\":\"记录ID格式错误！\"}");
        } catch (Exception ex) {
            ex.printStackTrace();
            out.write("{\"success\":false,\"message\":\"服务器错误，请重试！\"}");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}