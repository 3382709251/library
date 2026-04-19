package com.library.servlet;

import com.library.dao.AdminReaderDao;
import com.library.dao.ReaderDao;
import com.library.entity.BorrowRecord;
import com.library.entity.Reader;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminReaderDetailServlet extends HttpServlet {

    private final AdminReaderDao adminReaderDao = new AdminReaderDao();
    private final ReaderDao readerDao = new ReaderDao();
    // 日期格式化器：严格匹配数据库的 yyyy-MM-dd 格式
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // 常量：单本罚款上限（20元）
    private static final BigDecimal MAX_SINGLE_FINE = BigDecimal.valueOf(30.0);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String readerId = req.getParameter("readerId");
        Gson gson = new Gson();

        // 1. 校验读者ID
        if (readerId == null || readerId.trim().isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("records", List.of());
            errorResult.put("totalFine", 0.0);
            errorResult.put("error", "读者ID不能为空");
            resp.getWriter().write(gson.toJson(errorResult));
            return;
        }

        try {
            // 2. 获取读者信息（核心：拿到最大借阅时长）
            Reader reader = readerDao.findById(readerId);
            if (reader == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("records", List.of());
                errorResult.put("totalFine", 0.0);
                errorResult.put("error", "读者不存在");
                resp.getWriter().write(gson.toJson(errorResult));
                return;
            }
            int maxBorrowTime = reader.getMaxBorrowTime(); // R001 应为30天
            System.out.println("【调试】读者" + readerId + "最大借阅时长：" + maxBorrowTime + "天");

            // 3. 获取借阅记录
            List<BorrowRecord> records = adminReaderDao.getBorrowRecordsByReaderId(readerId);
            System.out.println("【调试】原始记录数量：" + records.size());

            // 4. 遍历记录，强制计算逾期天数和罚款（完全匹配你的实体类）
            // 移除：循环外的 overdueDays 变量，避免重复覆盖
            for (BorrowRecord record : records) {
                // 打印原始数据，排查字段值
                System.out.println("【调试】记录ID：" + record.getId() +
                        " | borrowDate：" + record.getBorrowDate() +
                        " | returnDate：" + record.getReturnDate() +
                        " | status：" + record.getStatus());

                // 修复：双重判断「未归还」— status + returnDate 都要检查（避免已归还书仍算罚金）
                boolean isReturned = record.getReturnDate() != null && !record.getReturnDate().isEmpty();
                if (!isReturned && "未归还".equals(record.getStatus())
                        && record.getBorrowDate() != null
                        && !"".equals(record.getBorrowDate())) {

                    try {
                        // 修复：正确解析日期（兼容字符串格式）
                        LocalDate borrowDate = LocalDate.parse(record.getBorrowDate(), DATE_FORMATTER);
                        LocalDate today = LocalDate.now();

                        // 计算借阅总天数（从借阅日到今天）
                        long borrowTotalDays = ChronoUnit.DAYS.between(borrowDate, today);
                        // 计算逾期天数 = 借阅总天数 - 最大借阅时长（小于0则为0）
                        long overdueDays = Math.max(0, borrowTotalDays - maxBorrowTime);
                        BigDecimal fineAmount;
                        if (overdueDays == 0) {
                            fineAmount = BigDecimal.ZERO;
                        } else if (overdueDays <= 15) {
                            fineAmount = new BigDecimal("5");
                        } else {
                            fineAmount = BigDecimal.valueOf((overdueDays - 15) * 1.0 + 5);
                        }

                        final BigDecimal MAX_SINGLE_FINE = new BigDecimal("30");
                        if (fineAmount.compareTo(MAX_SINGLE_FINE) > 0) {
                            fineAmount = MAX_SINGLE_FINE;
                        }
                        // 关键：给实体类赋值（完全匹配你的字段和方法）
                        record.setOverdueDays(overdueDays); // 逾期天数
                        record.setFineAmount(fineAmount);   // 本书罚款（BigDecimal 类型）

                        System.out.println("【调试】记录ID：" + record.getId() +
                                " | 借阅天数：" + borrowTotalDays +
                                " | 逾期天数：" + overdueDays +
                                " | 本书罚款（上限20元）：" + fineAmount);
                    } catch (Exception e) {
                        System.out.println("【调试】记录ID：" + record.getId() + "日期解析失败：" + e.getMessage());
                        // 解析失败时置0
                        record.setOverdueDays(0);
                        record.setFineAmount(BigDecimal.ZERO);
                    }
                } else {
                    // 已归还记录，逾期天数和罚款强制置0
                    record.setOverdueDays(0);
                    record.setFineAmount(BigDecimal.ZERO);
                }
            }

            // 5. 基于当前内存中计算的记录罚款求和（确保与单本书罚款一致）
            BigDecimal totalFineBD = BigDecimal.ZERO;
            for (BorrowRecord record : records) {
                if (record.getFineAmount() != null) {
                    totalFineBD = totalFineBD.add(record.getFineAmount());
                }
            }
            double totalFine = totalFineBD.doubleValue();
            System.out.println("【调试】读者" + readerId + "总罚款（内存求和）：" + totalFine);

            // 5.2 同步更新数据库 Account_Money（解决 readerList 与 readerDetail 不一致）
            readerDao.updateAccountMoney(readerId, totalFineBD);
            System.out.println("【调试】已同步数据库 Account_Money = " + totalFine);

            // 6. 组装返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("records", records);       // 带逾期天数和罚款的记录列表
            result.put("totalFine", totalFine);   // 读者总罚款
            result.put("maxBorrowTime", maxBorrowTime); // 读者最大借阅时长

            // 打印最终返回的JSON，方便排查
            String json = gson.toJson(result);
            System.out.println("【调试】最终返回JSON：" + json);
            resp.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            // 异常时返回友好提示
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("records", List.of());
            errorResult.put("totalFine", 0.0);
            errorResult.put("error", "查询借阅详情失败：" + e.getMessage());
            resp.getWriter().write(gson.toJson(errorResult));
        }
    }
}