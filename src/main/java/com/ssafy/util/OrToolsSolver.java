// src/main/java/com/ssafy/util/OrToolsSolver.java
package com.ssafy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.LocalSearchMetaheuristic;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.ssafy.dto.RouteResult;

/**
 * OR-Tools를 이용해 VRP 최적 경로를 계산하는 클래스
 */
@Component
public class OrToolsSolver {
    static {
        // JNI 로더 초기화 (jniortools.dll 로드)
        OrToolsNativeLoader.init();
    }

    /**
     * @param distanceMatrix 시간/거리 행렬
     * @param starts 출발 인덱스 배열
     * @param ends 도착 인덱스 배열
     * @param vehicleCount 차량(일) 수
     * @return 각 차량별 RouteResult 리스트
     */
    public List<RouteResult> solve(long[][] distanceMatrix, int[] starts, int[] ends, int vehicleCount) {
        int nodeCount = distanceMatrix.length;
        RoutingIndexManager manager = new RoutingIndexManager(nodeCount, vehicleCount, starts, ends);
        RoutingModel routing = new RoutingModel(manager);

        // 거리/시간 콜백 등록
        final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
            int fromNode = manager.indexToNode(fromIndex);
            int toNode = manager.indexToNode(toIndex);
            return distanceMatrix[fromNode][toNode];
        });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        // 시간 제한 차원 설정: 슬랙 0, 차량별(날자별) 최대 8시간(28800초)
        long[] vehicleCapacity = new long[vehicleCount];
        Arrays.fill(vehicleCapacity, 28800);
        routing.addDimensionWithVehicleCapacity(
            transitCallbackIndex, 0, vehicleCapacity, true, "Time");

        // 검색 파라미터: 기본값 기반으로 relocate 포함 모든 local search ON
        RoutingSearchParameters parameters = main.defaultRoutingSearchParameters()
            .toBuilder()
            .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
            // 필요한 경우 추가 local_search 옵션 설정
            .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)
            //.setLocalSearchNeighborhoodManager(NeighborhoodManager.Value.TWICE_LOCAL_SEARCH) // 지역 최적화
            .setSolutionLimit(1500)
            .build();

        Assignment solution = routing.solveWithParameters(parameters);
        List<RouteResult> results = new ArrayList<>();

        if (solution != null) {
            RoutingDimension timeDim = routing.getMutableDimension("Time");
            for (int vid = 0; vid < vehicleCount; vid++) {
                List<Integer> route = new ArrayList<>();
                long index = routing.start(vid);
                while (!routing.isEnd(index)) {
                    route.add(manager.indexToNode(index));
                    index = solution.value(routing.nextVar(index));
                }
                route.add(manager.indexToNode(index));
                long duration = solution.value(timeDim.cumulVar(index));
                results.add(new RouteResult(route, duration));
            }
        }
        return results;
    }
}
