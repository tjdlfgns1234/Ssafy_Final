package com.ssafy.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.dto.UserPlaceDTO;

@Mapper
public interface UserPlaceRepository {
    /** 사용자 정의 장소 등록 */
    int UserPlaceInsert(UserPlaceDTO place) throws Exception;

    /** 특정 사용자(userId)의 모든 장소 조회 */
    List<UserPlaceDTO> UserPlaceSelectAllByUser(@Param("userId") int userId) throws Exception;

    /** 단일 장소 조회 */
    UserPlaceDTO UserPlaceSelect(@Param("placeId") int placeId) throws Exception;

    /** 장소 정보 수정 */
    int UserPlaceUpdate(UserPlaceDTO place) throws Exception;

    /** 장소 삭제 */
    int UserPlaceDelete(@Param("placeId") int placeId) throws Exception;
}
