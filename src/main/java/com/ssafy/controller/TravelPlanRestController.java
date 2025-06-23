package com.ssafy.controller;

import com.ssafy.dto.TravelPlanDTO;
import com.ssafy.service.TravelPlanService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST 방식의 여행 계획 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/travelplans")
public class TravelPlanRestController {
    private final TravelPlanService travelPlanService;

    public TravelPlanRestController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    /** 전체 여행 계획 조회 (GET /api/v1/travelplans) */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<TravelPlanDTO> plans = travelPlanService.getAllTravelPlans();
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 사용자별 여행 계획 조회 (GET /api/v1/travelplans/user/{userId}) */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable int userId) {
        try {
            List<TravelPlanDTO> plans = travelPlanService.getTravelPlansByUser(userId);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 단일 여행 계획 조회 (GET /api/v1/travelplans/{planId}) */
    @GetMapping("/{planId}")
    public ResponseEntity<?> getOne(@PathVariable int planId) {
        try {
            TravelPlanDTO plan = travelPlanService.getTravelPlanById(planId);
            if (plan == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "FAIL", "error", "Plan not found"));
            }
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 여행 계획 등록 (POST /api/v1/travelplans) */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody TravelPlanDTO payload) {
        try {
            travelPlanService.createTravelPlan(payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("status", "SUCCESS", "plan", payload));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 여행 계획 수정 (PUT /api/v1/travelplans/{planId}) */
    @PutMapping("/{planId}")
    public ResponseEntity<?> update(@PathVariable int planId,
                                    @RequestBody TravelPlanDTO payload) {
        try {
            payload.setPlanId(planId);
            travelPlanService.updateTravelPlan(payload);
            TravelPlanDTO updated = travelPlanService.getTravelPlanById(planId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "plan", updated));
        } catch (DataAccessException dae) {
        	dae.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", dae.getMessage()));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 여행 계획 삭제 (DELETE /api/v1/travelplans/{planId}) */
    @DeleteMapping("/{planId}")
    public ResponseEntity<?> delete(@PathVariable int planId) {
        try {
            travelPlanService.deleteTravelPlan(planId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }
}