package com.ssafy.service;

import com.ssafy.dto.MetricsDTO;

public interface MetricService {
 /**
  * metrics 뷰에서 집계된 전체 카운트 정보를 가져옵니다.
  * @return MetricsDTO 객체
  */
	MetricsDTO getMetrics();
}
