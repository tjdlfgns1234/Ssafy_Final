package com.ssafy.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.ai.RouteRecommendationService;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.core.util.Json;

@RestController
@RequestMapping("/api/v1/route")
public class RouteRestController {

    @Autowired
    private RouteRecommendationService routeService;
    
    @Timed(
    	    value = "route.recommend",
    	    description = "AI 기반 경로 추천 시 걸린 시간",
    	    histogram = true,
    	    percentiles = {0.5, 0.95}
    	)
    @PostMapping("/recommend")
    public ResponseEntity<Object> recommendRoute(@RequestBody String routeJson) {
    	ObjectMapper objectMapper = new ObjectMapper();
        try {
			JsonNode routeJsonNode = objectMapper.readTree(routeService.recommendRoute(routeJson));
			
			return ResponseEntity.ok(routeJsonNode);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(400).body(Map.of("error", "Invalid JSON"));
		}
    }

}