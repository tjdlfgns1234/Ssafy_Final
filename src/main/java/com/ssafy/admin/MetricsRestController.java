package com.ssafy.admin;
import com.ssafy.dto.MetricsDTO;
import com.ssafy.service.MetricService;
import com.ssafy.service.MetricServiceImpl;
import com.sun.management.OperatingSystemMXBean;  // Sun 전용 확장 API

import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.MeterRegistry;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsRestController {

    private final MeterRegistry registry;
    private final MetricService metricService;

    public MetricsRestController(MeterRegistry registry,
                                 MetricService metricService) {
        this.registry     = registry;
        this.metricService = metricService;
    }

    @GetMapping("/all")
    public MetricsDTO getDbMetrics() {
        return metricService.getMetrics();
    }

    
    @GetMapping("/apis")
    public Map<String, Map<String, Long>> apiMetrics() {
        // 1) 타이머 조회하거나, 없으면 새 타이머 생성
        Timer postsTimer = registry.find("posts.listAll").timer();
        if (postsTimer == null) {
            postsTimer = Timer.builder("posts.listAll")
                              .publishPercentiles(0.5, 0.95)
                              .publishPercentileHistogram()
                              .register(registry);
        }

        Timer membersTimer = registry.find("members.getOne").timer();
        if (membersTimer == null) {
            membersTimer = Timer.builder("members.getOne")
                                .publishPercentiles(0.5, 0.95)
                                .publishPercentileHistogram()
                                .register(registry);
        }

        Timer commentsTimer = registry.find("comments.getByPost").timer();
        if (commentsTimer == null) {
            commentsTimer = Timer.builder("comments.getByPost")
                                 .publishPercentiles(0.5, 0.95)
                                 .publishPercentileHistogram()
                                 .register(registry);
        }

        // 추가: AI 기반 경로 추천 타이머
        Timer routeTimer = registry.find("route.recommend").timer();
        if (routeTimer == null) {
            routeTimer = Timer.builder("route.recommend")
                              .publishPercentiles(0.5, 0.95)
                              .publishPercentileHistogram()
                              .register(registry);
        }

        // 2) Snapshot
        HistogramSnapshot postsSnap    = postsTimer.takeSnapshot();
        HistogramSnapshot membersSnap  = membersTimer.takeSnapshot();
        HistogramSnapshot commentsSnap = commentsTimer.takeSnapshot();
        HistogramSnapshot routeSnap    = routeTimer.takeSnapshot();

        // 3) p50/p95 계산 (밀리초 단위)
        long postsP50    = Math.round(postsSnap.percentileValues()[0].value(TimeUnit.MILLISECONDS));
        long postsP95    = Math.round(postsSnap.percentileValues()[1].value(TimeUnit.MILLISECONDS));

        long membersP50  = Math.round(membersSnap.percentileValues()[0].value(TimeUnit.MILLISECONDS));
        long membersP95  = Math.round(membersSnap.percentileValues()[1].value(TimeUnit.MILLISECONDS));

        long commentsP50 = Math.round(commentsSnap.percentileValues()[0].value(TimeUnit.MILLISECONDS));
        long commentsP95 = Math.round(commentsSnap.percentileValues()[1].value(TimeUnit.MILLISECONDS));

        long routeP50    = Math.round(routeSnap.percentileValues()[0].value(TimeUnit.MILLISECONDS));
        long routeP95    = Math.round(routeSnap.percentileValues()[1].value(TimeUnit.MILLISECONDS));

        // 4) JSON 반환
        return Map.of(
            "posts",    Map.of("p50", postsP50,    "p95", postsP95),
            "members",  Map.of("p50", membersP50,  "p95", membersP95),
            "comments", Map.of("p50", commentsP50, "p95", commentsP95),
            "route",    Map.of("p50", routeP50,    "p95", routeP95)
        );
    }

    @GetMapping("/server")
    public Map<String, Object> serverMetrics() {
        // 1) OS MXBean을 Sun 확장 타입으로 캐스팅
        OperatingSystemMXBean os =
          (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // 2) CPU load: getCpuLoad() → 0.0~1.0 (퍼센트로 변환)
        double cpuLoad  = os.getCpuLoad() * 100;  

        // 3) 물리 메모리 사용량(퍼센트)
        long totalPhys = os.getTotalMemorySize();
        long freePhys  = os.getFreeMemorySize();
        double ramUsage = ((double)(totalPhys - freePhys) / totalPhys) * 100;

        // 4) Disk I/O 등은 필요시 OSHI 등의 라이브러리로 추가
        double diskIo = 0.0;

        return Map.of(
            "cpu",  Math.round(cpuLoad),
            "ram",  Math.round(ramUsage),
            "disk", Math.round(diskIo)
        );
    }
}

