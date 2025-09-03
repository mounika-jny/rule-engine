package com.example.rules.repo;

import com.example.rules.model.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppEntityRepository extends JpaRepository<AppEntity, Long> {
  List<AppEntity> findByApp_NameIgnoreCase(String appName);
}
