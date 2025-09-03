package com.example.rules.controller;

import com.example.rules.model.App;
import com.example.rules.model.AppEntity;
import com.example.rules.repo.AppEntityRepository;
import com.example.rules.repo.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/apps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppController {
  private final AppRepository apps;
  private final AppEntityRepository entities;

  @GetMapping
  public List<App> listApps(){ return apps.findAll(); }

  @GetMapping("/{appName}/entities")
  public List<AppEntity> listEntities(@PathVariable(name = "appName") String appName){
    return entities.findByApp_NameIgnoreCase(appName);
  }
}
