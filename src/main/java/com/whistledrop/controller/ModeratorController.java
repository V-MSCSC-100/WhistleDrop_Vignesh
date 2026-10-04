package com.whistledrop.controller;

import com.whistledrop.dto.ModeratorReportResponse;
import com.whistledrop.dto.StatusUpdateRequest;
import com.whistledrop.entity.ReportCategory;
import com.whistledrop.entity.ReportStatus;
import com.whistledrop.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/moderator/reports")
public class ModeratorController {
    private final ReportService reportService;
    public ModeratorController(ReportService reportService) { this.reportService = reportService; }

    @GetMapping
    List<ModeratorReportResponse> list(@RequestParam(required = false) ReportCategory category, @RequestParam(required = false) ReportStatus status) {
        return reportService.findReports(category, status);
    }

    @PatchMapping("/{id}/status")
    ModeratorReportResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return reportService.updateStatus(id, request);
    }
}
