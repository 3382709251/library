package com.library.entity;
public class Admin {
    private String adminId; // 修复：数据库是varchar，改为String
    private String loginAccount;
    private String loginPassword;
    private String adminName;
    private String adminRole;
    private String phone; // 保持字段名，修改set方法适配

    public Admin() {}

    // 修复：adminId改为String类型
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getLoginAccount() { return loginAccount; }
    public void setLoginAccount(String loginAccount) { this.loginAccount = loginAccount; }

    public String getLoginPassword() { return loginPassword; }
    public void setLoginPassword(String loginPassword) { this.loginPassword = loginPassword; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminRole() { return adminRole; }
    public void setAdminRole(String adminRole) { this.adminRole = adminRole; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}