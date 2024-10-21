package com.pure.weather.web;

import com.pure.weather.application.WeatherService;
import com.pure.weather.application.dto.response.CompareTodayResponse;
import com.pure.weather.application.dto.response.CompareWithLastYearResponse;
import com.pure.weather.enums.SearchType;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {
  private final WeatherService weatherService;

  @GetMapping("/yesterday")
  public ResponseEntity<CompareTodayResponse> compareTodayAndYesterday(
      @RequestParam("city") String city) {
    weatherService.collectAndSaveTodayTempData(city);
    weatherService.collectAndSaveYesterdayTempData(city);

    return ResponseEntity.ok(weatherService.compareTodayAndYesterdayTemp(city));
  }

  @GetMapping("/last-year-week")
  public ResponseEntity<CompareWithLastYearResponse> compareTodayAndLastYearWeek(
      @RequestParam("city") String city) {
    weatherService.collectAndSaveTodayTempData(city);
    weatherService.collectAndSaveLastYearTempData(SearchType.WEEK, city, LocalDate.now());

    return ResponseEntity.ok(weatherService.compareWithLastYear(SearchType.WEEK, city));
  }

  @GetMapping("/last-year-month")
  public ResponseEntity<CompareWithLastYearResponse> compareTodayAndLastYearMonth(
      @RequestParam("city") String city) {
    weatherService.collectAndSaveTodayTempData(city);
    weatherService.collectAndSaveLastYearTempData(SearchType.MONTH, city, LocalDate.now());

    return ResponseEntity.ok(weatherService.compareWithLastYear(SearchType.MONTH, city));
  }
}
