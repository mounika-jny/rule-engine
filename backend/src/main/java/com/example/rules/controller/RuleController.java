package com.example.rules.controller;

import com.example.rules.dto.*;
import com.example.rules.model.Rule;
import com.example.rules.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RuleController {
  private final RuleService svc;

  @GetMapping
  public List<RuleResponse> list(@RequestParam(name = "app", required=false) String app){
    return svc.list(app).stream().map(RuleResponse::of).toList();
  }

  @PostMapping
  public RuleResponse create(@RequestBody @Validated CreateRuleRequest req){
    return RuleResponse.of(svc.create(req));
  }

  @PostMapping("/evaluate")
  public EvaluateResponse evaluate(@RequestBody EvaluateRequest req){
    return new EvaluateResponse(svc.evaluate(req));
  }
}
