package com.example.rules.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public record CreateRuleRequest(
  @NotBlank String appName,
  @NotBlank String name,
  @NotBlank String queryText,
  @NotNull List<String> entities,
  Map<String, List<Object>> variables
) {}
