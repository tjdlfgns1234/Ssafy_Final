package com.ssafy.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonRawValue;

// 2) TravelPlanDTO
public class TravelPlanDTO {
    private Integer planId;
    private Integer userId;
    private String description;
    @JsonRawValue
    private String routes;
    private LocalDateTime start_day;
    private LocalDateTime end_day;

    public TravelPlanDTO() {}

    public TravelPlanDTO(Integer planId, Integer userId, String description,
    				String routes, LocalDateTime start_day, LocalDateTime end_day) {
        this.planId = planId;
        this.userId = userId;
        this.description = description;
        this.routes = routes;
        this.start_day = start_day;
        this.end_day = end_day;
    }

    public Integer getPlanId() { return planId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRoutes() { return routes; }
    public void setRoutes(String routes) { this.routes = routes; }
	public LocalDateTime getStart_day() {
		return start_day;
	}

	public void setStart_day(LocalDateTime start_day) {
		this.start_day = start_day;
	}

	public LocalDateTime getEnd_day() {
		return end_day;
	}

	public void setEnd_day(LocalDateTime end_day) {
		this.end_day = end_day;
	}
    
}
