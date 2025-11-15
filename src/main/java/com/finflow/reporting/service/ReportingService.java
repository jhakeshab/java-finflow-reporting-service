package com.finflow.reporting.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReportingService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    private final String WALLET_URL = "http://localhost:9002/api/wallet";
    private final String PAYMENT_URL = "http://localhost:9003/api/payment";

    public Map<String, Object> getSummary(Long userId) {
        // Cache key
        String cacheKey = "summary:" + userId;
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (summary != null) return summary;

        // Aggregate
        List wallets = restTemplate.getForObject(WALLET_URL + "/user/" + userId + "/wallets", List.class); // Assume endpoint added
        List payments = restTemplate.getForObject(PAYMENT_URL + "/user/" + userId, List.class); // Assume

        summary = new HashMap<>();
        summary.put("totalBalance", calculateTotalBalance(wallets));
        summary.put("transactionCount", payments.size());
        // Add more: avg txn, etc.

        redisTemplate.opsForValue().set(cacheKey, summary, 3600); // 1 hour
        return summary;
    }

    private BigDecimal calculateTotalBalance(List wallets) {
        // Logic to sum balances
        return BigDecimal.ZERO; // Placeholder
    }

    public ByteArrayInputStream exportToExcel(String type, Long userId) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(type);

        // Headers
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Amount");

        // Data from services
        Map<String, Object> summary = getSummary(userId);
        int rowNum = 1;
        // Populate from payments/wallets, e.g.
        // for each txn: Row row = sheet.createRow(rowNum++); row.createCell(0).setCellValue(txnDate); etc.

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}