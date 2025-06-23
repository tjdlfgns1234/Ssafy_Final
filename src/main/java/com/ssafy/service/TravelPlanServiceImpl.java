package com.ssafy.service;

import com.ssafy.dto.TravelPlanDTO;
import com.ssafy.repository.TravelPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 여행 계획 서비스 구현체
 */
@Service
public class TravelPlanServiceImpl implements TravelPlanService {
    private final TravelPlanRepository travelPlanRepository;

    public TravelPlanServiceImpl(TravelPlanRepository travelPlanRepository) {
        this.travelPlanRepository = travelPlanRepository;
    }

    @Override
    @Transactional
    public void createTravelPlan(TravelPlanDTO plan) throws Exception {
        travelPlanRepository.insertTravelPlan(plan);
    }

    @Override
    public List<TravelPlanDTO> getAllTravelPlans() throws Exception {
        return travelPlanRepository.selectAllTravelPlans();
    }

    @Override
    public List<TravelPlanDTO> getTravelPlansByUser(int userId) throws Exception {
        return travelPlanRepository.selectTravelPlansByUser(userId);
    }

    @Override
    public TravelPlanDTO getTravelPlanById(int planId) throws Exception {
        return travelPlanRepository.selectTravelPlan(planId);
    }

    @Override
    @Transactional
    public void updateTravelPlan(TravelPlanDTO plan) throws Exception {
        travelPlanRepository.updateTravelPlan(plan);
    }

    @Override
    @Transactional
    public void deleteTravelPlan(int planId) throws Exception {
        travelPlanRepository.deleteTravelPlan(planId);
    }
}