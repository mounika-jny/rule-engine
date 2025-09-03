package com.example.rules.controller;

import com.example.rules.dto.RuleResponse;
import com.example.rules.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkflowController {
  private final RuleService svc;

  @PostMapping("/{id}/submit") public RuleResponse submit(@PathVariable(name = "id") Long id){ return RuleResponse.of(svc.submit(id)); }
  @PostMapping("/{id}/approve") public RuleResponse approve(@PathVariable(name = "id") Long id){ return RuleResponse.of(svc.approve(id)); }
  @PostMapping("/{id}/reject") public RuleResponse reject(@PathVariable(name = "id") Long id){ return RuleResponse.of(svc.reject(id)); }
}
