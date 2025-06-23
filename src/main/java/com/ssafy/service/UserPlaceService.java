// src/main/java/com/ssafy/service/UserPlaceService.java
package com.ssafy.service;

import com.ssafy.dto.UserPlaceDTO;
import java.util.List;

/**
 * 사용자 정의 장소 서비스 인터페이스
 */
public interface UserPlaceService {
    /** 장소 등록 */
    void createUserPlace(UserPlaceDTO place) throws Exception;

    /** 특정 사용자(userId)의 장소 전체 조회 */
    List<UserPlaceDTO> getPlacesByUser(int userId) throws Exception;

    /** 단일 장소 조회 */
    UserPlaceDTO getPlaceById(int placeId) throws Exception;

    /** 장소 수정 */
    void updateUserPlace(UserPlaceDTO place) throws Exception;

    /** 장소 삭제 */
    void deleteUserPlace(int placeId) throws Exception;
}