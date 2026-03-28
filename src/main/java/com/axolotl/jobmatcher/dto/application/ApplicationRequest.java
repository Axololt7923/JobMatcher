package com.axolotl.jobmatcher.dto.application;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Deprecated(forRemoval = false)
@Data
public class ApplicationRequest {

    @NotNull
    private UUID jobId;

    @NotNull
    private UUID cvId;
}