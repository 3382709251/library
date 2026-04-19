package com.library.servlet;

import com.library.dao.AdminReaderDao;
import com.library.entity.Reader;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AdminReaderListServlet extends HttpServlet {

    private AdminReaderDao adminReaderDao = new AdminReaderDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            List<Reader> readers = adminReaderDao.getAllReaders();
            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(readers));

            System.out.println("✅ 管理员读者列表接口成功，返回 " + readers.size() + " 条数据");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"查询读者列表失败\"}");
        }
    }
}