package com.ssafy.dto;

import java.util.List;


public class RouteResponse {
    private int day;
    private String start;
    private String end;
    private long duration;
    private List<VisitInfo> visits;
	public RouteResponse(int day, String start, String end, long duration, List<VisitInfo> visits) {
		super();
		this.day = day;
		this.start = start;
		this.end = end;
		this.duration = duration;
		this.visits = visits;
	}
	
	@Override
	public String toString() {
		return "RouteResponse [day=" + day + ", start=" + start + ", end=" + end + ", duration=" + duration
				+ ", visits=" + visits + "]";
	}

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public List<VisitInfo> getVisits() {
		return visits;
	}
	public void setVisits(List<VisitInfo> visits) {
		this.visits = visits;
	}
    
}
