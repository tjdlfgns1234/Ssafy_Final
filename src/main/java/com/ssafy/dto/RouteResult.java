package com.ssafy.dto;

import java.util.List;

public class RouteResult {
	List<Integer> route;
    long duration;
    public RouteResult(List<Integer> route, long duration) {
        this.route = route;
        this.duration = duration;
    }
    public List<Integer> getRoute() { return route; }
    public long getDuration() { return duration; }
}
