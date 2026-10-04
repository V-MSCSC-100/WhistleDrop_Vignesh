package com.whistledrop.controller;

import com.whistledrop.dto.CreateReportRequest;
import com.whistledrop.dto.ReportResponse;
import com.whistledrop.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    public ReportController(ReportService reportService) { this.reportService = reportService; }

    @PostMapping
    ResponseEntity<ReportResponse> create(@Valid @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportService.create(request));
    }

    @GetMapping("/{caseCode}")
    ReportResponse get(@PathVariable String caseCode) { return reportService.getByCaseCode(caseCode); }
}
