package com.library.dao;

import com.library.entity.Book;
import com.library.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书数据访问层（DAO）：封装所有图书相关的数据库操作
 * 适配数据库 book 表结构，字段名：book_id, ISBN, book_name, author, publisher, publish_year,
 * book_type, total_count, borrowed_count, book_introduce, create_time, update_time
 */
public class BookDao {

    /**
     * 查询所有图书（无筛选条件）
     * @return 所有图书的列表，无数据返回空列表
     */
    public List<Book> findAllBooks() {
        List<Book> bookList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // SQL：查询所有字段，与数据库表完全匹配
        String sql = "SELECT book_id, ISBN, book_name, author, publisher, publish_year, "
                + "book_type, total_count, borrowed_count, book_introduce, "
                + "book_image,create_time, update_time FROM book";

        try {
            // 1. 获取数据库连接
            conn = DBUtil.getConnection();
            // 2. 预编译SQL
            pstmt = conn.prepareStatement(sql);
            // 3. 执行查询
            rs = pstmt.executeQuery();

            // 4. 遍历结果集，封装Book对象
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
                book.setBookIntroduce(rs.getString("book_introduce"));
                book.setCreateTime(rs.getTimestamp("create_time"));
                book.setUpdateTime(rs.getTimestamp("update_time"));
                book.setBookImage(rs.getString("book_image"));

                bookList.add(book);
            }
            System.out.println("查询所有图书成功，共" + bookList.size() + "条数据");
        } catch (SQLException e) {
            System.err.println("查询所有图书异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 5. 关闭数据库资源（必须执行，避免内存泄漏）
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }

    /**
     * 根据图书ID查询单本图书详情
     * @param bookId 图书ID（对应数据库 book_id 字段）
     * @return 匹配的Book对象，无数据返回null
     */
    public Book findBookById(String bookId) {
        Book book = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // SQL：按ID精准查询
        String sql = "SELECT book_id, ISBN, book_name, author, publisher, publish_year, "
                + "book_type, total_count, borrowed_count, book_introduce, "
                + "book_image,create_time, update_time FROM book WHERE book_id = ?";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            // 设置占位符参数（图书ID）
            pstmt.setString(1, bookId);
            rs = pstmt.executeQuery();

            // 封装单条数据
            if (rs.next()) {
                book = new Book();
                book.setBookId(rs.getString("book_id"));
                book.setIsbn(rs.getString("ISBN"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setBookType(rs.getString("book_type"));
                book.setTotalCount(rs.getInt("total_count"));
                book.setBorrowedCount(rs.getInt("borrowed_count"));
                book.setBookIntroduce(rs.getString("book_introduce"));
                book.setCreateTime(rs.getTimestamp("create_time"));
                book.setUpdateTime(rs.getTimestamp("update_time"));
                book.setBookImage(rs.getString("book_image"));
            }
            System.out.println("根据ID[" + bookId + "]查询图书：" + (book != null ? "成功" : "未找到"));
        } catch (SQLException e) {
            System.err.println("根据ID查询图书异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return book;
    }

    /**
     * 根据图书类型+关键词筛选图书（搜索功能核心）
     * @param bookType 图书类型（null/空则不筛选）
     * @param keyword 搜索关键词（匹配书名/作者/ISBN，null/空则不搜索）
     * @return 筛选后的图书列表
     */
    public List<Book> getBooksByFilter(String bookType, String keyword) {
        List<Book> bookList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        // 1. 动态拼接SQL（避免SQL注入，使用参数化查询）
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT book_id, ISBN, book_name, author, publisher, publish_year, ");
        sql.append("book_type, total_count, borrowed_count, book_introduce, ");
        sql.append("book_image,create_time, update_time FROM book WHERE 1=1");

        // 存储参数的列表（按顺序）
        List<Object> params = new ArrayList<>();

        // 2. 添加图书类型筛选条件
        if (bookType != null && !bookType.trim().isEmpty()) {
            sql.append(" AND book_type = ?");
            params.add(bookType.trim());
        }

        // 3. 添加关键词搜索条件（模糊匹配书名/作者/ISBN）
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (book_name LIKE ? OR author LIKE ? OR ISBN LIKE ?)");
            String likeKeyword = "%" + keyword.trim() + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            // 4. 设置所有参数（按占位符顺序）
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            // 5. 执行查询并封装结果
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
                book.setBookIntroduce(rs.getString("book_introduce"));
                book.setCreateTime(rs.getTimestamp("create_time"));
                book.setUpdateTime(rs.getTimestamp("update_time"));
                book.setBookImage(rs.getString("book_image"));

                bookList.add(book);
            }
            System.out.println("筛选查询图书成功（类型：" + bookType + "，关键词：" + keyword + "），共" + bookList.size() + "条数据");
        } catch (SQLException e) {
            System.err.println("筛选查询图书异常：" + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }

    /**
     * 更新图书已借数量（借阅+1，归还-1）
     * @param bookId 图书ID
     * @param change 变化量（+1=借阅，-1=归还）
     * @return true=更新成功，false=更新失败
     */
    public boolean updateBorrowedCount(String bookId, int change) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int affectedRows = 0;

        // SQL：更新已借数量（基于当前值增减）
        String sql = "UPDATE book SET borrowed_count = borrowed_count + ? WHERE book_id = ?";

        try {
            conn = DBUtil.getConnection();
            // 开启事务（可选，保证数据一致性）
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, change);
            pstmt.setString(2, bookId);

            // 执行更新
            affectedRows = pstmt.executeUpdate();
            conn.commit(); // 提交事务

            System.out.println("更新图书[" + bookId + "]已借数量（变化量：" + change + "）：" + (affectedRows > 0 ? "成功" : "失败"));
        } catch (SQLException e) {
            System.err.println("更新已借数量异常：" + e.getMessage());
            e.printStackTrace();
            // 回滚事务
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            // 恢复自动提交
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBUtil.close(conn, pstmt);
        }
        return affectedRows > 0;
    }

    /**
     * 测试方法：验证数据库连接和查询功能
     */
    public static void main(String[] args) {
        BookDao bookDao = new BookDao();

        // 1. 测试查询所有图书
        List<Book> allBooks = bookDao.findAllBooks();
        System.out.println("===== 测试查询所有图书 =====");
        System.out.println("总图书数：" + allBooks.size());

        // 2. 测试按ID查询（替换为你数据库中存在的图书ID）
        if (!allBooks.isEmpty()) {
            String testBookId = allBooks.get(0).getBookId();
            Book book = bookDao.findBookById(testBookId);
            System.out.println("\n===== 测试按ID查询 =====");
            System.out.println("图书ID：" + book.getBookId() + "，书名：" + book.getBookName());
        }

        // 3. 测试筛选查询
        List<Book> filterBooks = bookDao.getBooksByFilter("小说", "三");
        System.out.println("\n===== 测试筛选查询（类型：小说，关键词：三） =====");
        System.out.println("筛选结果数：" + filterBooks.size());
    }

    /**
     * 根据 book_type 查询图书（首页分类专用）
     * @param bookType 图书类型
     * @return 该类型的图书列表
     */
    public List<Book> findByBookType(String bookType) {
        // 直接复用你已经写好的筛选方法（bookType 不为空，keyword 为空）
        return getBooksByFilter(bookType, null);
    }

    /**
     * 获取借阅排行榜（全部图书）
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
                book.setBookImage(rs.getString("book_image"));   // 如果你有这个字段

                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }

    /**
     * 按分类获取借阅排行榜
     */
    public List<Book> getBorrowRankByCategory(String category) {
        List<Book> bookList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM book WHERE book_type = ? ORDER BY borrowed_count DESC LIMIT 20";

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getString("book_id"));
                book.setBookName(rs.getString("book_name"));
                book.setAuthor(rs.getString("author"));
                book.setBorrowedCount(rs.getInt("borrowed_count"));
                book.setBookImage(rs.getString("book_image"));

                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }

    /**
     * 获取借阅排行前3名（专门供首页轮播图使用）
     */
    public List<Book> getBorrowRankTop3() {
        List<Book> bookList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM book ORDER BY borrowed_count DESC LIMIT 3";

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
                book.setBookImage(rs.getString("book_image"));

                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return bookList;
    }


}