package com.ssafy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Destination {
	private long id;
    private int day;
    private int order;
    private String title;
    private String address;
    private int duration;
    private double latitude;
    private double longitude;
    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("content_id")
    private Object contentId;
    private String description;
    private String displayName;
    

    @Override
	public String toString() {
		return "Destination [id=" + id + ", day=" + day + ", order=" + order + ", title=" + title + ", address="
				+ address + ", duration=" + duration + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", placeId=" + placeId + ", contentId=" + contentId + ", description=" + description
				+ ", displayName=" + displayName + "]";
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public int getDay() {
		return day;
	}


	public void setDay(int day) {
		this.day = day;
	}


	public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		this.order = order;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public String getPlaceId() {
		return placeId;
	}


	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}


	public Object getContentId() {
		return contentId;
	}


	public void setContentId(Object contentId) {
		this.contentId = contentId;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getKey() {
        if (placeId != null && !placeId.isEmpty()) return placeId;
        return contentId != null ? contentId.toString() : null;
    }

}