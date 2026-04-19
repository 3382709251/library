package com.library.dao;

import com.library.entity.BorrowRecord;
import com.library.entity.Book;
import com.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminBorrowDao {

    /**
     * 根据读者ID查询所有借阅记录（关联图书信息）
     * 已适配你的数据库字段名
     */
    /**
     * 根据读者ID查询所有借阅记录（关联图书信息）
     */
    /**
     * 管理员端 - 根据读者ID查询借阅记录（用于 readerdetail.jsp）
     */
    public List<BorrowRecord> getBorrowRecordsByReaderId(String readerId) {
        List<BorrowRecord> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "SELECT " +
                    "b.id, " +
                    "b.book_id AS bookId, " +
                    "b.reader_id AS readerId, " +
                    "b.borrow_date AS borrowDate, " +      // 对应实体类 borrowDate
                    "b.return_date AS returnDate, " +      // 对应实体类 returnDate
                    "b.status, " +
                    "b.fine_amount AS fineAmount, " +
                    "b.fine_status AS fineStatus, " +
                    "COALESCE(b.return_apply_status, 0) AS returnApplyStatus, " +
                    "COALESCE(b.return_apply_time, '') AS returnApplyTime, " +
                    "bk.book_name AS bookName, " +
                    "r.reader_name AS readerName " +
                    "FROM borrow b " +
                    "LEFT JOIN book bk ON b.book_id = bk.book_id " +
                    "LEFT JOIN reader r ON b.reader_id = r.Reader_ID " +
                    "WHERE b.reader_id = ? " +
                    "ORDER BY b.borrow_date DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, readerId);   // reader_id 是 varchar，用 setString
            rs = pstmt.executeQuery();

            int count = 0;
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord();

                record.setId(rs.getInt("id"));
                record.setBookId(rs.getString("bookId"));
                record.setReaderId(rs.getString("readerId"));

                // 使用实体类中已定义的字段
                record.setBorrowDate(rs.getString("borrowDate"));
                record.setReturnDate(rs.getString("returnDate"));

                record.setStatus(rs.getString("status"));
                record.setFineAmount(rs.getBigDecimal("fineAmount"));
                record.setFineStatus(rs.getString("fineStatus"));

                // 归还申请字段 —— 核心！
                record.setReturnApplyStatus(rs.getInt("returnApplyStatus"));
                record.setReturnApplyTime(rs.getString("returnApplyTime"));

                record.setBookName(rs.getString("bookName"));
                record.setReaderName(rs.getString("readerName"));

                list.add(record);
                count++;
            }

            System.out.println("【AdminBorrowDao】成功查询到读者 " + readerId + " 的借阅记录数量: " + count + " 条");

        } catch (Exception e) {
            System.out.println("【AdminBorrowDao】查询借阅记录异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }
}