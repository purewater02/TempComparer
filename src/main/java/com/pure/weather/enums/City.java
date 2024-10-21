package com.pure.weather.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum City {
  // 일부 지역만 사용
  SEOUL("108"),
  BUSAN("159"),
  DAEGU("143"),
  INCHEON("112"),
  GWANGJU("156"),
  DAEJEON("133"),
  ULSAN("152"),
  SEJONG("131"),
  GYEONGGI("119"),
  GANGWON("105"),
  CHEONGJU("131"),
  JEONJU("146"),
  JEJU("184");

  private final String stnId;
}
