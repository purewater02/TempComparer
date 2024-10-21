package com.pure.weather.domain;

import com.pure.weather.enums.City;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "temperatures",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"city", "date"})})
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Temperature extends BaseEntity {
  @Column(name = "city")
  @Enumerated(EnumType.STRING)
  private City city;

  @Column(name = "date")
  private LocalDate date;

  @Column(name = "average_temp")
  private Double averageTemp;

  @Column(name = "max_temp")
  private Double maxTemp;

  @Column(name = "min_temp")
  private Double minTemp;

  public void update(Double averageTemp, Double maxTemp, Double minTemp) {
    this.averageTemp = averageTemp;
    this.maxTemp = maxTemp;
    this.minTemp = minTemp;
  }
}
