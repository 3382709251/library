package com.library.dao;
import com.library.entity.Admin;
import com.library.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDao {
    public Admin login(String loginAccount, String loginPassword) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Admin admin = null;

        System.out.println("========== AdminDao.login 执行 ==========");
        Connection conn = DBUtil.getConnection();

        try {
            // 修复：先查询账号对应的所有信息，再手动比对密码（避免ResultSet取值异常）
            String sql = "SELECT * FROM admin WHERE login_account=?"; // 仅按账号查询
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loginAccount);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 先获取数据库中的密码
                String dbPwd = rs.getString("login_password");
                System.out.println("数据库查询到管理员[" + loginAccount + "]的密码：" + dbPwd);

                // 手动比对密码
                if (dbPwd != null && dbPwd.equals(loginPassword)) {
                    // 密码匹配，构建完整Admin对象
                    admin = new Admin();
                    admin.setAdminId(rs.getString("admin_id"));
                    admin.setLoginAccount(rs.getString("login_account"));
                    admin.setLoginPassword(dbPwd); // 手动赋值数据库密码
                    admin.setAdminName(rs.getString("admin_name"));
                    admin.setAdminRole(rs.getString("admin_role"));
                    admin.setPhone(rs.getString("worker_phone")); // 注意：你的手机号字段是worker_phone，不是phone！
                } else {
                    System.out.println("登录失败：密码不匹配，输入密码=" + loginPassword + "，数据库密码=" + dbPwd);
                }
            } else {
                System.out.println("登录失败：未查询到账号[" + loginAccount + "]");
            }

        } catch (Exception e) {
            System.out.println("AdminDao.login 异常：");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return admin;
    }

    // 修改密码方法（保持之前的优化）
    public boolean changePassword(String adminId, String oldPassword, String newPassword) {
        PreparedStatement checkPstmt = null;
        PreparedStatement updatePstmt = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean result = false;

        try {
            conn = DBUtil.getConnection();
            String checkSql = "SELECT login_password FROM admin WHERE admin_id=?";
            checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setString(1, adminId);
            rs = checkPstmt.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("login_password");
                System.out.println("验证旧密码：数据库密码=" + dbPassword + "，传入旧密码=" + oldPassword);

                if (dbPassword != null && dbPassword.equals(oldPassword)) {
                    String updateSql = "UPDATE admin SET login_password=? WHERE admin_id=?";
                    updatePstmt = conn.prepareStatement(updateSql);
                    updatePstmt.setString(1, newPassword);
                    updatePstmt.setString(2, adminId);

                    int rows = updatePstmt.executeUpdate();
                    result = rows > 0;
                    System.out.println("密码更新行数：" + rows + "，更新结果：" + result);
                } else {
                    System.out.println("旧密码比对失败：数据库密码为null 或 与传入密码不一致");
                }
            } else {
                System.out.println("未查询到管理员ID：" + adminId);
            }
        } catch (Exception e) {
            System.out.println("AdminDao.changePassword 异常：");
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, checkPstmt, rs);
            DBUtil.close(null, updatePstmt, null);
        }
        return result;
    }
}