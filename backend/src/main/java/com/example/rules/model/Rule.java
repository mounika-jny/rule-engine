package com.example.rules.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "rules")
public class Rule {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false)
  private String appName;

  @Column(nullable=false)
  private String name;

  @Lob @Column(nullable=false)
  private String queryText;

  @Lob
  private String compiledJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private Status status;

  private Instant createdAt;
  private Instant updatedAt;
  private String createdBy;

  public enum Status { DRAFT, SUBMITTED, APPROVED, REJECTED }
}
