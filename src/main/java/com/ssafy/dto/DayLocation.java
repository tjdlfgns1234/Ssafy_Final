package com.ssafy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.LatLng;

public class DayLocation {
	@JsonProperty("start_place_id")
    private String startPlaceId;

    @JsonProperty("start_content_id")
    private String startContentId;

    @JsonProperty("end_place_id")
    private String endPlaceId;

    @JsonProperty("end_content_id")
    private String endContentId;

    @JsonProperty("startCoordinates")
    private LatLng startCoordinates;

    @JsonProperty("endCoordinates")
    private LatLng endCoordinates;

    @JsonProperty("start_location_title")
    private String startLocationTitle;

    @JsonProperty("end_location_title")
    private String endLocationTitle;

    // getter/setter 생략
    
    public DayLocation(String startPlaceId, String endPlaceId, String startContentId, String endContentId,
			LatLng startCoordinates, LatLng endCoordinates) {
		super();
		this.startPlaceId = startPlaceId;
		this.endPlaceId = endPlaceId;
		this.startContentId = startContentId;
		this.endContentId = endContentId;
		this.startCoordinates = startCoordinates;
		this.endCoordinates = endCoordinates;
	}
    
	@Override
	public String toString() {
		return "DayLocation [startPlaceId=" + startPlaceId + ", endPlaceId=" + endPlaceId + ", startContentId="
				+ startContentId + ", endContentId=" + endContentId + ", startCoordinates=" + startCoordinates
				+ ", endCoordinates=" + endCoordinates + "]";
	}

	public String getStartPlaceId() {
		return startPlaceId;
	}
	
	public void setStartPlaceId(String startPlaceId) {
		this.startPlaceId = startPlaceId;
	}
	public String getEndPlaceId() {
		return endPlaceId;
	}
	public void setEndPlaceId(String endPlaceId) {
		this.endPlaceId = endPlaceId;
	}
	public String getStartContentId() {
		return startContentId;
	}
	public void setStartContentId(String startContentId) {
		this.startContentId = startContentId;
	}
	public String getEndContentId() {
		return endContentId;
	}
	public void setEndContentId(String endContentId) {
		this.endContentId = endContentId;
	}
	public LatLng getStartCoordinates() {
		return startCoordinates;
	}
	public void setStartCoordinates(LatLng startCoordinates) {
		this.startCoordinates = startCoordinates;
	}
	public LatLng getEndCoordinates() {
		return endCoordinates;
	}
	public void setEndCoordinates(LatLng endCoordinates) {
		this.endCoordinates = endCoordinates;
	}
    
    
}
