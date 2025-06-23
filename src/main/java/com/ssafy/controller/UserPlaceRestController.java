// src/main/java/com/ssafy/controller/UserPlaceRestController.java
package com.ssafy.controller;

import com.ssafy.dto.UserPlaceDTO;
import com.ssafy.service.UserPlaceService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST 방식의 UserPlace API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/userplaces")
public class UserPlaceRestController {

    private final UserPlaceService userPlaceService;

    public UserPlaceRestController(UserPlaceService userPlaceService) {
        this.userPlaceService = userPlaceService;
    }

    /** 특정 사용자(userId)의 장소 전체 조회 (GET /api/v1/userplaces/user/{userId}) */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPlacesByUser(@PathVariable int userId) {
        try {
            List<UserPlaceDTO> places = userPlaceService.getPlacesByUser(userId);
            return ResponseEntity.ok(places);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 단일 장소 조회 (GET /api/v1/userplaces/{placeId}) */
    @GetMapping("/{placeId}")
    public ResponseEntity<?> getPlace(@PathVariable int placeId) {
        try {
            UserPlaceDTO place = userPlaceService.getPlaceById(placeId);
            if (place == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "FAIL", "error", "Place not found"));
            }
            return ResponseEntity.ok(place);
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 장소 등록 (POST /api/v1/userplaces) */
    @PostMapping
    public ResponseEntity<?> createPlace(@RequestBody UserPlaceDTO payload) {
        try {
            userPlaceService.createUserPlace(payload);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of("status", "SUCCESS", "place", payload));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }

    /** 장소 수정 (PUT /api/v1/userplaces/{placeId}) */
    @PutMapping("/{placeId}")
    public ResponseEntity<?> updatePlace(@PathVariable int placeId,
                                         @RequestBody UserPlaceDTO payload) {
        try {
            payload.setPlaceId(placeId);
            userPlaceService.updateUserPlace(payload);
            UserPlaceDTO updated = userPlaceService.getPlaceById(placeId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "place", updated));
        } catch (DataAccessException dae) {
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

    /** 장소 삭제 (DELETE /api/v1/userplaces/{placeId}) */
    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable int placeId) {
        try {
            userPlaceService.deleteUserPlace(placeId);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAIL", "error", e.getMessage()));
        }
    }
}
