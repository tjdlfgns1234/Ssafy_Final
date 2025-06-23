package com.ssafy.service;

import org.springframework.stereotype.Service;

import com.ssafy.dto.MetricsDTO;
import com.ssafy.repository.MetricsRepository;

@Service
public class MetricServiceImpl implements MetricService {

    private final MetricsRepository metricsRepository;

    public MetricServiceImpl(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    @Override
    public MetricsDTO getMetrics() {
        return metricsRepository.fetchMetrics();
    }
}
