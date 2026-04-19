package com.library.servlet;

import com.library.dao.BookDao;
import com.library.dao.BorrowDao;
import com.library.dao.ReaderDao;
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

// 补充Servlet映射注解，确保接口可访问
@WebServlet("/borrowBook")
public class BorrowBookServlet extends HttpServlet {

    private BookDao bookDao = new BookDao();
    private BorrowDao borrowDao = new BorrowDao();
    private ReaderDao readerDao = new ReaderDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = req.getSession(false);  // 不创建新session
            Reader reader = (Reader) session.getAttribute("loginReader");

            System.out.println("=== 借阅请求开始 ===");
            System.out.println("读者登录状态: " + (reader != null ? "已登录" : "未登录"));
            if (reader != null) {
                System.out.println("读者ID: " + reader.getReaderId());
                System.out.println("读者姓名: " + reader.getReaderName());
            }

            String bookId = req.getParameter("bookId");
            System.out.println("请求借阅的图书ID: " + bookId);

            // 1. 基础校验：登录状态 + 图书ID
            if (reader == null) {
                result.put("success", false);
                result.put("message", "请先登录！");
                System.out.println("借阅失败原因: 未登录");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }
            if (bookId == null || bookId.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "图书ID不能为空！");
                System.out.println("借阅失败原因: bookId为空");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }

            // ========== 限制条件1：判断是否超过最大借阅量（极简版） ==========
            int maxBorrowNum = 5; // 若Reader有该字段则替换为 reader.getMaxBorrowNum()
            int currentBorrowCount = borrowDao.getUnReturnedBookCount(reader.getReaderId());
            System.out.println("最大可借: " + maxBorrowNum + " | 当前未还: " + currentBorrowCount);

            if (currentBorrowCount >= maxBorrowNum) {
                result.put("success", false);
                result.put("message", "借阅失败！当前借阅书籍已达上限（" + maxBorrowNum + "本）");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }

            // ========== 限制条件2：判断是否有逾期（直接读读者总罚款Account_Money） ==========
            double totalFine = readerDao.getReaderTotalFine(reader.getReaderId());

            System.out.println("读者总罚款: " + totalFine);
            if (totalFine > 0) { // 总罚款>0 说明有逾期
                result.put("success", false);
                result.put("message", "借阅失败！您有逾期未缴罚款，请先缴清罚款后再借阅");
                resp.getWriter().write(new Gson().toJson(result));
                return;
            }

            // 执行借阅（基础校验+限制条件都通过后）
            System.out.println("开始调用 borrowDao.borrowBook...");
            boolean success = borrowDao.borrowBook(reader.getReaderId(), bookId);
            System.out.println("borrowDao.borrowBook 返回结果: " + success);

            if (success) {
                result.put("success", true);
                result.put("message", "借阅成功！");
                System.out.println("借阅成功！");
            } else {
                result.put("success", false);
                result.put("message", "借阅失败！可能库存不足或您已借过该书");
                System.out.println("借阅失败 - Dao返回false");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("借阅过程中发生异常: " + e.getMessage());
            result.put("success", false);
            result.put("message", "系统异常：" + e.getMessage());
        }

        String jsonResponse = new Gson().toJson(result);
        System.out.println("返回给前端的JSON: " + jsonResponse);
        resp.getWriter().write(jsonResponse);
    }
}