package com.ohyes.GrowUpMoney.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 출처
        //config.addAllowedOriginPattern("");
        config.addAllowedOrigin("https://growupmoney.duckdns.org"); // 허용할 도메인
        config.addAllowedOrigin("https://localhost:3000");  // React 앱의 Origin
        config.addAllowedOrigin("https://localhost:5173");  // React 앱의 Origin
        config.addAllowedMethod(""); // 모든 HTTP 메소드 허용
        config.addAllowedHeader(""); // 모든 헤더 허용
        config.setAllowCredentials(true); // 쿠키를 포함한 요청 허용

        // ★ 사용하는 프론트 도메인만 정확히 허용
        // config.setAllowedOrigins(Arrays.asList(
        //         "http://localhost:3000/",            // React 개발 서버
        //         "https://localhost:5173/",            // React 개발 서버
        //         "https://localhost:3000/",            // React 개발 서버
        //         "https://growupmoney.duckdns.org/"   // 혹시나 프론트도 여기서 띄울 때
        // ));

        // 허용할 HTTP 메소드
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));


        // 허용할 헤더
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                ""
        ));

        // 인증 정보 포함 허용
        config.setAllowCredentials(true);

        // 캐시 시간 (3600초 = 1시간)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}



