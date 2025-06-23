package com.ssafy.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.dto.OptimizationRequest;
import com.ssafy.dto.RouteResponse;
import com.ssafy.service.RouteOptimizerService;

@RestController
@RequestMapping("/api/v1/optimize")
public class RouteOptimizationController {
    private final RouteOptimizerService optimizerService;
    public RouteOptimizationController(RouteOptimizerService optimizerService) {
        this.optimizerService = optimizerService;
    }

 

	@PostMapping("/optimize-route")
    public ResponseEntity<Map<String, List<RouteResponse>>> optimize(@RequestBody OptimizationRequest request) {
        List<RouteResponse> result = optimizerService.optimize(request);
        System.out.println(result);
        return ResponseEntity.ok(Map.of("dayRoutes", result));
    }
}
