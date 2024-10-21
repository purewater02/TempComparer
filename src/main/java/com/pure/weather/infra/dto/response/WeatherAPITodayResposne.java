package com.pure.weather.infra.dto.response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAPITodayResposne {
  private Response response;

  @Data
  public static class Response {
    private Header header;
    private Body body;
  }

  @Data
  public static class Header {
    private String resultCode;
    private String resultMsg;
  }

  @Data
  public static class Body {
    private String dataType;
    private Items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
  }

  @Data
  public static class Items {
    private List<Item> item;
  }

  @Data
  public static class Item {
    private String baseDate; // 발표 일자
    private String baseTime; // 발표 시각
    private String category; // 자료 구분 코드
    private String fcstDate; // 예보 일자
    private String fcstTime; // 예보 시각
    private String fcstValue; // 예보 값
    private int nx; // X 좌표
    private int ny; // Y 좌표
  }

  public static List<Item> getItems(final WeatherAPITodayResposne response) {
    return Optional.ofNullable(response)
        .map(WeatherAPITodayResposne::getResponse)
        .map(Response::getBody)
        .map(Body::getItems)
        .map(Items::getItem)
        .filter(item -> !item.isEmpty())
        .orElse(Collections.emptyList());
  }
}
