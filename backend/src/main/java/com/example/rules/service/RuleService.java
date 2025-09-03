package com.example.rules.service;

import com.example.rules.dto.*;
import com.example.rules.model.Rule;
import com.example.rules.repo.RuleRepository;
import com.example.rules.util.JsonUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RuleService {
  private final RuleRepository rules;
  private final RuleParser parser = new RuleParser();
  private final RuleEvaluator evaluator = new RuleEvaluator();

  @Transactional
  public Rule create(CreateRuleRequest req){
    ObjectNode compiled = parser.parse(req.appName(), req.entities(), req.queryText());
    Rule r = Rule.builder()
      .appName(req.appName())
      .name(req.name())
      .queryText(req.queryText())
      .compiledJson(compiled.toString())
      .status(Rule.Status.DRAFT)
      .createdAt(Instant.now())
      .updatedAt(Instant.now())
      .createdBy("system")
      .build();
    return rules.save(r);
  }

  public List<Rule> list(String app){
    return (app==null||app.isBlank()) ? rules.findAll() : rules.findByAppNameIgnoreCase(app);
  }

  @Transactional
  public Rule submit(Long id){
    Rule r = get(id); r.setStatus(Rule.Status.SUBMITTED); r.setUpdatedAt(Instant.now()); return r;
  }
  @Transactional
  public Rule approve(Long id){
    Rule r = get(id); r.setStatus(Rule.Status.APPROVED); r.setUpdatedAt(Instant.now()); return r;
  }
  @Transactional
  public Rule reject(Long id){
    Rule r = get(id); r.setStatus(Rule.Status.REJECTED); r.setUpdatedAt(Instant.now()); return r;
  }

  public boolean evaluate(EvaluateRequest req){
    Rule r = get(req.ruleId());
    try {
      var compiled = JsonUtil.MAPPER.readTree(r.getCompiledJson());
      return evaluator.evaluate(compiled, req.variables(), req.record());
    } catch (Exception e){
      throw new RuntimeException("Evaluation failed", e);
    }
  }

  public Rule get(Long id){
    return rules.findById(id).orElseThrow(() -> new NoSuchElementException("Rule not found: "+id));
  }
}
