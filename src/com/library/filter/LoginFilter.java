package com.library.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// 拦截管理员/读者路径（正确）
@WebFilter({"/admin/*", "/reader/*"})
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // 修复：排除静态资源（CSS/JS）+ 登录相关路径
        if (path.contains("/login.jsp") || path.contains("/login")
                || path.contains("/css/") || path.contains("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        // 管理员路径：校验loginAdmin
        if (path.startsWith(req.getContextPath() + "/admin/")) {
            if (session == null || session.getAttribute("loginAdmin") == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
        }
        // 读者路径：校验loginReader
        else if (path.startsWith(req.getContextPath() + "/reader/")) {
            if (session == null || session.getAttribute("loginReader") == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
                return;
            }
        }
        // 已登录，放行
        chain.doFilter(request, response);
    }

    // 补充Filter必需的空实现（避免Tomcat启动警告）
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}