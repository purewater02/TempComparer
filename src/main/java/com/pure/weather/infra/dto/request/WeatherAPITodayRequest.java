package com.pure.weather.infra.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAPITodayRequest {
  @JsonProperty("ServiceKey")
  private String ServiceKey;

  private int pageNo;
  private int numOfRows;
  private String dataType;

  @JsonProperty("base_date")
  private String base_date;

  @JsonProperty("base_time")
  private String base_time;

  private int nx; // int로 요청 안하고 소수점으로 보내면 에러
  private int ny;

  public static WeatherAPITodayRequest of(
      String ServiceKey, int pageNo, int numOfRows, String base_date, int nx, int ny) {
    return new WeatherAPITodayRequest(
        ServiceKey,
        pageNo,
        numOfRows,
        "JSON",
        base_date,
        "0200", // 0200시 발표 활용하면 모두 처리 가능
        nx,
        ny);
  }
}
