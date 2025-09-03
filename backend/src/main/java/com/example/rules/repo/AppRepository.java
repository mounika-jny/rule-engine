package com.example.rules.repo;

import com.example.rules.model.App;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppRepository extends JpaRepository<App, Long> {
  Optional<App> findByNameIgnoreCase(String name);
}
