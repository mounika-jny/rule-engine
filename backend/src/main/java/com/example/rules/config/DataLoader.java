package com.example.rules.config;

import com.example.rules.model.App;
import com.example.rules.model.AppEntity;
import com.example.rules.repo.AppEntityRepository;
import com.example.rules.repo.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
  private final AppRepository apps; 
  private final AppEntityRepository entities;

  @Override public void run(String... args){
    App app1 = apps.findByNameIgnoreCase("App1").orElseGet(() -> apps.save(App.builder().name("App1").build()));
    if (entities.findByApp_NameIgnoreCase("App1").isEmpty()){
      entities.save(AppEntity.builder().app(app1).name("Employee").allowedFields("employeeId").build());
      entities.save(AppEntity.builder().app(app1).name("Department").allowedFields("departmentId").build());
    }
  }
}
