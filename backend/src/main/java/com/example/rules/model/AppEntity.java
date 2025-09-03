package com.example.rules.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AppEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false)
  private App app;

  @Column(nullable=false)
  private String name; // e.g., "Employee", "Department"

  private String allowedFields; // e.g., "employeeId,departmentId"
}
