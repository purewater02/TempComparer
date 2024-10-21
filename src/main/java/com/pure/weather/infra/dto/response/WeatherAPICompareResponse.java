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
public class WeatherAPICompareResponse {

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
    private String stnId;
    private String stnNm;
    private String tm; // 날짜
    private String avgTa; // 평균 기온
    private String minTa; // 최저 기온
    private String maxTa; // 최고 기온
  }

  public static List<Item> getItems(final WeatherAPICompareResponse response) {
    return Optional.ofNullable(response)
        .map(WeatherAPICompareResponse::getResponse)
        .map(Response::getBody)
        .map(Body::getItems)
        .map(Items::getItem)
        .filter(item -> !item.isEmpty())
        .orElse(Collections.emptyList());
  }
}
