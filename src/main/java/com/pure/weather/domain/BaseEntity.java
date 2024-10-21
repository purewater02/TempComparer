package com.pure.weather.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id = null;

  @CreatedDate
  @Column(updatable = false, nullable = false, name = "created_at")
  private LocalDateTime createdAt = null;

  @LastModifiedDate
  @Column(nullable = false, name = "updated_at")
  private LocalDateTime updatedAt = null;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt = null; // For Soft Delete
}
