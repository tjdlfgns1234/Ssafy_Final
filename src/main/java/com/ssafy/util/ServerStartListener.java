package com.ssafy.util;

import jakarta.servlet.ServletContext;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class ServerStartListener {

    @Autowired
    private ServletContext servletContext;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            String osmPath = "classpath:static/seoul-non-military.osm.pbf";
//            File File0 = ResourceUtils.getFile("classpath:application.properties");
//            System.out.println(File0.exists());
            //File file = ResourceUtils.getFile(osmPath);
            
//            OSMGraphBuilder builder = new OSMGraphBuilder();
//            builder.buildGraph(file);
//        	
//            servletContext.setAttribute("graph", builder);
//            System.out.println("✅ Road graph loaded and cached at startup.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load OSM graph", e);
        }
    }
}



//@WebListener
//public class ServerStartListener implements ServletContextListener {
//
//    /**
//     * Default constructor. 
//     */
//    public ServerStartListener() {
//        // TODO Auto-generated constructor stub
//    	
//    }
//
//	/**
//     * @see ServletContextListener#contextInitialized(ServletContextEvent)
//     */
//    public void contextInitialized(ServletContextEvent sce)  { 
//         // TODO Auto-generated method stub
//    	try {
//            String osmPath = sce.getServletContext().getRealPath("trip/seoul-non-military.osm.pbf"); // adjust path
////            String osmPath = sce.getServletContext().getRealPath("trip/south-korea-latest.osm.pbf"); // adjust path
//
//            OSMGraphBuilder builder = new OSMGraphBuilder();
//            builder.buildGraph(osmPath);
//
//            // Store graph in application context
//            sce.getServletContext().setAttribute("graph", builder);
//
//            System.out.println("✅ Road graph loaded and cached at startup.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to load OSM graph", e);
//        }
//    }
//
//	/**
//     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
//     */
//    public void contextDestroyed(ServletContextEvent sce)  { 
//         // TODO Auto-generated method stub
//    }
//	
//}
