package com.ssafy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//	@Bean
//	public ViewResolver viewResolver() {
//		InternalResourceViewResolver ver = new InternalResourceViewResolver();
//		ver.setPrefix("/WEB-INF/views/");
//		ver.setSuffix(".jsp");
//		return ver;
//	}
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		// TODO Auto-generated method stub
//		registry.addResourceHandler("/img/**").addResourceLocations("/WEB-INF/img/");
//		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");
//		registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/");
//	}
	
//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		// TODO Auto-generated method stub
//		registry.addInterceptor(new  MyInterceptor()).addPathPatterns("/**");
//	}
	
//	@Bean
//	public MyInterceptor myInterceptor() {
//		return new MyInterceptor();
//	}
	
//	localhost:8080/img/hello.jpg
//	<img href="/img/hello.jpg">
	// CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")               // /api 로 시작하는 모든 경로
                .allowedOrigins("http://localhost:5173") // Vite dev 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                .allowCredentials(true);             // 세션 쿠키 허용
    }
	
//	
//	@Override
//	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/").setViewName("index");
//		registry.addViewController("/member/login-form").setViewName("member/login");
//	}
}
