package com.ssafy.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.dto.AttractionDTO;
import com.ssafy.service.AttractionService;
//import com.ssafy.util.OSMGraphBuilder;
//import com.ssafy.util.OSMGraphBuilder.Edge;
//import com.ssafy.util.OSMGraphBuilder.Node;

@RestController
@RequestMapping("/api/v1/trip")

public class TripRestController {
	@Autowired
    private AttractionService service;
	

    ///GET /api/v1/trip/infos?areaCode=&sigunguCode=&contentType=
    @GetMapping("/infos")
    public List<AttractionDTO> getInfos(
            @RequestParam(required = false) Integer areaCode,
            @RequestParam(required = false) Integer sigunguCode,
            @RequestParam(required = false) Integer contentType,
            @RequestParam(required = false, defaultValue = "") String keyword
    ) throws SQLException {
        int area    = (areaCode    != null) ? areaCode    : -1;
        int sigungu = (sigunguCode != null) ? sigunguCode : -1;
        int content = (contentType != null) ? contentType : -1;
        return service.getcities(area, sigungu, content, keyword);
    }
    ///GET /api/v1/trip/route?fromLat=...&fromLon=...&toLat=...&toLon=...
//    @GetMapping("/route")
//    public List<Map<String, Double>> getRoute(
//            @RequestParam double fromLat,
//            @RequestParam double fromLon,
//            @RequestParam double toLat,
//            @RequestParam double toLon,
//            HttpServletRequest req
//    ) throws IOException {
//    	ServletContext context = req.getServletContext();
//	    OSMGraphBuilder graph = (OSMGraphBuildzker) context.getAttribute("graph");
//	    
//        long startId = findNearestNode(fromLat, fromLon, graph);
//        long   endId = findNearestNode(toLat,   toLon,   graph);
//
//        List<Long> path = doAstar(graph, startId, endId);
//
//        return path.stream().map(id -> {
//            Node n = graph.nodeMap.get(id);
//            Map<String, Double> m = new HashMap<>();
//            m.put("lat", (double) n.lat);
//            m.put("lon", (double) n.lon);
//            return m;
//        }).collect(Collectors.toList());
//    }
//
//    private List<Long> doAstar(OSMGraphBuilder graphholder, long startNodeId, long endNodeId) {
//        Map<Long, Node> graphMap = graphholder.nodeMap;
//        Map<Long, Float> gScore  = new HashMap<>();
//        Map<Long, Long>  prev    = new HashMap<>();
//        Set<Long>        closed   = new HashSet<>();
//
//        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
//
//        Node start = graphMap.get(startNodeId);
//        Node goal  = graphMap.get(endNodeId);
//        float gx0 = start.dist = 0;
//        float hx0 = heuristic(start.lat, start.lon, goal.lat, goal.lon);
//        start.f = gx0 + hx0;
//
//        gScore.put(startNodeId, 0f);
//        prev.put(startNodeId, -1L);
//        pq.offer(start);
//
//        while (!pq.isEmpty()) {
//            Node cur = pq.poll();
//            if (!closed.add(cur.id)) continue;
//            if (cur.id == endNodeId) break;
//
//            for (Edge e : graphMap.get(cur.id).edges) {
//                long   nid  = e.getToNodeId();
//                float  cost = (float) e.getDistance();
//                float  gNew = gScore.get(cur.id) + cost;
//
//                if (gNew < gScore.getOrDefault(nid, Float.MAX_VALUE)) {
//                    gScore.put(nid, gNew);
//                    prev.put(nid, cur.id);
//
//                    Node nb = graphMap.get(nid);
//                    float h  = heuristic(nb.lat, nb.lon, goal.lat, goal.lon);
//                    nb.f      = gNew + h;
//                    nb.dist   = gNew;
//                    pq.offer(nb);
//                }
//            }
//        }
//
//        // 경로 재구성
//        List<Long> route = new ArrayList<>();
//        Long cur = endNodeId;
//        while (cur != null && cur != -1) {
//            route.add(cur);
//            cur = prev.get(cur);
//        }
//        Collections.reverse(route);
//        return route;
//    }
//
//    private static float heuristic(double lat1, double lon1, double lat2, double lon2) {
//        double dy = lat1 - lat2, dx = lon1 - lon2;
//        return (float) Math.sqrt(dx*dx + dy*dy);
//    }
//
//    // === 최단거리 노드 찾기 ===
//    private long findNearestNode(double lat, double lon, OSMGraphBuilder graph) {
//        double min = Double.MAX_VALUE;
//        long   best = -1;
//        for (Node n : graph.nodeMap.values()) {
//            if (n.edges.isEmpty()) continue;
//            double d = haversine(lat, lon, n.lat, n.lon);
//            if (d < min) {
//                min = d;
//                best = n.id;
//            }
//        }
//        return best;
//    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                 + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }
}
