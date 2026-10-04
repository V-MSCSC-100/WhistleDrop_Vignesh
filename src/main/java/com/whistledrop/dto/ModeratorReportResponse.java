package com.whistledrop.dto;

import com.whistledrop.entity.*;
import java.time.Instant;
import java.util.List;

public record ModeratorReportResponse(
    Long id,
    ReportCategory category,
    String description,
    String evidenceUrl,
    ReportStatus status,
    Instant createdAt,
    Instant updatedAt,
    List<StatusUpdateResponse> updates
) {}
