package com.whistledrop.dto;

import com.whistledrop.entity.ReportCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record CreateReportRequest(
    @NotNull ReportCategory category,
    @NotBlank @Size(min = 10, max = 10000) String description,
    @Size(max = 2048) @Pattern(regexp = "^$|https?://.+$", message = "evidenceUrl must be a valid HTTP or HTTPS URL") String evidenceUrl
) {}
