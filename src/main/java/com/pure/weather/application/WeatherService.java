package com.pure.weather.application;

import com.pure.weather.application.dto.response.CompareTodayResponse;
import com.pure.weather.application.dto.response.CompareWithLastYearResponse;
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
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class WeatherService {
  @Value("${weather.api.key}")
  private String serviceKey;

  private final WeatherFeignClient weatherFeignClient;
  private final TemperatureRepository temperatureRepository;

  private Pair<String, String> getStartEndPair(final SearchType type, final LocalDate date) {
    String start = null;
    String end = null;
    if (type.equals(SearchType.WEEK)) {
      String[] weekRange = DateCalculator.getLastYearWeekRange(date);
      start = weekRange[0];
      end = weekRange[1];
    } else if (type.equals(SearchType.MONTH)) {
      String[] monthRange = DateCalculator.getLastYearMonthRange(date);
      start = monthRange[0];
      end = monthRange[1];
    }
    assert start != null;
    assert end != null;
    return Pair.of(start, end);
  }

  @Transactional
  public void collectAndSaveLastYearTempData(SearchType type, String city, LocalDate date) {
    Pair<String, String> startEndPair = getStartEndPair(type, date);
    WeatherAPICompareResponse response =
        weatherFeignClient.getCompareInfo(
            WeatherAPICompareRequest.of(
                serviceKey,
                1,
                31,
                startEndPair.getFirst(),
                startEndPair.getSecond(),
                City.valueOf(city).getStnId()));

    WeatherAPICompareResponse.getItems(response)
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

    AtomicReference<Double> avgTemp = new AtomicReference<>();
    AtomicReference<Double> maxTemp = new AtomicReference<>();
    AtomicReference<Double> minTemp = new AtomicReference<>();

    WeatherAPITodayResposne.getItems(response)
        .forEach(
            item -> {
              switch (item.getCategory()) {
                case "TMP" -> avgTemp.set(Double.valueOf(item.getFcstValue()));
                case "TMX" -> maxTemp.set(Double.valueOf(item.getFcstValue()));
                case "TMN" -> minTemp.set(Double.valueOf(item.getFcstValue()));
              }
            });

    Double finalAvgTemp = avgTemp.get();
    Double finalMaxTemp = maxTemp.get();
    Double finalMinTemp = minTemp.get();

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

    AtomicReference<Double> avgTemp = new AtomicReference<>();
    AtomicReference<Double> maxTemp = new AtomicReference<>();
    AtomicReference<Double> minTemp = new AtomicReference<>();

    WeatherAPITodayResposne.getItems(response)
        .forEach(
            item -> {
              switch (item.getCategory()) {
                case "TMP" -> avgTemp.set(Double.valueOf(item.getFcstValue()));
                case "TMX" -> maxTemp.set(Double.valueOf(item.getFcstValue()));
                case "TMN" -> minTemp.set(Double.valueOf(item.getFcstValue()));
              }
            });

    Double finalAvgTemp = avgTemp.get();
    Double finalMaxTemp = maxTemp.get();
    Double finalMinTemp = minTemp.get();

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

  public CompareWithLastYearResponse compareWithLastYear(SearchType type, String city) {
    LocalDate today = LocalDate.now();
    Pair<String, String> lastYearRange = getStartEndPair(type, today);
    LocalDate startOfLastYear = DateCalculator.convertToLocalDate(lastYearRange.getFirst());
    LocalDate endOfLastYear = DateCalculator.convertToLocalDate(lastYearRange.getSecond());

    List<Temperature> lastYearData =
        temperatureRepository.findByCityAndDateBetween(
            City.valueOf(city), startOfLastYear, endOfLastYear);
    Temperature todayTemp =
        temperatureRepository
            .findByDateAndCity(today, City.valueOf(city))
            .orElseThrow(() -> new IllegalArgumentException("no result for today"));

    if (lastYearData.isEmpty()) {
      throw new IllegalArgumentException("no result for last year week");
    }

    double lastYearAvg =
        lastYearData.stream().mapToDouble(Temperature::getAverageTemp).average().orElse(0);
    double todayAvg = todayTemp.getAverageTemp();

    return CompareWithLastYearResponse.of(type.name(), city, todayAvg, lastYearAvg);
  }
}
