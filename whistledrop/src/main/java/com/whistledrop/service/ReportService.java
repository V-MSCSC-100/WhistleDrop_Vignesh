package com.whistledrop.service;

import com.whistledrop.dto.*;
import com.whistledrop.entity.*;
import com.whistledrop.exception.ApiException;
import com.whistledrop.repository.ReportRepository;
import com.whistledrop.repository.StatusUpdateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final StatusUpdateRepository statusUpdateRepository;
    private final CaseCodeService caseCodeService;

    public ReportService(ReportRepository reportRepository, StatusUpdateRepository statusUpdateRepository, CaseCodeService caseCodeService) {
        this.reportRepository = reportRepository;
        this.statusUpdateRepository = statusUpdateRepository;
        this.caseCodeService = caseCodeService;
    }

    @Transactional
    public ReportResponse create(CreateReportRequest request) {
        String caseCode = caseCodeService.generate();
        Report report = new Report();
        report.setCaseCodeHash(caseCodeService.hash(caseCode));
        report.setCategory(request.category());
        report.setDescription(request.description().trim());
        report.setEvidenceUrl(blankToNull(request.evidenceUrl()));
        report.setStatus(ReportStatus.SUBMITTED);
        reportRepository.save(report);
        addUpdate(report, ReportStatus.SUBMITTED, "Report submitted successfully and is awaiting review.");
        return toReporterResponse(report, caseCode);
    }

    @Transactional(readOnly = true)
    public ReportResponse getByCaseCode(String caseCode) {
        Report report = findByCaseCode(caseCode);
        return toReporterResponse(report, null);
    }

    @Transactional(readOnly = true)
    public List<ModeratorReportResponse> findReports(ReportCategory category, ReportStatus status) {
        List<Report> reports;
        if (category != null && status != null) reports = reportRepository.findAll((root, query, cb) -> cb.and(cb.equal(root.get("category"), category), cb.equal(root.get("status"), status)), Sort.by(Sort.Direction.DESC, "createdAt"));
        else if (category != null) reports = reportRepository.findAll((root, query, cb) -> cb.equal(root.get("category"), category), Sort.by(Sort.Direction.DESC, "createdAt"));
        else if (status != null) reports = reportRepository.findAll((root, query, cb) -> cb.equal(root.get("status"), status), Sort.by(Sort.Direction.DESC, "createdAt"));
        else reports = reportRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return reports.stream().map(this::toModeratorResponse).toList();
    }

    @Transactional
    public ModeratorReportResponse updateStatus(Long id, StatusUpdateRequest request) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        validateTransition(report.getStatus(), request.status());
        report.setStatus(request.status());
        addUpdate(report, request.status(), request.message().trim());
        reportRepository.save(report);
        return toModeratorResponse(report);
    }

    private void validateTransition(ReportStatus current, ReportStatus next) {
        if (current == next) throw new ApiException(HttpStatus.BAD_REQUEST, "Report is already in this status");
        boolean valid = (current == ReportStatus.SUBMITTED && next == ReportStatus.UNDER_REVIEW) ||
                (current == ReportStatus.UNDER_REVIEW && (next == ReportStatus.RESOLVED || next == ReportStatus.DISMISSED));
        if (!valid) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid status transition from " + current + " to " + next);
    }

    private void addUpdate(Report report, ReportStatus status, String message) {
        StatusUpdate update = new StatusUpdate();
        update.setReport(report);
        update.setStatus(status);
        update.setMessage(message);
        statusUpdateRepository.save(update);
        report.getStatusUpdates().add(update);
    }

    private Report findByCaseCode(String code) {
        if (code == null || !code.matches("WD-[A-Z2-9]{20}")) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid case code format");
        return reportRepository.findByCaseCodeHash(caseCodeService.hash(code)).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Case not found"));
    }

    private ReportResponse toReporterResponse(Report report, String caseCode) {
        return new ReportResponse(caseCode, report.getCategory(), report.getDescription(), report.getEvidenceUrl(), report.getStatus(), report.getCreatedAt(), report.getUpdatedAt(), updates(report));
    }

    private ModeratorReportResponse toModeratorResponse(Report report) {
        return new ModeratorReportResponse(report.getId(), report.getCategory(), report.getDescription(), report.getEvidenceUrl(), report.getStatus(), report.getCreatedAt(), report.getUpdatedAt(), updates(report));
    }

    private List<StatusUpdateResponse> updates(Report report) {
        return report.getStatusUpdates().stream().map(u -> new StatusUpdateResponse(u.getStatus(), u.getMessage(), u.getCreatedAt())).toList();
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
