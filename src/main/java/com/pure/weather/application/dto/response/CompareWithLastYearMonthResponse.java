package com.pure.weather.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class CompareWithLastYearMonthResponse {
  private Double todayAvg;
  private Double lastYearMonthAvg;
}
