package com.library.servlet;

import com.library.dao.AdminBookDao;
import com.library.entity.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class AdminBookAddServlet extends HttpServlet {

    private final AdminBookDao adminBookDao = new AdminBookDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        try {
            // 获取所有表单参数
            // 在 try 块中替换这部分参数获取代码：
            String bookId = request.getParameter("bookId");
            String isbn = request.getParameter("isbn");
            String bookName = request.getParameter("bookName");
            String author = request.getParameter("author");
            String publisher = request.getParameter("publisher");
            String publishYearStr = request.getParameter("publishYear");
            String priceStr = request.getParameter("price");
            String bookType = request.getParameter("bookType");
            String totalCountStr = request.getParameter("totalCount");
            String bookIntroduce = request.getParameter("bookIntroduce");

            if (bookId == null || bookId.trim().isEmpty()) {
                response.getWriter().write("失败：图书ID不能为空");
                return;
            }
            if (bookName == null || bookName.trim().isEmpty()) {
                response.getWriter().write("失败：书名不能为空");
                return;
            }

            int totalCount = Integer.parseInt(totalCountStr != null ? totalCountStr.trim() : "1");
            int publishYear = publishYearStr != null && !publishYearStr.trim().isEmpty()
                    ? Integer.parseInt(publishYearStr.trim()) : 2024;
            double price = priceStr != null && !priceStr.trim().isEmpty()
                    ? Double.parseDouble(priceStr.trim()) : 0.00;

            // 图片上传
            String bookImage = null;
            Part filePart = request.getPart("bookImage");
            if (filePart != null && filePart.getSize() > 0) {
                String originalName = getSubmittedFileName(filePart);
                String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
                String newFileName = UUID.randomUUID().toString() + extension;

                String uploadPath = getServletContext().getRealPath("/images");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                filePart.write(uploadPath + File.separator + newFileName);
                bookImage = newFileName;
                System.out.println("图片保存成功: " + newFileName);
            }

            // 组装 Book 对象
            Book book = new Book();
            book.setBookId(bookId.trim());
            book.setIsbn(isbn != null ? isbn.trim() : null);
            book.setBookName(bookName.trim());
            book.setAuthor(author != null ? author.trim() : "");
            book.setPublisher(publisher != null ? publisher.trim() : "");
            book.setPublishYear(publishYear);
            book.setPrice(price);
            book.setBookType(bookType != null ? bookType.trim() : "");
            book.setTotalCount(totalCount);
            book.setBorrowedCount(0);
            book.setBookIntroduce(bookIntroduce != null ? bookIntroduce.trim() : "");
            book.setBookImage(bookImage);

            // 执行插入
            boolean success = adminBookDao.addBook(book);
            System.out.println("DAO.addBook 返回结果: " + success);

            if (success) {
                response.getWriter().write("成功：图书添加成功！");
            } else {
                response.getWriter().write("失败：数据库插入失败，请检查 AdminBookDao.addBook SQL 是否正确");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("失败：系统异常 - " + e.getMessage());
        }
    }

    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "unknown";
    }
}