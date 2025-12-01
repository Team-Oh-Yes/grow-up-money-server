package com.ohyes.GrowUpMoney.global.config;

import com.ohyes.GrowUpMoney.domain.auth.service.MemberDetailsService;
import com.ohyes.GrowUpMoney.global.jwt.JwtFilter;
import com.ohyes.GrowUpMoney.global.oauth.handler.OAuth2SuccessHandler;
import com.ohyes.GrowUpMoney.global.oauth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberDetailsService memberDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtFilter jwtFilter;
    private final CustomOAuth2UserService customOAuth2UserService;  // 추가
    private final OAuth2SuccessHandler oAuth2SuccessHandler;        // 추가





    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.addFilterBefore(jwtFilter, ExceptionTranslationFilter.class);

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                )
                .successHandler(oAuth2SuccessHandler)
        );

        http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/error").permitAll()
                                .requestMatchers("/users/signup","/users/login","/users/logout", "/users/me").permitAll()
                                .requestMatchers(
                                        "/oauth2/**",
                                        "/login/oauth2/**",
                                        "/oauth2/authorization/**"
                                ).permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/api-docs",
                                        "/api-docs/*"
                                ).permitAll()
                                .requestMatchers("/users/refresh").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()

                );
        return http.build();
    }
}
