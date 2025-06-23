package com.ssafy.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.dto.TravelPlanDTO;

@Mapper
public interface TravelPlanRepository {
    /** 여행 계획 등록 */
    int insertTravelPlan(TravelPlanDTO plan) throws Exception;

    /** 전체 여행 계획 조회 */
    List<TravelPlanDTO> selectAllTravelPlans() throws Exception;

    /** 특정 사용자(userId)의 여행 계획 조회 */
    List<TravelPlanDTO> selectTravelPlansByUser(@Param("userId") int userId) throws Exception;

    /** 단일 여행 계획 조회 */
    TravelPlanDTO selectTravelPlan(@Param("planId") int planId) throws Exception;

    /** 여행 계획 수정 */
    int updateTravelPlan(TravelPlanDTO plan) throws Exception;

    /** 여행 계획 삭제 */
    int deleteTravelPlan(@Param("planId") int planId) throws Exception;
}
