//package com.ssafy.controller;
//
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.http.HttpServletRequest;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.PriorityQueue;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.ssafy.dto.AttractionDTO;
//import com.ssafy.service.AttractionService;
//import com.ssafy.util.OSMGraphBuilder;
//import com.ssafy.util.OSMGraphBuilder.Edge;
//import com.ssafy.util.OSMGraphBuilder.Node;
//
//
////@WebServlet("/trip")
////public class TripController extends HttpServlet {
////	private static final long serialVersionUID = 1L;
////       
//
//@Controller
//@RequestMapping("/trip")
//
//public class TripController {
//	@Autowired
//	private AttractionService service;
////	@Override
////	public void init() throws ServletException {
////		// TODO Auto-generated method stub
////		super.init();
////		service = new AttractionServiceImpl();
////	}
////	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
////		// TODO Auto-generated method stub
////		process(request, response);
////	}
////	/**
////	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
////	 */
////	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
////		// TODO Auto-generated method stub
////		process(request, response);
////	}
////	
////	private void process(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
////		// TODO Auto-generated method stub
////		String action = req.getParameter("action");
////		String url = "";
////		
////		Enumeration<String> paramNames = req.getParameterNames();
////		System.out.println("🚀 Parameters received:");
////		while (paramNames.hasMoreElements()) {
////		    String name = paramNames.nextElement();
////		    System.out.println("→ " + name + " = " + req.getParameter(name));
////		}
////
////		
////		try {
////			if (action == null || action.isBlank()) {
////				url = getinit(req, res);
////			}
////			else {
////				if (action.equals("fetch")) {
////					url = getboxinfos(req, res);
////				}
////				if (action.equals("getinfos")) {
////					url = getinfos(req, res);
////				}
////				if (action.equals("route")) {
////					url = getpaths(req, res);
////				}
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////		if (url == null) return;
////		
////		if(url.startsWith("redirect")) {
////			url = url.substring(url.indexOf(":")+1);
////			res.sendRedirect(req.getContextPath()+url);
////		}
////		else {
////			req.getRequestDispatcher(url).forward(req, res);
////		}
////	}
//	
//	@GetMapping("")
//	private String getinit(@RequestParam(required=false) String areaCode,
//						@RequestParam(required=false) String sigunguCode,
//						@RequestParam(required=false) String contentType) {
//		
//		System.out.println("areacode : " + areaCode + "\n sigunguCode: " + sigunguCode + " \ncontentType: " + contentType);
////		if (areaCode != null) {
////			
////		}
////		if (sigunguCode != null) {
////			
////		}
////		if (contentType != null) {
////			
////		}
//		return "trip/localtrip";
//	}
//	@PostMapping("/getinfos")
//	private String getinfos(Model m,
//							@RequestParam(required=false) String areaCode,
//							@RequestParam(required=false) String sigunguCode,
//							@RequestParam(required=false) String contentType) throws NumberFormatException, SQLException {
//
////		String areaCode = req.getParameter("areaCode");
////		String sigunguCode = req.getParameter("sigunguCode");
////		String contentType = req.getParameter("contentType");
//		
//		int areaInt = areaCode != null ? Integer.parseInt(areaCode) : -1;
//		int sigunguInt = sigunguCode != null ? Integer.parseInt(sigunguCode) : -1;
//		int contentInt = contentType != null ? Integer.parseInt(contentType) : -1;
//		
//		System.out.println("areacode : " + areaCode + "\n sigunguCode: " + sigunguCode + " \ncontentType: " + contentType);
//		
//		List<AttractionDTO> list = service.getcities(areaInt, sigunguInt, contentInt);
//		
//		System.out.println("res size : " + list.size());
//		for (AttractionDTO l : list) {
//			System.out.println(l);
//		}
//		m.addAttribute("list", list);
//		
//		return "trip/localtrip";
//	}
//	
//	@GetMapping("/route")
//	@ResponseBody
//	private List<Map<String, Float>> getpaths(@RequestParam double fromLat,
//											  @RequestParam double fromLon,
//											  @RequestParam double toLat,
//											  @RequestParam double toLon,
//											  HttpServletRequest req) throws IOException {
////		System.out.println(req.getRequestURL());
////		double fromLat = Double.parseDouble(req.getParameter("fromLat"));
////	    double fromLon = Double.parseDouble(req.getParameter("fromLon"));
////	    double toLat = Double.parseDouble(req.getParameter("toLat"));
////	    double toLon = Double.parseDouble(req.getParameter("toLon"));
////	    
//	    System.out.println(fromLat + " " + fromLon + " " + toLat + " " + toLon);
//	    ServletContext context = req.getServletContext();
//	    OSMGraphBuilder graph = (OSMGraphBuilder) context.getAttribute("graph");
//
//	    long startNodeId = findNearestNode(fromLat, fromLon, graph);
//	    long endNodeId = findNearestNode(toLat, toLon, graph);
//
//	    // TODO: run actual pathfinding
//	    //List<Long> path = List.of(startNodeId, endNodeId);
//	    //List<Long> path = do_dijkstra(graph, startNodeId, endNodeId);
//	    long stime = System.currentTimeMillis();
//	    List<Long> path = do_astar(graph, startNodeId, endNodeId);
//	    long etime = System.currentTimeMillis();
//	    
//	    System.out.println("spent : " + (etime - stime) +"ms");
//	    //List<Long> path = do_bfs(graph, startNodeId);
//	    
//	    System.out.println("pathres : " + path.size());
//	    List<Map<String, Float>> latlngs = path.stream()
//	    	    .map(id -> {
//	    	        Node n = graph.nodeMap.get(id);
//	    	        Map<String, Float> latlng = new HashMap<>();
//	    	        latlng.put("lat", n.lat);
//	    	        latlng.put("lng", n.lon);
//	    	        return latlng;
//	    	    })
//	    	    .collect(Collectors.toList());
//
//	    System.out.println(latlngs);
//	    return latlngs;
////	    res.setContentType("application/json");
////	    res.setCharacterEncoding("UTF-8");
////	    res.getWriter().write(new com.google.gson.Gson().toJson(latlngs));
//	    
//	    
//	}
//	
////	@GetMapping("")
////	private String getboxinfos(HttpServletRequest req, HttpServletResponse res) {
////		// TODO Auto-generated method stub
////		
////		return "trip/localtrip";
////	}
////	
//
//	
//	class point implements Comparable<point>{
//		long id;
//		int lv;
//		float dist;
//		public point(float dist, int lv, long id) {
//			// TODO Auto-generated constructor stub
//			this.id = id;
//			this.lv = lv;
//			this.dist = dist;
//		}
//		@Override
//		public int compareTo(point o) {
//			// TODO Auto-generated method stub
//			return dist < o.dist ? 1 : -1;
//		}
//		
//	}
////	private List<Long> do_bfs(OSMGraphBuilder graphholder, long startNodeId) {
////		HashMap<Long, Node> graph = graphholder.nodeMap;
////		
////		Queue<point> q = new ArrayDeque<>();
////		HashMap<Long, Integer> visited = new HashMap<>();
////		HashMap<Long, Long> hist = new HashMap<>();
////		
////		q.offer(new point(0, 0, startNodeId));
////		visited.put(startNodeId, 1);
////		hist.put(startNodeId, -1L);
////		
////		long example = 0;
////		
////		while(!q.isEmpty()) {
////			point cur = q.poll();
////			double lv = cur.dist;
////			long id = cur.id;
////			if (!graph.containsKey(id)) continue;
////			if (lv > 40) continue;
////			
////			for (Edge n : graph.get(id).getEdges()) {
////				long next = n.getToNodeId();
////				if (visited.containsKey(next)) continue;
////				visited.put(next, 1);
////				hist.put(next, id);
////				example = next;
////				q.offer(new point(lv + 1, 0, next));
////			}
////		}
////	
////		ArrayList<Long> histlist = new ArrayList<>();
////		if (!hist.containsKey(example)) return histlist;
////		
////		while(example != -1l) {
////			histlist.add(example);
////			if (!hist.containsKey(example)) break;
////			example = hist.get(example);
////		}
////		Collections.reverse(histlist);
////		return histlist;
////	}
//	@Deprecated
//	private List<Long> do_dijkstra(OSMGraphBuilder graphholder, long startNodeId, long endNodeId) {
//		// TODO Auto-generated method stub
//		
//		HashMap<Long, Node> graph = graphholder.nodeMap;
//		
//		HashMap<Long, Float> dist = new HashMap<>();
//		HashMap<Long, Long> hist = new HashMap<>();
//		
//		
//		PriorityQueue<point> pq = new PriorityQueue<>();
//		
//		dist.put(startNodeId, 0.0f);
//		pq.offer(new point(0, 0, startNodeId));
//		hist.put(startNodeId, -1L);
//		
//		while(!pq.isEmpty()) {
//			point cur = pq.poll();
//			float d = cur.dist;
//			int lv = cur.lv;
//			long id = cur.id;
//			if (lv > 200) continue;
//			//System.out.println("current : " + id);
//			if (dist.containsKey(id) && dist.get(id) < d) continue;
//			if (!graph.containsKey(id)) continue;
//			for (Edge n1 : graph.get(id).edges) {
//				long next = n1.getToNodeId();
//				float cost = (float)n1.getDistance();
//				//Node nextnode = graph.get(next);
//				
//				if (!dist.containsKey(next)) {
//					dist.put(next, (d + cost));
//					pq.offer(new point(d + cost,lv + 1, next));
//					hist.put(next, id);
//					continue;
//				}
//				if (dist.get(next) > d + cost) {
//					dist.put(next, (d + cost));
//					pq.offer(new point(d + cost, lv + 1, next));
//					hist.put(next, id);
//				}
//			}
//		}
//		System.out.println("node size ; " + graph.size());
//		System.out.println("edge size : " + graphholder.edgecnt);
//		System.out.println("hist size : " + hist.size());
//		long cur = endNodeId;
//		ArrayList<Long> histlist = new ArrayList<>();
//		if (!hist.containsKey(cur)) return histlist;
//		
//		while(cur != -1l) {
//			histlist.add(cur);
//			if (!hist.containsKey(cur)) break;
//			cur = hist.get(cur);
//		}
//		Collections.reverse(histlist);
//		return histlist;
//	}
//	
//	private List<Long> do_astar(OSMGraphBuilder graphholder, long startNodeId, long endNodeId) {
//	    HashMap<Long, Node> graph = graphholder.nodeMap;
//	    
//	    HashMap<Long, Float> dist = new HashMap<>(); //g
//	    HashMap<Long, Long> hist = new HashMap<>();
//	    HashSet<Long> closedSet = new HashSet<>();
//	    
//	 
//	    PriorityQueue<Node> pq = new PriorityQueue<>();
//	    
//	    System.out.println("node size: " + graph.size());
//	    System.out.println("edge size: " + graphholder.edgecnt);
//	    System.out.println("min dist: " + graphholder.mindist);
//	    System.out.println("max dist: " + graphholder.maxdist);
//	    
//	    Node start = graph.get(startNodeId);
//	    Node enode = graph.get(endNodeId);
//	    
//	    float elat = (float) enode.lat;
//	    float elng = (float) enode.lon;
//	    
//	    start.lv = 0;
//	    start.dist = 0;
//	    start.f = (float)Math.sqrt(Math.pow(start.lat - elat, 2) + Math.pow(start.lon - elng, 2));
//	    
//	    pq.offer(start);
//	    hist.put(startNodeId, -1L);
//	    dist.put(startNodeId, 0.0f);
//	    
//	    while (!pq.isEmpty()) {
//	        Node cur = pq.poll();
//	        float d = cur.dist;
//	        long id = cur.id;
//	        
//	        if (closedSet.contains(id)) continue;
//	        closedSet.add(id);
//	        
//	        if (id == endNodeId) break;
//	        
//	        if (!graph.containsKey(id)) continue;
//	        
//	        for (Edge n1 : graph.get(id).edges) {
//	            long next = n1.getToNodeId();
//	            
//	            
//	            float cost = n1.getDistance();
//	            
//	            Node nextnode = graph.get(next);
//	            if (nextnode == null) continue; 
//	            
//	            float lat = nextnode.lat;
//	            float lng = nextnode.lon;
//	            //l2 norm
//	            float h = (float)Math.sqrt(Math.pow(elat - lat, 2) + Math.pow(elng - lng, 2));
//	            
//	            float g = d + cost; 
//	            float f = g + h;
//	            
//	            if (g < dist.getOrDefault(next, Float.MAX_VALUE)) {
//	                dist.put(next, g);
//	                nextnode.f = f;
//	                nextnode.dist = g;
//	                nextnode.lv = cur.lv + 1;
//	                pq.offer(nextnode);
//	                hist.put(next, id);
//	            }
//	        }
//	    }
//	    
//	    System.out.println("hist size: " + hist.size());
//	    long curId = endNodeId;
//	    ArrayList<Long> histlist = new ArrayList<>();
//	    if (!hist.containsKey(curId)) return histlist;
//	    
//	    while (curId != -1L) {
//	        histlist.add(curId);
//	        if (!hist.containsKey(curId)) break;
//	        curId = hist.get(curId);
//	    }
//	    
//	    for (Long c : closedSet) {
//	        Node target = graph.get(c);
//	        target.lv = 0;
//	        target.dist = 0;
//	        target.visited = false;
//	        target.f = 0;
//	    }
//	    
//	    Collections.reverse(histlist);
//	    return histlist;
//	}	
//	
//	private long findNearestNode(double lat, double lon, OSMGraphBuilder graph) {
//        double minDist = Double.MAX_VALUE;
//        long nearestId = -1;
//
//        for (var entry : graph.nodeMap.entrySet()) {
//            var node = entry.getValue();
//            if (node.edges.size() == 0) continue;
//            double d = haversine(lat, lon, node.lat, node.lon);
//            if (d < minDist) {
//                minDist = d;
//                nearestId = node.id;
//            }
//        }
//        return nearestId;
//    }
//
//    private double haversine(double lat1, double lon1, double lat2, double lon2) {
//        double R = 6371;
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//    }
//    
//}
