package com.pure.weather.infra;

import com.pure.weather.infra.dto.request.WeatherAPICompareRequest;
import com.pure.weather.infra.dto.response.WeatherAPICompareResponse;
import com.pure.weather.infra.dto.response.WeatherAPITodayResposne;
import com.pure.weather.infra.feignConfig.WeatherFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "weatherClient",
    url = "http://apis.data.go.kr",
    configuration = WeatherFeignConfig.class)
public interface WeatherFeignClient {
  @GetMapping(value = "/1360000/AsosDalyInfoService/getWthrDataList", produces = "application/json")
  WeatherAPICompareResponse getCompareInfo(
      @SpringQueryMap WeatherAPICompareRequest weatherAPICompareRequest);

  @GetMapping(
      value = "/1360000/VilageFcstInfoService_2.0/getVilageFcst",
      produces = "application/json")
  WeatherAPITodayResposne getTodayInfo(
      @RequestParam("ServiceKey") String serviceKey,
      @RequestParam("pageNo") int pageNo,
      @RequestParam("numOfRows") int numOfRows,
      @RequestParam("dataType") String dataType,
      @RequestParam("base_date") String baseDate,
      @RequestParam("base_time") String baseTime,
      @RequestParam("nx") int nx,
      @RequestParam("ny") int ny);
}
