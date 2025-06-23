package com.ssafy.service;

import com.ssafy.dto.UserPlaceDTO;
import com.ssafy.repository.UserPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 정의 장소 서비스 구현체
 */
@Service
public class UserPlaceServiceImpl implements UserPlaceService {
    private final UserPlaceRepository userPlaceRepository;

    public UserPlaceServiceImpl(UserPlaceRepository userPlaceRepository) {
        this.userPlaceRepository = userPlaceRepository;
    }

    @Override
    @Transactional
    public void createUserPlace(UserPlaceDTO place) throws Exception {
        userPlaceRepository.UserPlaceInsert(place);
    }

    @Override
    public List<UserPlaceDTO> getPlacesByUser(int userId) throws Exception {
        return userPlaceRepository.UserPlaceSelectAllByUser(userId);
    }

    @Override
    public UserPlaceDTO getPlaceById(int placeId) throws Exception {
        return userPlaceRepository.UserPlaceSelect(placeId);
    }

    @Override
    @Transactional
    public void updateUserPlace(UserPlaceDTO place) throws Exception {
        userPlaceRepository.UserPlaceUpdate(place);
    }

    @Override
    @Transactional
    public void deleteUserPlace(int placeId) throws Exception {
        userPlaceRepository.UserPlaceDelete(placeId);
    }
}