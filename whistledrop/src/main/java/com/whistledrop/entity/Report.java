package com.whistledrop.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_report_case_hash", columnList = "case_code_hash", unique = true),
    @Index(name = "idx_report_status", columnList = "status"),
    @Index(name = "idx_report_category", columnList = "category")
})
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "case_code_hash", nullable = false, unique = true, length = 64)
    private String caseCodeHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportCategory category;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "evidence_url", length = 2048)
    private String evidenceUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<StatusUpdate> statusUpdates = new ArrayList<>();

    @PrePersist
    void prePersist() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate
    void preUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public String getCaseCodeHash() { return caseCodeHash; }
    public void setCaseCodeHash(String value) { caseCodeHash = value; }
    public ReportCategory getCategory() { return category; }
    public void setCategory(ReportCategory value) { category = value; }
    public String getDescription() { return description; }
    public void setDescription(String value) { description = value; }
    public String getEvidenceUrl() { return evidenceUrl; }
    public void setEvidenceUrl(String value) { evidenceUrl = value; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus value) { status = value; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<StatusUpdate> getStatusUpdates() { return statusUpdates; }
}
