package com.example.rules.repo;

import com.example.rules.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuleRepository extends JpaRepository<Rule, Long> {
  List<Rule> findByAppNameIgnoreCase(String appName);
}
