package com.ssafy.dto;

import java.util.List;
import java.util.Map;

public class OptimizationRequest {
	
	
	Map<String, DayLocation> dayLocations;
    List<Destination> destinations;
    
    
	public OptimizationRequest(Map<String, DayLocation> dayLocations, List<Destination> destinations) {
		super();
		this.dayLocations = dayLocations;
		this.destinations = destinations;
	}
	public Map<String, DayLocation> getDayLocations() {
		return dayLocations;
	}
	public void setDayLocations(Map<String, DayLocation> dayLocations) {
		this.dayLocations = dayLocations;
	}
	public List<Destination> getDestinations() {
		return destinations;
	}
	public void setDestinations(List<Destination> destinations) {
		this.destinations = destinations;
	}
    
    
}
