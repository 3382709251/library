package com.library.servlet;

import com.library.dao.AdminDao;
import com.library.dao.ReaderDao;
import com.library.entity.Admin;
import com.library.entity.Reader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录请求处理器
 * @WebServlet("/login")：Servlet映射路径，与前端表单action的/login对应，接收登录请求
 */

public class LoginServlet extends HttpServlet {
    /**
     * 处理GET请求（如直接访问/login地址时）
     * @param request  请求对象：封装客户端请求信息
     * @param response 响应对象：封装服务端响应信息
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 转发到登录页面：
        // /login.jsp是项目内相对路径（forward转发无需加上下文路径）
        // forward特点：服务器内部跳转，地址栏不变，请求数据可共享
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * 处理POST请求（表单提交的登录请求）
     * @param request  请求对象：获取表单提交的参数
     * @param response 响应对象：控制登录成功/失败的跳转逻辑
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. 设置请求/响应编码，解决中文乱码问题
        // 针对POST请求的参数编码，必须在获取参数前设置
        request.setCharacterEncoding("UTF-8");
        // 响应内容的编码，确保页面输出中文不乱码
        response.setContentType("text/html;charset=UTF-8");

        // 2. 获取表单提交的参数
        String username = request.getParameter("username"); // 用户名
        String password = request.getParameter("password"); // 密码
        String loginType = request.getParameter("type");    // 登录类型（reader/admin）

        // 3. 非空校验：若用户名/密码/登录类型为空，返回登录页并提示错误
        if (username == null || password == null || username.trim().isEmpty()
                || password.trim().isEmpty() || loginType == null) {
            // 设置错误提示信息，供前端login.jsp显示
            request.setAttribute("error", "用户名/密码/登录类型不能为空");
            // 转发回登录页（保留错误提示）
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return; // 终止后续代码执行
        }

        // 4. 声明变量：标记登录是否成功、存储登录用户对象
        boolean loginSuccess = false; // 登录成功标识
        Object loginUser = null;      // 存储读者/管理员对象

        // 5. 根据登录类型分别验证账号
        if ("reader".equals(loginType)) { // 读者登录逻辑
            // 实例化读者数据访问对象（DAO），负责和读者表交互
            ReaderDao readerDao = new ReaderDao();
            // 调用DAO的登录方法，验证用户名密码，返回读者对象（null表示验证失败）
            Reader reader = readerDao.login(username, password);
            if (reader != null) { // 读者账号验证成功
                loginSuccess = true; // 标记登录成功
                loginUser = reader;  // 存储读者对象
                // 将读者对象存入Session，供后续页面校验登录状态（键：loginReader）
                request.getSession().setAttribute("loginReader", reader);
            }
        } else if ("admin".equals(loginType)) { // 管理员登录逻辑
            // 实例化管理员数据访问对象（DAO），负责和管理员表交互
            AdminDao adminDao = new AdminDao();
            // 调用DAO的登录方法，验证用户名密码，返回管理员对象（null表示验证失败）
            Admin admin = adminDao.login(username, password);
            if (admin != null) { // 管理员账号验证成功
                loginSuccess = true; // 标记登录成功
                loginUser = admin;   // 存储管理员对象
                // 将管理员对象存入Session，供后续页面校验登录状态（键：loginAdmin）
                request.getSession().setAttribute("loginAdmin", admin);
            }
        }

        // 6. 根据登录结果处理跳转
        if (loginSuccess) { // 登录成功：分角色跳转
            if ("reader".equals(loginType)) {
                // 读者登录成功：重定向到图书列表页
                // sendRedirect特点：客户端跳转，地址栏变化，需拼接上下文路径
                response.sendRedirect(request.getContextPath() + "/reader/main.jsp");
            } else if ("admin".equals(loginType)) {
                // 管理员登录成功：重定向到管理员首页
                response.sendRedirect(request.getContextPath() + "/admin/bookList.jsp");
            }
        } else { // 登录失败：返回登录页并提示错误
            // 设置错误提示信息（用户名或密码错误）
            request.setAttribute("error", "用户名或密码错误");
            // 转发回登录页（保留错误提示）
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}