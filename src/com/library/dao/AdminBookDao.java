package com.library.dao;

import com.library.entity.Book;
import com.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminBookDao {

    /**
     * 获取所有图书列表（适配你的数据库字段名）
     */
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            // 使用数据库实际字段名
            String sql = "SELECT book_id, ISBN, book_name, author, publisher, publish_year, " +
                    "book_type, total_count, borrowed_count, book_image,book_status " +
                    "FROM book ORDER BY create_time DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getString("book_id"));
                book.setIsbn(rs.getString("ISBN"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setBookType(rs.getString("book_type"));
                book.setTotalCount(rs.getInt("total_count"));
                book.setBorrowedCount(rs.getInt("borrowed_count"));
                book.setBookImage(rs.getString("book_image"));
                book.setBookStatus(rs.getString("book_status"));

                list.add(book);
            }

            System.out.println("【AdminBookDao】查询成功，共 " + list.size() + " 条图书");
            if (!list.isEmpty()) {
                System.out.println("第一本书： " + list.get(0).getBookName());
            }
        } catch (Exception e) {
            System.out.println("查询图书异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return list;
    }

    /**
     * 新增图书（适配数据库字段）
     */
    public boolean addBook(Book book) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "INSERT INTO book (" +
                    "book_id, ISBN, book_name, author, price, publisher, publish_year, " +
                    "book_type, total_count, borrowed_count, book_introduce, book_image, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, book.getBookId() != null ? book.getBookId().trim() : null);           // book_id
            pstmt.setString(2, book.getIsbn() != null ? book.getIsbn().trim() : null);               // ISBN
            pstmt.setString(3, book.getBookName() != null ? book.getBookName().trim() : "");         // book_name（不能为空）
            pstmt.setString(4, book.getAuthor() != null ? book.getAuthor().trim() : "");             // author（不能为空）
            pstmt.setDouble(5, book.getPrice());                                                    // price（double基本类型）
            pstmt.setString(6, book.getPublisher() != null ? book.getPublisher().trim() : "");       // publisher（不能为空）
            // 3. int基本类型字段：直接调用getter，无需判null
            pstmt.setInt(7, book.getPublishYear());                                                 // publish_year
            pstmt.setString(8, book.getBookType() != null ? book.getBookType().trim() : "");         // book_type（不能为空）
            pstmt.setInt(9, book.getTotalCount());                                                  // total_count
            pstmt.setInt(10, book.getBorrowedCount());                                              // borrowed_count
            pstmt.setString(11, book.getBookIntroduce() != null ? book.getBookIntroduce().trim() : ""); // book_introduce
            pstmt.setString(12, book.getBookImage() != null ? book.getBookImage() : "default.jpg");  // book_image（默认图）

            int rows = pstmt.executeUpdate();

            System.out.println("【AdminBookDao.addBook】执行结果 → 影响行数: " + rows +
                    " | 图书名: " + book.getBookName() +
                    " | book_id: " + book.getBookId());

            return rows > 0;

        } catch (Exception e) {
            System.out.println("【AdminBookDao.addBook】异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 删除图书
     */
    public boolean deleteBook(String bookId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "DELETE FROM book WHERE book_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookId);
            int rows = pstmt.executeUpdate();
            System.out.println("删除图书 " + bookId + "，影响行数：" + rows);
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }

    /**
     * 更新图书信息
     */
    public boolean updateBook(Book book) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();

            String sql = "UPDATE book SET " +
                    "book_name = ?, author = ?, price = ?, publisher = ?, publish_year = ?, " +
                    "book_type = ?, total_count = ?, book_introduce = ?, update_time = NOW() " +
                    "WHERE book_id = ?";

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, book.getBookName());
            pstmt.setString(2, book.getAuthor());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getPublishYear());
            pstmt.setString(6, book.getBookType());
            pstmt.setInt(7, book.getTotalCount());
            pstmt.setString(8, book.getBookIntroduce() != null ? book.getBookIntroduce() : "");
            pstmt.setString(9, book.getBookId());   // WHERE 条件

            int rows = pstmt.executeUpdate();
            System.out.println("【更新图书】book_id=" + book.getBookId() + "，影响行数: " + rows);

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("更新图书异常: " + e.getMessage());
            return false;
        } finally {
            DBUtil.close(conn, pstmt, null);
        }
    }
    /**
     * 检查该图书是否有未归还的借阅记录
     * @param bookId 图书编号
     * @return 未归还数量（>0 表示不能删除）
     */
    public int countUnreturnedBorrows(String bookId) {
        String sql = "SELECT COUNT(*) FROM borrow WHERE book_id = ? AND return_date IS NULL";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;  // 发生异常时默认允许删除（或改为返回1阻止删除）
    }

    /**
     * 根据 bookId 查询单个图书（用于删除前获取图片路径，方便删除服务器文件）
     * @param bookId 图书编号
     * @return Book 对象（可能为null）
     */
    public Book getBookById(String bookId) {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        Book book = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    book = new Book();
                    book.setBookId(rs.getString("book_id"));
                    book.setIsbn(rs.getString("ISBN"));
                    book.setBookName(rs.getString("book_name"));
                    book.setAuthor(rs.getString("author"));
                    book.setPrice(rs.getDouble("price"));
                    book.setPublisher(rs.getString("publisher"));
                    book.setPublishYear(rs.getInt("publish_year"));
                    book.setBookType(rs.getString("book_type"));
                    book.setTotalCount(rs.getInt("total_count"));
                    book.setBorrowedCount(rs.getInt("borrowed_count"));
                    book.setBookIntroduce(rs.getString("book_introduce"));
                    book.setBookImage(rs.getString("book_image"));
                    // 其他字段按需映射...
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    /**
     * 更新图书状态（正常 <-> 已下架）
     */
    /**
     * 更新图书上下架状态
     */
    public boolean updateBookStatus(String bookId, String status) {
        String sql = "UPDATE book SET book_status = ?, update_time = NOW() WHERE book_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, bookId);

            int rows = ps.executeUpdate();
            System.out.println("更新图书状态影响行数: " + rows);
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}