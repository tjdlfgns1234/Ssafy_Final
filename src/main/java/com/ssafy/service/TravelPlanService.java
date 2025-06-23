package com.ssafy.service;

import com.ssafy.dto.TravelPlanDTO;
import java.util.List;

/**
 * 여행 계획 서비스 인터페이스
 */
public interface TravelPlanService {
    /** 여행 계획 등록 */
    void createTravelPlan(TravelPlanDTO plan) throws Exception;

    /** 전체 여행 계획 조회 */
    List<TravelPlanDTO> getAllTravelPlans() throws Exception;

    /** 특정 사용자(userId)의 여행 계획 조회 */
    List<TravelPlanDTO> getTravelPlansByUser(int userId) throws Exception;

    /** 단일 여행 계획 조회 */
    TravelPlanDTO getTravelPlanById(int planId) throws Exception;

    /** 여행 계획 수정 */
    void updateTravelPlan(TravelPlanDTO plan) throws Exception;

    /** 여행 계획 삭제 */
    void deleteTravelPlan(int planId) throws Exception;
}