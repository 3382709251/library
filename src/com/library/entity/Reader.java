package com.library.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 读者实体类：与reader表字段一一对应（完全匹配数据库）
 */
public class Reader {
    // 1. 数据库字段对应的成员变量（名称与数据库字段一一映射，注释标注对应关系）
    private String readerId;        // 对应数据库 Reader_ID
    private String readerName;      // 对应数据库 reader_name）
    private String college;         // 对应数据库 college
    private int readerCategory;     // 对应数据库 Reader_Category
    private String password;        // 对应数据库 Reader_Password
    private Date createTime;        // 对应数据库 Create_time（datetime类型用Date接收）
    private Date updateTime;        // 对应数据库 Update_time（datetime类型用Date接收）
    private int maxBorrowTime;      // 对应数据库 Max_borrow_time
    private int maxBorrowCount;     // 对应数据库 Max_borrow_count
    private BigDecimal accountMoney;// 对应数据库 Account_Money（decimal类型用BigDecimal）
    private String readerPhone;     // 对应数据库 readerphone

    // 2. 所有字段的 Getter & Setter 方法
    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public int getReaderCategory() {
        return readerCategory;
    }

    public void setReaderCategory(int readerCategory) {
        this.readerCategory = readerCategory;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getMaxBorrowTime() {
        return maxBorrowTime;
    }

    public void setMaxBorrowTime(int maxBorrowTime) {
        this.maxBorrowTime = maxBorrowTime;
    }

    public int getMaxBorrowCount() {
        return maxBorrowCount;
    }

    public void setMaxBorrowCount(int maxBorrowCount) {
        this.maxBorrowCount = maxBorrowCount;
    }

    public BigDecimal getAccountMoney() {
        return accountMoney;
    }

    public void setAccountMoney(BigDecimal accountMoney) {
        this.accountMoney = accountMoney;
    }

    public String getReaderPhone() {
        return readerPhone;
    }

    public void setReaderPhone(String readerPhone) {
        this.readerPhone = readerPhone;
    }


    // 在 Reader.java 中补充以下方法（如果没有的话）
}