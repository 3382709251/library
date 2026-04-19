package com.library.servlet;

import com.library.dao.ReaderDao;
import com.library.entity.Reader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ReaderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String readerId = request.getParameter("readerId");

        if (readerId == null || readerId.trim().isEmpty()) {
            response.getWriter().write("fail：读者ID不能为空");
            return;
        }

        ReaderDao readerDao = new ReaderDao();
        Reader reader = readerDao.findById(readerId);

        if (reader == null) {
            response.getWriter().write("fail：读者不存在");
            return;
        }

        response.getWriter().write("success：" + reader.getReaderName());
    }
}