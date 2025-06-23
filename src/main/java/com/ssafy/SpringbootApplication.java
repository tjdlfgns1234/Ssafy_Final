package com.ssafy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ssafy.util.OrToolsNativeLoader;

@SpringBootApplication
@MapperScan("com.ssafy.repository")
public class SpringbootApplication {

	public static void main(String[] args) {
		OrToolsNativeLoader.init();
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
