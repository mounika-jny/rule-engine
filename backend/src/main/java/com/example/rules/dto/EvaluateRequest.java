package com.example.rules.dto;

import java.util.Map;

public record EvaluateRequest(
  Long ruleId,
  Map<String, Object> variables,
  Map<String, Object> record
) {}
