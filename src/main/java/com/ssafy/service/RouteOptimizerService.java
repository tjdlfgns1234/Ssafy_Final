package com.ssafy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.maps.model.LatLng;
import com.ssafy.dto.DayLocation;
import com.ssafy.dto.Destination;
import com.ssafy.dto.OptimizationRequest;
import com.ssafy.dto.RouteResponse;
import com.ssafy.dto.RouteResult;
import com.ssafy.dto.VisitInfo;
import com.ssafy.util.GoogleDistanceMatrixClient;
import com.ssafy.util.OrToolsSolver;

@Service
public class RouteOptimizerService {

    private final GoogleDistanceMatrixClient distanceClient;
    private final OrToolsSolver solver;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public RouteOptimizerService(GoogleDistanceMatrixClient distanceClient, OrToolsSolver solver) {
        this.distanceClient = distanceClient;
        this.solver = solver;
    }

    public List<RouteResponse> optimize(OptimizationRequest req) {
        System.out.println("OPTIMIZING____________________");
        List<LatLng> allCoordinates = new ArrayList<>();
        Map<String, Integer> keyToIndex = new HashMap<>();
        Map<Integer, VisitInfo> indexToVisitInfo = new HashMap<>();
        Map<String, DayLocation> dayLocations = req.getDayLocations();
        List<Destination> destinations = req.getDestinations();
        
        // 기존 로직에서 dayLocations와 destinations의 중복을 처리하고 키를 'day * 10000 + order'로 생성
        int index = 0;

        // 1) dayLocations의 시작/종료 좌표 추가
        for (Map.Entry<String, DayLocation> entry : dayLocations.entrySet()) {
            DayLocation dl = entry.getValue();
            String startKey = dl.getStartPlaceId().isEmpty() ? dl.getStartContentId() : dl.getStartPlaceId();
            String endKey = dl.getEndPlaceId().isEmpty() ? dl.getEndContentId() : dl.getEndPlaceId();

            // day * 10000 + order를 키로 사용
            keyToIndex.put(startKey, index);
            indexToVisitInfo.put(index++, new VisitInfo(-1, -1, startKey));
            allCoordinates.add(dl.getStartCoordinates());

            keyToIndex.put(endKey, index);
            indexToVisitInfo.put(index++, new VisitInfo(-1, -1, endKey));
            allCoordinates.add(dl.getEndCoordinates());
        }

        // 2) destinations의 좌표 추가
        for (Destination d : destinations) {
            String key = !d.getPlaceId().isEmpty() ? d.getPlaceId() : String.valueOf(d.getContentId());
            if (!keyToIndex.containsKey(key)) {
                // key 생성: day * 10000 + order
                String hashKey = d.getDay() * 10000000 + d.getOrder() + "";
                keyToIndex.put(hashKey, index);
                indexToVisitInfo.put(index++, new VisitInfo(d.getDay(), d.getOrder(), hashKey));
                allCoordinates.add(new LatLng(d.getLatitude(), d.getLongitude()));
            }
        }

        // 3) 거리 행렬 계산
        long[][] distanceMatrix = distanceClient.getTimeMatrix(allCoordinates);

        // 4) 서버 로그에 distanceMatrix 찍기
        System.out.println("=== Distance Matrix ===");
        for (int i = 0; i < distanceMatrix.length; i++) {
            System.out.println("row " + i + ": " + Arrays.toString(distanceMatrix[i]));
        }
        System.out.println("=======================");

        // 5) OR-Tools로 최적 경로 계산
        int[] starts = new int[dayLocations.size()];
        int[] ends = new int[dayLocations.size()];
        for (int i = 0; i < dayLocations.size(); i++) {
            starts[i] = i * 2;
            ends[i] = i * 2 + 1;
        }
        List<RouteResult> routes = solver.solve(distanceMatrix, starts, ends, dayLocations.size());

        // 6) RouteResult → RouteResponse 변환
        List<RouteResponse> result = new ArrayList<>();
        int day = 1;
        for (RouteResult r : routes) {
            List<VisitInfo> visits = r.getRoute().stream()
                .map(indexToVisitInfo::get)
                .filter(vk -> vk.getDay() != -1 && vk.getOrder() != -1)
                .collect(Collectors.toList());

            String start = indexToVisitInfo.get(r.getRoute().get(0)).getKey();
            String end   = indexToVisitInfo.get(r.getRoute().get(r.getRoute().size() - 1)).getKey();
            result.add(new RouteResponse(day++, start, end, r.getDuration(), visits));
        }

        // 7) 서버 로그에 결과 JSON 찍기
        try {
            String jsonResult = jsonMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);
            System.out.println("=== Optimization Result JSON ===");
            System.out.println(jsonResult);
            System.out.println("================================");
        } catch (JsonProcessingException e) {
            System.err.println("JSON 직렬화 실패: " + e.getMessage());
        }

        return result;
    }
}
