package com.pure.weather.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Coord {
  SEOUL(37, 127),
  BUSAN(35, 129),
  DAEGU(35, 128),
  INCHEON(37, 126),
  GWANGJU(35, 126),
  DAEJEON(36, 127),
  ULSAN(35, 129),
  SEJONG(36, 127),
  GYEONGGI(37, 127),
  GANGWON(37, 128),
  CHEONGJU(36, 127),
  JEONJU(35, 127),
  JEJU(33, 126);

  private final int lat;
  private final int lon;
}
