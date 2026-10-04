package com.whistledrop.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "status_updates")
public class StatusUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportStatus status;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Report getReport() { return report; }
    public void setReport(Report value) { report = value; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus value) { status = value; }
    public String getMessage() { return message; }
    public void setMessage(String value) { message = value; }
    public Instant getCreatedAt() { return createdAt; }
}
