package com.finflow.reporting.controller;

import com.finflow.reporting.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.*;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {
    private final ReportingService reportingService;

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(reportingService.getSummary(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(@RequestParam String startDate, @RequestParam String endDate, @RequestParam Long userId) {
        // Call payment service with dates
        return ResponseEntity.ok(Map.of("transactions", "list")); // Placeholder
    }

    @PostMapping("/export")
    public ResponseEntity<InputStreamResource> export(@RequestParam String type, @RequestParam Long userId) {
        ByteArrayInputStream stream = reportingService.exportToExcel(type, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }
}