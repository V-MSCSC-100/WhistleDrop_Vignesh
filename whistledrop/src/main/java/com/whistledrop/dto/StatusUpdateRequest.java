package com.whistledrop.dto;

import com.whistledrop.entity.ReportStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StatusUpdateRequest(
    @NotNull ReportStatus status,
    @NotBlank @Size(max = 1000) String message
) {}
