package com.ssafy.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ssafy.dto.MetricsDTO;

@Mapper
public interface MetricsRepository {

    /**
     * metrics 뷰의 모든 컬럼을 MetricsDTO에 매핑하여 반환한다.
     */
    MetricsDTO fetchMetrics();
}
