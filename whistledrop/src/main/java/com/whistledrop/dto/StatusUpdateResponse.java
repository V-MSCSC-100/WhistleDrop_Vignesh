package com.whistledrop.dto;

import com.whistledrop.entity.ReportStatus;
import java.time.Instant;

public record StatusUpdateResponse(ReportStatus status, String message, Instant createdAt) {}
