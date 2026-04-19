package com.library.dao;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BorrowDao {

    /**
     * 借阅图书
     */
    public boolean borrowBook(String readerId, String bookId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 更新图书已借数量 +1
            String updateBookSql = "UPDATE book SET borrowed_count = borrowed_count + 1 WHERE book_id = ?";
            pstmt = conn.prepareStatement(updateBookSql);
            pstmt.setString(1, bookId);
            int updateRows = pstmt.executeUpdate();

            if (updateRows == 0) {
                conn.rollback();
                return false;
            }

            // 插入借阅记录（适配你的表名和字段名）
            String insertSql = "INSERT INTO borrow (reader_id, book_id, borrow_date, fine_amount) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, readerId);
            pstmt.setString(2, bookId);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setDouble(4, 0.0); // 新增：初始罚款金额为0
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 归还图书
     */
    public boolean returnBook(String readerId, String bookId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 检查是否存在未归还记录
            String checkSql = "SELECT id FROM borrow WHERE reader_id = ? AND book_id = ? AND return_date IS NULL";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, readerId);
            pstmt.setString(2, bookId);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int recordId = rs.getInt("id");

            // 更新归还时间 + 重置罚款金额为0
            String updateRecordSql = "UPDATE borrow SET return_date = ?, fine_amount = 0.0 WHERE id = ?";
            pstmt = conn.prepareStatement(updateRecordSql);
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, recordId);
            pstmt.executeUpdate();

            // 更新图书已借数量 -1
            String updateBookSql = "UPDATE book SET borrowed_count = borrowed_count - 1 WHERE book_id = ?";
            pstmt = conn.prepareStatement(updateBookSql);
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    /**
     * 查询读者的所有借阅记录（我的借阅页面使用）
     * 新增：实时计算并更新单本罚款金额
     */
    public List<BorrowRecord> getBorrowRecordsByReader(String readerId) {
        List<BorrowRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 适配你的表名和字段名
        String sql = "SELECT br.*, b.book_name, b.author " +
                "FROM borrow br " +
                "LEFT JOIN book b ON br.book_id = b.book_id " +
                "WHERE br.reader_id = ? " +
                "ORDER BY br.borrow_date DESC";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();
                record.setId(rs.getInt("id"));
                record.setReaderId(rs.getString("reader_id"));
                record.setBookId(rs.getString("book_id"));
                record.setBorrowTime(rs.getDate("borrow_date"));
                record.setReturnTime(rs.getDate("return_date"));
                //还书申请
                record.setReturnApplyTime(rs.getString("return_apply_time"));
                record.setReturnApplyStatus(rs.getInt("return_apply_status"));
                // 新增：读取数据库中的罚款金额
                record.setFineAmount(rs.getDouble("fine_amount"));

                // 关联图书信息
                Book book = new Book();
                book.setBookId(rs.getString("book_id"));
                book.setBookName(rs.getString("book_name")); // 封装书名
                book.setAuthor(rs.getString("author"));       // 封装作者

                // 将Book对象赋值给BorrowRecord
                record.setBook(book);
                record.setBookName(rs.getString("book_name"));

                // 新增：实时计算罚款并更新数据库
                double fine = calculateFine(
                        rs.getTimestamp("borrow_date"),
                        rs.getTimestamp("return_date")
                );
                record.setFineAmount(fine);
                updateFineAmount(record.getId(), fine); // 更新到数据库

                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return records;
    }

    /**
     * 获取借阅排行榜（全部）
     */
    public List<Book> getBorrowRank() {
        List<Book> bookList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM book ORDER BY borrowed_count DESC LIMIT 20";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getString("book_id"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setBorrowedCount(rs.getInt("borrowed_count"));
                book.setBookImage(rs.getString("book_image"));   // 如果有这个字段

                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }

    // BorrowDao.java 新增方法
    /**
     * 读者提交归还申请
     */
    public boolean applyReturnBook(String readerId, String bookId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 检查是否存在未归还且未提交归还申请的记录
            String checkSql = "SELECT id FROM borrow WHERE reader_id = ? AND book_id = ? AND return_date IS NULL AND return_apply_status = 0";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, readerId);
            pstmt.setString(2, bookId);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int recordId = rs.getInt("id");

            // 2. 更新归还申请状态和申请时间
            String updateSql = "UPDATE borrow SET return_apply_status = 1, return_apply_time = ? WHERE id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, recordId);
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // BorrowDao.java 新增方法
    /**
     * 管理员确认归还（线下归还后手动确认）
     * 新增：归还时重置罚款金额为0
     */
    public boolean confirmReturnBook(int recordId, String bookId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 检查是否存在已申请归还但未确认的记录
            String checkSql = "SELECT id FROM borrow WHERE id = ? AND book_id = ? AND return_apply_status = 1 AND return_date IS NULL";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, recordId);
            pstmt.setString(2, bookId);
            rs = pstmt.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            // 2. 更新归还状态：设置return_date、修改return_apply_status为2、重置罚款为0
            String updateRecordSql = "UPDATE borrow SET return_date = ?, return_apply_status = 2, fine_amount = 0.0 WHERE id = ?";
            pstmt = conn.prepareStatement(updateRecordSql);
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, recordId);
            pstmt.executeUpdate();

            // 2.2 更新图书已借数量-1
            String updateBookSql = "UPDATE book SET borrowed_count = borrowed_count - 1 WHERE book_id = ?";
            pstmt = conn.prepareStatement(updateBookSql);
            pstmt.setString(1, bookId);
            pstmt.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
    }

    // ====================== 新增罚款相关方法 ======================
    /**
     * 计算单本图书的罚款金额
     * @param borrowDate 借阅时间
     * @param returnDate 归还时间（null表示未归还）
     * @return 罚款金额
     */
    public double calculateFine(Timestamp borrowDate, Timestamp returnDate) {
        // 已归还则罚款为0
        if (returnDate != null) return 0.0;

        // 空值校验
        if (borrowDate == null) return 0.0;

        // 应还时间 = 借阅时间 + 30天
        long dueTime = borrowDate.getTime() + 30L * 24 * 60 * 60 * 1000;
        Date dueDate = new Date(dueTime);
        Date now = new Date();

        // 计算逾期天数（向上取整）
        long overdueMs = now.getTime() - dueDate.getTime();
        int overdueDays = (int) Math.ceil(overdueMs / (24 * 60 * 60 * 1000.0));

        double fine = 0.0;
        if (overdueDays > 0) {
            if (overdueDays <= 15) {
                fine = 5.0; // 15天内罚款5元
            } else if (overdueDays > 15 && overdueDays <= 30) {
                fine = 5.0 + (overdueDays - 15); // 15-30天：5元 + 超出天数×1元/天
            } else if (overdueDays > 30) {
                fine = 30.0; // 超30天固定罚款30元
            }
        }
        return fine;
    }

    /**
     * 更新单本借阅记录的罚款金额
     * @param borrowId 借阅记录ID
     * @param fineAmount 罚款金额
     * @return 是否更新成功
     */
    public boolean updateFineAmount(int borrowId, double fineAmount) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE borrow SET fine_amount = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, fineAmount);
            pstmt.setInt(2, borrowId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 根据借阅记录ID获取读者ID（用于确认归还后更新总罚款）
     * @param borrowId 借阅记录ID
     * @return 读者ID
     */
    public String getReaderIdByBorrowId(int borrowId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT reader_id FROM borrow WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, borrowId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("reader_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return null;
    }

    /**
     * 管理员端：查询读者的所有借阅记录（含罚款）
     * @param readerId 读者ID
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getBorrowRecordsByReaderId(String readerId) {
        return getBorrowRecordsByReader(readerId); // 复用已有查询逻辑
    }

    /**
     * 统计读者未归还的书籍数量（极简版，仅计数）
     * @param readerId 读者ID
     * @return 未归还数量
     */
    public int getUnReturnedBookCount(String readerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBUtil.getConnection();
            // 仅查询未归还记录的数量，性能最优
            String sql = "SELECT COUNT(*) FROM borrow WHERE reader_id = ? AND return_date IS NULL";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, readerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return 0;
    }
}