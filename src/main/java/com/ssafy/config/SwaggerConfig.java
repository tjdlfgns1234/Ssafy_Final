package com.ssafy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("SSAFY REST API")
                        .description("SSAFY 멤버·여행계획·게시글·댓글·장소 관리 API 문서")
                        .version("1.0.0")
                );
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("member-api")
                .pathsToMatch("/api/v1/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi travelPlanApi() {
        return GroupedOpenApi.builder()
                .group("travelplan-api")
                .pathsToMatch("/api/v1/travelplans/**")
                .build();
    }

    @Bean
    public GroupedOpenApi postApi() {
        return GroupedOpenApi.builder()
                .group("post-api")
                .pathsToMatch("/api/v1/posts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commentApi() {
        return GroupedOpenApi.builder()
                .group("comment-api")
                .pathsToMatch("/api/v1/comments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tripApi() {
        return GroupedOpenApi.builder()
                .group("trip-api")
                .pathsToMatch("/api/v1/trip/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userPlaceApi() {
        return GroupedOpenApi.builder()
                .group("userplace-api")
                .pathsToMatch("/api/v1/userplaces/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi algorithmApi() {
        return GroupedOpenApi.builder()
                .group("algorithm")
                .pathsToMatch("/api/v1/optimize/**")
                .build();
    }
    @Bean
    public GroupedOpenApi metricsApi() {
        return GroupedOpenApi.builder()
                .group("metrics-api")
                .pathsToMatch("/api/v1/metrics/**")
                .build();
    }
}
