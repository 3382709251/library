package com.library.servlet;

import com.library.dao.AdminBookDao;
import com.library.entity.Book;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class AdminBookDeleteServlet extends HttpServlet {

    private final AdminBookDao adminBookDao = new AdminBookDao();

    // 必须有 doPost 方法，且签名完全一致
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String bookId = request.getParameter("bookId");

        System.out.println("=== AdminBookDeleteServlet doPost 被调用 ===");
        System.out.println("接收到的 bookId = " + bookId);

        if (bookId == null || bookId.trim().isEmpty()) {
            out.print("失败：图书ID不能为空");
            return;
        }

        try {
            // 检查是否有未归还借阅记录
            int unreturned = adminBookDao.countUnreturnedBorrows(bookId);
            if (unreturned > 0) {
                out.print("失败：该图书还有 " + unreturned + " 本未归还，不能删除！");
                return;
            }

            // 获取图书信息（用于删除图片）
            Book book = adminBookDao.getBookById(bookId);

            // 执行删除
            boolean success = adminBookDao.deleteBook(bookId);

            if (success) {
                // 删除服务器上的图片文件（可选）
                if (book != null && book.getBookImage() != null && !book.getBookImage().trim().isEmpty()) {
                    String realPath = getServletContext().getRealPath(book.getBookImage());
                    File imgFile = new File(realPath);
                    if (imgFile.exists()) {
                        imgFile.delete();
                    }
                }
                out.print("成功：图书删除成功！");
            } else {
                out.print("失败：删除失败，图书可能不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("失败：系统异常 - " + e.getMessage());
        }
    }

    // 兼容 GET 请求（防止有人直接访问）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}