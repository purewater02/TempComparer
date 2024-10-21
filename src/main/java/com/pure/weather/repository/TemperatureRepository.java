package com.pure.weather.repository;

import com.pure.weather.domain.Temperature;
import com.pure.weather.enums.City;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {
  Optional<Temperature> findByDateAndCity(LocalDate date, City city);

  List<Temperature> findByCityAndDateBetween(City city, LocalDate startDate, LocalDate endDate);

  boolean existsByDateAndCity(LocalDate date, City city);
}
