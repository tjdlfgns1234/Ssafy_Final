package com.ssafy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 3) UserPlaceDTO
public class UserPlaceDTO {
    private Integer placeId;
    private Integer userId;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime created;

    public UserPlaceDTO() {}

    public UserPlaceDTO(Integer placeId, Integer userId, String name,
                        BigDecimal latitude, BigDecimal longitude, LocalDateTime created) {
        this.placeId = placeId;
        this.userId = userId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created = created;
    }

    public Integer getPlaceId() { return placeId; }
    public void setPlaceId(Integer placeId) { this.placeId = placeId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
}
