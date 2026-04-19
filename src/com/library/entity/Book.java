package com.library.entity;

import java.sql.Timestamp;

/**
 * 图书实体类：对应数据库 book 表（优化后）
 */
public class Book {
    // 原有字段（保留）
    private String bookId;
    private String isbn;
    private String bookName;
    private String author;
    private String publisher;
    private int publishYear;
    private String bookType;
    private int totalCount;
    private int borrowedCount;
    private String bookIntroduce;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Double fineAmount;
    private String fineStatus;
    private String bookImage;

    // 新增：price成员变量（double基本类型，符合业务逻辑）
    private double price;
    private String bookStatus;

    // 无参构造
    public Book() {}

    // 全参构造（补充price字段）
    public Book(String bookId, String isbn, String bookName, String author, String publisher,
                int publishYear, String bookType, int totalCount, int borrowedCount,
                String bookIntroduce, Timestamp createTime, Timestamp updateTime, double price) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.bookName = bookName;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.bookType = bookType;
        this.totalCount = totalCount;
        this.borrowedCount = borrowedCount;
        this.bookIntroduce = bookIntroduce;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.price = price; // 补充price赋值
    }

    // 原有getter/setter（保留）
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public int getPublishYear() { return publishYear; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
    public String getBookType() { return bookType; }
    public void setBookType(String bookType) { this.bookType = bookType; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getBorrowedCount() { return borrowedCount; }
    public void setBorrowedCount(int borrowedCount) { this.borrowedCount = borrowedCount; }
    public String getBookIntroduce() { return bookIntroduce; }
    public void setBookIntroduce(String bookIntroduce) { this.bookIntroduce = bookIntroduce; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
    public String getBookImage() { return bookImage; }
    public void setBookImage(String bookImage) { this.bookImage = bookImage; }
    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }
    public String getFineStatus() { return fineStatus; }
    public void setFineStatus(String fineStatus) { this.fineStatus = fineStatus; }

    // 完善price的get/set（不再返回固定0.0）
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        // 价格合法性校验：不能为负数
        this.price = price < 0 ? 0.0 : price;
    }

    // toString方法（补充price字段）
    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", bookName='" + bookName + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishYear=" + publishYear +
                ", bookType='" + bookType + '\'' +
                ", totalCount=" + totalCount +
                ", borrowedCount=" + borrowedCount +
                ", price=" + price + // 补充price
                '}';
    }

    public String getBookStatus() { return bookStatus; }
    public void setBookStatus(String bookStatus) { this.bookStatus = bookStatus; }
}