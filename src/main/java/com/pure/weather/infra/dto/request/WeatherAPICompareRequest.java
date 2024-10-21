package com.pure.weather.infra.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAPICompareRequest {
  private String serviceKey;
  private int pageNo;
  private int numOfRows;
  private String dataType;
  private String dataCd;
  private String dateCd;
  private String startDt;
  private String endDt;
  private String stnIds;

  public static WeatherAPICompareRequest of(
      String serviceKey, int pageNo, int numOfRows, String startDt, String endDt, String stnIds) {
    return new WeatherAPICompareRequest(
        serviceKey, pageNo, numOfRows, "JSON", "ASOS", "DAY", startDt, endDt, stnIds);
  }
}
