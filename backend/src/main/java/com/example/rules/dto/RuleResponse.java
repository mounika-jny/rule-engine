package com.example.rules.dto;

import com.example.rules.model.Rule;

public record RuleResponse(
  Long id,
  String appName,
  String name,
  String queryText,
  String compiledJson,
  String status
) {
  public static RuleResponse of(Rule r){
    return new RuleResponse(r.getId(), r.getAppName(), r.getName(), r.getQueryText(), r.getCompiledJson(), r.getStatus().name());
  }
}
