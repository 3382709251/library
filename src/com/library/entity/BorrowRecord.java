package com.library.entity;

import java.sql.Date;        // 使用 java.sql.Date 更适合 borrow_date / return_date
import java.math.BigDecimal;

public class BorrowRecord {

    private int id;
    private String readerId;
    private String bookId;
    private String bookName;        // 关联查询得到的图书名称
    private String readerName;      // 关联查询得到的读者姓名

    private String borrowDate;     // 字符串格式的借阅时间（兼容前端展示）
    private String returnDate;     // 字符串格式的归还时间

    private String status;
    private BigDecimal fineAmount;  // 罚款金额（BigDecimal 保证精度）
    private String fineStatus;      // 罚款状态 ('未缴纳', '已缴纳')

    // 可选：保留你原来的 Date 字段（兼容 Dao 层时间计算）
    private Date borrowTime;   // 对应数据库 borrow_date (date 类型)
    private Date returnTime;   // 对应数据库 return_date (date 类型)

    // 归还申请相关字段
    private String returnApplyTime; // 归还申请时间
    private Integer returnApplyStatus; // 归还申请状态：0-未申请，1-已申请，2-已确认

    // 关联图书对象
    private Book book;

    // ====================== 核心补充：fineAmount 的 Getter 方法（缺失修复） ======================
    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    // ====================== 兼容 double 类型的罚款设置（适配 Dao 层计算） ======================
    // 重载 setFineAmount 方法，支持 double 类型（避免类型转换错误）
    public void setFineAmount(double fineAmount) {
        this.fineAmount = BigDecimal.valueOf(fineAmount);
    }

    // ====================== 原有 Getter/Setter 方法（完整保留+修正） ======================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 原有 BigDecimal 类型的 setFineAmount（保留，兼容数据库查询）
    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(String fineStatus) {
        this.fineStatus = fineStatus;
    }

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getReturnApplyTime() {
        return returnApplyTime;
    }

    public void setReturnApplyTime(String returnApplyTime) {
        this.returnApplyTime = returnApplyTime;
    }

    public Integer getReturnApplyStatus() {
        return returnApplyStatus;
    }

    public void setReturnApplyStatus(Integer returnApplyStatus) {
        this.returnApplyStatus = returnApplyStatus;
    }

    // 在BorrowRecord类中新增
    private long overdueDays; // 逾期天数

    // 新增getter/setter
    public long getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(long overdueDays) {
        this.overdueDays = overdueDays;
    }
}