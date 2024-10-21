package com.pure.weather.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CompareTodayResponse {
  private Double todayAvg;
  private Double todayMax;
  private Double todayMin;
  private Double yesterdayAvg;
  private Double yesterdayMax;
  private Double yesterdayMin;
}
