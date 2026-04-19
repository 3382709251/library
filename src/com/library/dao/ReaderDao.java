package com.library.dao;

import com.library.entity.Reader;
import com.library.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReaderDao {
    public List<Reader> getAllReaders() {
        List<Reader> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM reader";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Reader reader = new Reader();
                reader.setReaderId(rs.getString("Reader_ID"));
                reader.setReaderName(rs.getString("reader_name"));
                reader.setPassword(rs.getString("Reader_Password"));
                reader.setReaderPhone(rs.getString("readerphone"));
                reader.setCollege(rs.getString("college"));
                reader.setReaderCategory(rs.getInt("Reader_Category"));

                reader.setCreateTime(rs.getTimestamp("Create_time"));
                reader.setUpdateTime(rs.getTimestamp("Update_time"));
                reader.setMaxBorrowTime(rs.getInt("Max_borrow_time"));
                reader.setMaxBorrowCount(rs.getInt("Max_borrow_count"));
                // 修正注释：Account_Money 是读者未缴纳的总罚款，非账户余额
                reader.setAccountMoney(rs.getBigDecimal("Account_Money"));

                list.add(reader);
            }
            System.out.println("查询所有读者成功，共" + list.size() + "条数据");
        } catch (Exception e) {
            System.out.println("查询所有读者异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 读者登录验证：匹配读者姓名（raeder_Name）和密码（Reader_Password）
     * 核心修复：补全所有字段的封装，确保Session中的Reader对象完整
     * @param username 前端传入的读者姓名（对应表中raeder_Name字段）
     * @param password 前端传入的登录密码（对应表中Reader_Password字段）
     * @return 验证成功返回完整的Reader对象，失败返回null
     */
    public Reader login(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            System.out.println("========== ReaderDao.login 执行 ==========");
            System.out.println("读者姓名：" + username + "，密码：" + password);

            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM reader WHERE reader_name=? AND Reader_Password=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Reader reader = new Reader();
                reader.setReaderId(rs.getString("Reader_ID"));
                reader.setReaderName(rs.getString("reader_name"));
                reader.setPassword(rs.getString("Reader_Password"));
                reader.setReaderPhone(rs.getString("readerphone"));
                reader.setCollege(rs.getString("college"));
                reader.setReaderCategory(rs.getInt("Reader_Category"));

                reader.setCreateTime(rs.getTimestamp("Create_time"));
                reader.setUpdateTime(rs.getTimestamp("Update_time"));
                reader.setMaxBorrowTime(rs.getInt("Max_borrow_time"));
                reader.setMaxBorrowCount(rs.getInt("Max_borrow_count"));
                // 修正注释：Account_Money 是总罚款
                reader.setAccountMoney(rs.getBigDecimal("Account_Money"));

                System.out.println("读者登录成功：" + reader.getReaderName() + "（ID：" + reader.getReaderId() + "）");
                System.out.println("借阅时长：" + reader.getMaxBorrowTime() + "天，借阅数量：" + reader.getMaxBorrowCount() + "本");
                System.out.println("当前未缴纳总罚款：" + reader.getAccountMoney() + "元"); // 新增：打印总罚款
                return reader;
            } else {
                System.out.println("读者登录失败：未查询到【姓名=" + username + "，密码=" + password + "】的读者");
            }
        } catch (Exception e) {
            System.out.println("读者登录异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 根据读者ID查询读者信息（适配reader表结构）
     * @param readerId 读者ID（对应表中Reader_ID字段）
     * @return 匹配的完整Reader对象，无数据返回null
     */
    public Reader findById(String readerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT * FROM reader WHERE Reader_ID=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Reader reader = new Reader();
                reader.setReaderId(rs.getString("Reader_ID"));
                reader.setReaderName(rs.getString("reader_name"));
                reader.setPassword(rs.getString("Reader_Password"));
                reader.setReaderPhone(rs.getString("readerphone"));
                reader.setCollege(rs.getString("college"));
                reader.setReaderCategory(rs.getInt("Reader_Category"));
                reader.setCreateTime(rs.getTimestamp("Create_time"));
                reader.setUpdateTime(rs.getTimestamp("Update_time"));
                reader.setMaxBorrowTime(rs.getInt("Max_borrow_time"));
                reader.setMaxBorrowCount(rs.getInt("Max_borrow_count"));
                // 修正注释：Account_Money 是总罚款
                reader.setAccountMoney(rs.getBigDecimal("Account_Money"));
                return reader;
            }
        } catch (Exception e) {
            System.out.println("根据ID查询读者异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    // 修改基本信息
    public boolean updateReaderInfo(String readerId, String readerName, String college, String readerPhone) {
        String sql = "UPDATE reader SET reader_name=?, college=?, readerphone=? WHERE Reader_ID=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, readerName);
            ps.setString(2, college);
            ps.setString(3, readerPhone);
            ps.setString(4, readerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 修改密码（建议对密码进行简单加密，这里用明文演示）
    public boolean changePassword(String readerId, String oldPassword, String newPassword) {
        String checkSql = "SELECT Reader_Password FROM reader WHERE Reader_ID = ?";
        String updateSql = "UPDATE reader SET Reader_Password = ? WHERE Reader_ID = ?";

        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, readerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (!oldPassword.equals(rs.getString("Reader_Password"))) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newPassword);
                ps.setString(2, readerId);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ====================== 修正：罚款相关核心方法（适配 Account_Money 为总罚款 + 线下缴纳） ======================

    /**
     * 计算并更新读者的总罚款（同步到 reader 表的 Account_Money 字段）
     * @param readerId 读者ID
     * @return 计算后的总罚款金额（double类型，适配前端展示）
     */
    public double calculateTotalFine(String readerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalFine = 0.0;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 计算该读者所有未归还图书的罚款总和
            String calcSql = "SELECT COALESCE(SUM(fine_amount), 0) AS total_fine " +
                    "FROM borrow " +
                    "WHERE reader_id = ? AND return_date IS NULL";
            pstmt = conn.prepareStatement(calcSql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                BigDecimal totalFineBD = rs.getBigDecimal("total_fine");
                totalFine = totalFineBD != null ? totalFineBD.doubleValue() : 0.0;
            }

            // 2. 更新 reader 表的 Account_Money（总罚款）
            String updateSql = "UPDATE reader SET Account_Money = ? WHERE Reader_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setBigDecimal(1, BigDecimal.valueOf(totalFine));
            pstmt.setString(2, readerId);
            pstmt.executeUpdate();

            conn.commit();
            System.out.println("【ReaderDao】读者 " + readerId + " 总罚款更新完成：" + totalFine + " 元");

        } catch (SQLException e) {
            System.out.println("计算并更新读者总罚款异常：" + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
            DBUtil.close(conn, pstmt, rs);
        }
        return totalFine;
    }

    public void updateAccountMoney(String readerId, BigDecimal amount) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE reader SET Account_Money = ? WHERE Reader_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setBigDecimal(1, amount);
            pstmt.setString(2, readerId);
            pstmt.executeUpdate();
            System.out.println("【ReaderDao】已更新读者 " + readerId + " 的 Account_Money = " + amount);
        } catch (SQLException e) {
            System.out.println("更新 Account_Money 异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 管理员确认读者线下缴纳罚款（清零/扣除指定金额）
     * @param readerId 读者ID
     * @param payAmount 缴纳金额（0=清零全部，>0=扣除指定金额）
     * @return true=确认成功，false=确认失败
     */
    public boolean confirmPayFine(String readerId, double payAmount) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 查询当前总罚款
            String checkSql = "SELECT Account_Money FROM reader WHERE Reader_ID = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                System.out.println("确认缴费失败：读者 " + readerId + " 不存在");
                return false;
            }

            BigDecimal currentTotalFine = rs.getBigDecimal("Account_Money");
            BigDecimal payAmountBD = BigDecimal.valueOf(payAmount);
            BigDecimal newTotalFine;

            // 2. 计算新的总罚款
            if (payAmount <= 0) {
                // 缴纳金额≤0 表示清零全部罚款
                newTotalFine = BigDecimal.ZERO;
            } else {
                // 扣除指定金额（确保不出现负数）
                newTotalFine = currentTotalFine.subtract(payAmountBD);
                if (newTotalFine.compareTo(BigDecimal.ZERO) < 0) {
                    newTotalFine = BigDecimal.ZERO;
                }
            }

            // 3. 更新总罚款（Account_Money）
            String updateSql = "UPDATE reader SET Account_Money = ? WHERE Reader_ID = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setBigDecimal(1, newTotalFine);
            pstmt.setString(2, readerId);
            int updateRows = pstmt.executeUpdate();

            if (updateRows > 0) {
                conn.commit();
                System.out.println("【ReaderDao】读者 " + readerId + " 线下缴费确认成功：" +
                        "原罚款=" + currentTotalFine + "，缴纳=" + payAmountBD + "，剩余=" + newTotalFine);
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.out.println("确认线下缴纳罚款异常：" + e.getMessage());
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) {}
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // ReaderDao.java 新增方法
    /**
     * 查询读者总罚款（Account_Money字段）
     * @param readerId 读者ID
     * @return 总罚款金额
     */
    public double getReaderTotalFine(String readerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT Account_Money FROM reader WHERE reader_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Account_Money");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0.0;
    }
}
