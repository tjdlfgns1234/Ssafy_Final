package com.ssafy.util;

import com.google.maps.GeoApiContext;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class GoogleDistanceMatrixClient {

    private static final Logger logger = LoggerFactory.getLogger(GoogleDistanceMatrixClient.class);
    private static final long INF = Long.MAX_VALUE;
    private static final String API_KEY = ""; // 실제 API 키 사용

    private final GeoApiContext context;

    public GoogleDistanceMatrixClient() {
        this.context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
    }

    /**
     * coords 리스트 크기 n×n 의 시간 매트릭스를 반환합니다.
     * 큰 행렬을 여러 번의 작은 호출로 나누어 처리합니다.
     */
    public long[][] getTimeMatrix(List<LatLng> coordinates) {
        int size = coordinates.size();
        long[][] matrix = new long[size][size];

        int chunkSize = 5; // 한 번에 요청할 크기 (예: 5x5)
        int totalChunks = (int) Math.ceil((double) size / chunkSize);  // 전체 매트릭스를 쪼갤 수 있는 조각의 수

        for (int chunk = 0; chunk < totalChunks; chunk++) {
            int startIndex = chunk * chunkSize;
            int endIndex = Math.min((chunk + 1) * chunkSize, size);

            // 1) 현재 chunk에 해당하는 시작 좌표와 종료 좌표 설정
            List<LatLng> chunkCoordinates = coordinates.subList(startIndex, endIndex);
            String[] origins = chunkCoordinates.stream()
                .map(LatLng::toUrlValue)
                .toArray(String[]::new);
            String[] destinations = coordinates.stream()
                .map(LatLng::toUrlValue)
                .toArray(String[]::new);

            try {
                // 2) Google DistanceMatrix API에 요청을 보냄
                DistanceMatrix dm = DistanceMatrixApi.newRequest(context)
                    .origins(origins)
                    .destinations(destinations)
                    .mode(TravelMode.TRANSIT)
                    .units(Unit.METRIC)
                    .await();

                // 3) 결과 파싱 및 matrix에 채우기
                DistanceMatrixRow[] rows = dm.rows;
                for (int i = 0; i < chunkCoordinates.size(); i++) {
                    DistanceMatrixElement[] elements = rows[i].elements;
                    for (int j = 0; j < size; j++) {
                        DistanceMatrixElement e = elements[j];
                        if (e.status == DistanceMatrixElementStatus.OK) {
                            matrix[startIndex + i][j] = e.duration.inSeconds;
                        } else {
                            matrix[startIndex + i][j] = INF;  // 실패 시 INF로 처리
                        }
                    }
                }
            } catch (ApiException | InterruptedException | IOException ex) {
                // 실패 시 해당 범위는 INF로 처리
                for (int i = 0; i < chunkCoordinates.size(); i++) {
                    Arrays.fill(matrix[startIndex + i], INF);
                }
            }
        }
        
        return matrix;
    }

}
