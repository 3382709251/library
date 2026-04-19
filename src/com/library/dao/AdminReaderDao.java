package com.library.dao;

import com.library.entity.Reader;
import com.library.entity.BorrowRecord;
import com.library.util.DBUtil;

import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminReaderDao {

    /**
     * 获取所有读者列表
     */
    public List<Reader> getAllReaders() {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT * FROM reader ORDER BY Create_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reader r = new Reader();
                r.setReaderId(rs.getString("Reader_ID"));
                r.setReaderName(rs.getString("reader_name"));
                r.setCollege(rs.getString("college"));
                r.setReaderCategory(rs.getInt("Reader_Category"));
                r.setReaderPhone(rs.getString("readerphone"));
                r.setMaxBorrowCount(rs.getInt("Max_borrow_count"));
                r.setAccountMoney(rs.getBigDecimal("Account_Money"));
                readers.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readers;
    }

    /**
     * 根据读者ID查询借阅记录（关联读者姓名和书籍名称）
     */
    /**
     * 管理员端 - 根据读者ID查询借阅记录（用于 readerdetail.jsp）
     */
    public List<BorrowRecord> getBorrowRecordsByReaderId(String readerId) {
        List<BorrowRecord> records = new ArrayList<>();

        String sql = "SELECT " +
                "b.id, " +
                "b.book_id AS bookId, " +
                "b.reader_id AS readerId, " +
                "b.borrow_date AS borrowDate, " +
                "b.return_date AS returnDate, " +
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

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, readerId);
            ResultSet rs = ps.executeQuery();

            int count = 0;
            while (rs.next()) {
                BorrowRecord br = new BorrowRecord();

                br.setId(rs.getInt("id"));
                br.setBookId(rs.getString("bookId"));
                br.setReaderId(rs.getString("readerId"));

                // 全部用 String 接收日期 —— 彻底避免转换异常
                br.setBorrowDate(rs.getString("borrowDate"));
                br.setReturnDate(rs.getString("returnDate"));
                br.setReturnApplyTime(rs.getString("returnApplyTime"));

                br.setStatus(rs.getString("status"));
                br.setFineAmount(rs.getBigDecimal("fineAmount"));
                br.setFineStatus(rs.getString("fineStatus"));
                br.setReturnApplyStatus(rs.getInt("returnApplyStatus"));

                br.setBookName(rs.getString("bookName"));
                br.setReaderName(rs.getString("readerName"));

                records.add(br);
                count++;
            }

            System.out.println("【AdminReaderDao】成功查询到读者 " + readerId + " 的借阅记录数量: " + count + " 条");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("【AdminReaderDao】查询异常: " + e.getMessage());
        }
        return records;
    }
}