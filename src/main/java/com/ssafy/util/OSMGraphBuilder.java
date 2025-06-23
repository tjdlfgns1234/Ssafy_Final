//package com.ssafy.util;
//
//import de.topobyte.osm4j.core.access.OsmIterator;
//import de.topobyte.osm4j.core.model.iface.*;
//import de.topobyte.osm4j.pbf.seq.PbfIterator;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.*;
//
//public class OSMGraphBuilder {
//
//    public static class Node implements Comparable<Node>{
//        public long id;
//        public float lat, lon;
//        public List<Edge> edges = new ArrayList<>();
//        
//        public float f;
//        public float dist; //g when uses astar
//        public int lv;
//        public boolean visited;
//
//        public Node(long id, float lat, float lon) {
//            this.id = id;
//            this.lat = lat;
//            this.lon = lon;
//        }
//
//
//		@Override
//		public int compareTo(Node o) {
//			// TODO Auto-generated method stub
//			return f > o.f ? 1 : -1;
//		}
//    }
//
//    public static class Edge {
//        long toNodeId;
//        float distance;
//
//        public Edge(long toNodeId, float distance) {
//            this.toNodeId = toNodeId;
//            this.distance = distance;
//        }
//
//		public long getToNodeId() {
//			return toNodeId;
//		}
//
//		public void setToNodeId(long toNodeId) {
//			this.toNodeId = toNodeId;
//		}
//
//		public float getDistance() {
//			return distance;
//		}
//
//		public void setDistance(long distance) {
//			this.distance = distance;
//		}
//    }
//
//    // Main graph map: Node ID → Node
//    public HashMap<Long, Node> nodeMap = new HashMap<>();
//    public int edgecnt = 0;
//    public double mindist = 1000000000d;
//    public double maxdist = 0d;
//    // Allowed road types (filter out footways, paths, etc.)
//    private final Set<String> allowedHighways = Set.of(
//            "residential", "primary", "secondary", "tertiary",
//            "unclassified", "trunk", "service", "motorway",
//            "living_street", "road"
//    );
//
//    public void buildGraph(File file) throws Exception {
//        InputStream input = new FileInputStream(file);
//        OsmIterator iterator = new PbfIterator(input, false);
//        edgecnt = 0;
//
//        List<OsmWay> ways = new ArrayList<>();
//
//        while (iterator.hasNext()) {
//            OsmEntity entity = iterator.next().getEntity();
//
//            if (entity instanceof OsmNode node) {
//                nodeMap.put(node.getId(), new Node(
//                        node.getId(),
//                        (float)node.getLatitude(),
//                        (float)node.getLongitude()
//                ));
//            } else if (entity instanceof OsmWay way) {
//                boolean isRoad = false;
//                for (int i = 0; i < way.getNumberOfTags(); i++) {
//                    OsmTag tag = way.getTag(i);
//                    if (tag.getKey().equals("highway") &&
//                            allowedHighways.contains(tag.getValue())) {
//                        isRoad = true;
//                        break;
//                    }
//                }
//
//                if (isRoad) {
//                    ways.add(way);
//                }
//            }
//        }
//
//        for (OsmWay way : ways) {
//            boolean isOneWay = false;
//            boolean isReverse = false;
//
//            for (int i = 0; i < way.getNumberOfTags(); i++) {
//                OsmTag tag = way.getTag(i);
//                String k = tag.getKey();
//                String v = tag.getValue();
//
//                if (k.equals("oneway")) {
//                    if (v.equals("yes")) isOneWay = true;
//                    else if (v.equals("-1")) {
//                        isOneWay = true;
//                        isReverse = true;
//                    }
//                }
//
//                if (k.equals("junction") && v.equals("roundabout")) {
//                    isOneWay = true;
//                }
//
//                if (k.equals("highway") && v.equals("motorway")) {
//                    isOneWay = true;
//                }
//            }
//
//            List<Long> nodeIds = new ArrayList<>();
//            for (int i = 0; i < way.getNumberOfNodes(); i++) {
//                nodeIds.add(way.getNodeId(i));
//            }
//
//            if (isReverse) {
//                Collections.reverse(nodeIds);
//            }
//
//            for (int i = 1; i < nodeIds.size(); i++) {
//                long fromId = nodeIds.get(i - 1);
//                long toId = nodeIds.get(i);
//
//                Node from = nodeMap.get(fromId);
//                Node to = nodeMap.get(toId);
//
//                if (from != null && to != null) {
//                    float dist = (float)haversine(from.lat, from.lon, to.lat, to.lon);
//                    mindist = Math.min(dist, mindist);
//                    maxdist = Math.max(maxdist, dist);
//                    if (dist < 0) {
//                    	throw new NumberFormatException();
//                    }
//                    from.edges.add(new Edge(to.id, dist));
//                    edgecnt++;
//                    if (!isOneWay) {
//                        to.edges.add(new Edge(from.id, dist));
//                        edgecnt++;
//                    }
//                }
//            }
//        }
//
//        input.close();
//        System.out.println("Graph built: " + nodeMap.size() + " nodes");
//    }
//
//    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
//        double R = 6371; // Earth radius in meters //6371 for km
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//    }
//}
