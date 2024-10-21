package com.pure.weather.application;

import com.pure.weather.application.dto.response.CompareTodayResponse;
import com.pure.weather.application.dto.response.CompareWithLastYearMonthResponse;
import com.pure.weather.application.dto.response.CompareWithLastYearWeekResponse;
import com.pure.weather.domain.Temperature;
import com.pure.weather.enums.City;
import com.pure.weather.enums.Coord;
import com.pure.weather.enums.SearchType;
import com.pure.weather.infra.WeatherFeignClient;
import com.pure.weather.infra.dto.request.WeatherAPICompareRequest;
import com.pure.weather.infra.dto.response.WeatherAPICompareResponse;
import com.pure.weather.infra.dto.response.WeatherAPITodayResposne;
import com.pure.weather.repository.TemperatureRepository;
import com.pure.weather.util.DateCalculator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WeatherService {
  @Value("${weather.api.key}")
  private String serviceKey;

  private final WeatherFeignClient weatherFeignClient;
  private final TemperatureRepository temperatureRepository;

  @Transactional
  public void collectAndSaveLastYearTempData(SearchType type, String city, LocalDate date) {
    String start = null;
    String end = null;
    if (type.equals(SearchType.WEEK)) {
      String[] lastYearWeekRange = DateCalculator.getLastYearWeekRange(date);
      start = lastYearWeekRange[0];
      end = lastYearWeekRange[1];
    } else if (type.equals(SearchType.MONTH)) {
      String[] lastYearMonthRange = DateCalculator.getLastYearMonthRange(date);
      start = lastYearMonthRange[0];
      end = lastYearMonthRange[1];
    }

    WeatherAPICompareResponse response =
        weatherFeignClient.getCompareInfo(
            WeatherAPICompareRequest.of(
                serviceKey, 1, 31, start, end, City.valueOf(city).getStnId()));

    if (response != null
        && response.getResponse() != null
        && response.getResponse().getBody() != null) {
      response
          .getResponse()
          .getBody()
          .getItems()
          .getItem()
          .forEach(
              item -> {
                boolean exists =
                    temperatureRepository.existsByDateAndCity(
                        LocalDate.parse(item.getTm()), City.valueOf(city));
                if (!exists) {
                  temperatureRepository.save(
                      Temperature.of(
                          City.valueOf(city),
                          LocalDate.parse(item.getTm()),
                          Double.valueOf(item.getAvgTa()),
                          Double.valueOf(item.getMaxTa()),
                          Double.valueOf(item.getMinTa())));
                }
              });
    }
  }

  @Transactional
  public void collectAndSaveTodayTempData(String city) {
    LocalDate today = LocalDate.now();
    WeatherAPITodayResposne response =
        weatherFeignClient.getTodayInfo(
            serviceKey,
            1,
            1000,
            "JSON",
            DateCalculator.convertToString(today),
            "0500",
            Coord.valueOf(city).getLat(),
            Coord.valueOf(city).getLon());

    Double avgTemp = null;
    Double maxTemp = null;
    Double minTemp = null;

    if (response != null
        && response.getResponse() != null
        && response.getResponse().getBody() != null) {
      List<WeatherAPITodayResposne.Item> list =
          response.getResponse().getBody().getItems().getItem();

      for (WeatherAPITodayResposne.Item item : list) {
        if (item.getCategory().equals("TMP")) {
          avgTemp = Double.valueOf(item.getFcstValue());
        } else if (item.getCategory().equals("TMX")) {
          maxTemp = Double.valueOf(item.getFcstValue());
        } else if (item.getCategory().equals("TMN")) {
          minTemp = Double.valueOf(item.getFcstValue());
        }
      }

      Double finalAvgTemp = avgTemp;
      Double finalMaxTemp = maxTemp;
      Double finalMinTemp = minTemp;

      temperatureRepository
          .findByDateAndCity(today, City.valueOf(city))
          .ifPresentOrElse(
              temperature -> {
                log.info("collectAndSaveTodayTempData: update temperature data");
                temperature.update(finalAvgTemp, finalMaxTemp, finalMinTemp);
              },
              () -> {
                log.info("collectAndSaveTodayTempData: save data");
                temperatureRepository.save(
                    Temperature.of(
                        City.valueOf(city), today, finalAvgTemp, finalMaxTemp, finalMinTemp));
              });
    }
  }

  @Transactional
  public void collectAndSaveYesterdayTempData(String city) {
    LocalDate yesterday = LocalDate.now().minusDays(1);
    WeatherAPITodayResposne response =
        weatherFeignClient.getTodayInfo(
            serviceKey,
            1,
            1000,
            "JSON",
            DateCalculator.convertToString(yesterday),
            "0500",
            Coord.valueOf(city).getLat(),
            Coord.valueOf(city).getLon());

    Double avgTemp = null;
    Double maxTemp = null;
    Double minTemp = null;

    if (response != null
        && response.getResponse() != null
        && response.getResponse().getBody() != null) {
      List<WeatherAPITodayResposne.Item> list =
          response.getResponse().getBody().getItems().getItem();

      for (WeatherAPITodayResposne.Item item : list) {
        if (item.getCategory().equals("TMP")) {
          avgTemp = Double.valueOf(item.getFcstValue());
        } else if (item.getCategory().equals("TMX")) {
          maxTemp = Double.valueOf(item.getFcstValue());
        } else if (item.getCategory().equals("TMN")) {
          minTemp = Double.valueOf(item.getFcstValue());
        }
      }

      Double finalAvgTemp = avgTemp;
      Double finalMaxTemp = maxTemp;
      Double finalMinTemp = minTemp;

      temperatureRepository
          .findByDateAndCity(yesterday, City.valueOf(city))
          .ifPresentOrElse(
              temperature -> {
                log.info("collectAndSaveYesterdayTempData: update temperature data");
                temperature.update(finalAvgTemp, finalMaxTemp, finalMinTemp);
              },
              () -> {
                log.info("collectAndSaveYesterdayTempData: save data");
                temperatureRepository.save(
                    Temperature.of(
                        City.valueOf(city), yesterday, finalAvgTemp, finalMaxTemp, finalMinTemp));
              });
    }
  }

  public CompareTodayResponse compareTodayAndYesterdayTemp(String city) {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
    Temperature todayTemp =
        temperatureRepository
            .findByDateAndCity(today, City.valueOf(city))
            .orElseThrow(() -> new IllegalArgumentException("no result for today"));
    Temperature yesterdayTemp =
        temperatureRepository
            .findByDateAndCity(yesterday, City.valueOf(city))
            .orElseThrow(() -> new IllegalArgumentException("no result for yesterday"));
    return CompareTodayResponse.of(
        todayTemp.getAverageTemp(),
        todayTemp.getMaxTemp(),
        todayTemp.getMinTemp(),
        yesterdayTemp.getAverageTemp(),
        yesterdayTemp.getMaxTemp(),
        yesterdayTemp.getMinTemp());
  }

  public CompareWithLastYearWeekResponse compareWithLastYearWeek(String city) {
    LocalDate today = LocalDate.now();
    String[] lastYearWeekRange = DateCalculator.getLastYearWeekRange(today);
    LocalDate startOfLastYearWeek = DateCalculator.convertToLocalDate(lastYearWeekRange[0]);
    LocalDate endOfLastYearWeek = DateCalculator.convertToLocalDate(lastYearWeekRange[1]);
    List<Temperature> lastYearWeekData =
        temperatureRepository.findByCityAndDateBetween(
            City.valueOf(city), startOfLastYearWeek, endOfLastYearWeek);
    Temperature todayTemp =
        temperatureRepository
            .findByDateAndCity(today, City.valueOf(city))
            .orElseThrow(() -> new IllegalArgumentException("no result for today"));

    if (lastYearWeekData.isEmpty()) {
      throw new IllegalArgumentException("no result for last year week");
    }

    double lastYearWeekAvg =
        lastYearWeekData.stream().mapToDouble(Temperature::getAverageTemp).average().orElse(0);
    double todayAvg = todayTemp.getAverageTemp();

    return CompareWithLastYearWeekResponse.of(todayAvg, lastYearWeekAvg);
  }

  public CompareWithLastYearMonthResponse compareWithLastYearMonth(String city) {
    LocalDate today = LocalDate.now();
    String[] lastYearMonthRange = DateCalculator.getLastYearMonthRange(today);
    LocalDate startOfLastYearMonth = DateCalculator.convertToLocalDate(lastYearMonthRange[0]);
    LocalDate endOfLastYearMonth = DateCalculator.convertToLocalDate(lastYearMonthRange[1]);
    List<Temperature> lastYearMonthData =
        temperatureRepository.findByCityAndDateBetween(
            City.valueOf(city), startOfLastYearMonth, endOfLastYearMonth);
    Temperature todayTemp =
        temperatureRepository
            .findByDateAndCity(today, City.valueOf(city))
            .orElseThrow(() -> new IllegalArgumentException("no result for today"));

    if (lastYearMonthData.isEmpty()) {
      throw new IllegalArgumentException("no result for last year month");
    }

    double lastYearMonthAvg =
        lastYearMonthData.stream().mapToDouble(Temperature::getAverageTemp).average().orElse(0);
    double todayAvg = todayTemp.getAverageTemp();

    return CompareWithLastYearMonthResponse.of(todayAvg, lastYearMonthAvg);
  }
}
