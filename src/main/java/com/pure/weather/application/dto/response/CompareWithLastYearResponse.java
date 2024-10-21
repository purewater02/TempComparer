package com.pure.weather.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CompareWithLastYearResponse {
  private String type;
  private String city;
  private Double todayAvg;
  private Double lastYearWeekAvg;
}
