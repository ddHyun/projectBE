package org.koreait.configs;

import jakarta.servlet.http.HttpServletResponse;
import org.koreait.configs.jwt.CustomJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //@PreAuthorize("hasAuthority('ADMIN')) 사용시 필요한 어노테이션
public class SecurityConfig {

    @Autowired
    private CustomJwtFilter customJwtFilter;
    @Autowired
    private CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())     //csrf토튼 해제, 이걸 사용하면 jwt토큰 사용 불가
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(c -> {
            c.authenticationEntryPoint((req, res, e) -> {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED); //401
            });
            
            c.accessDeniedHandler((req, res, e) -> {
                res.sendError(HttpServletResponse.SC_FORBIDDEN); //403
            });
        });
        
        http.authorizeHttpRequests(c -> {
           c.requestMatchers(
                   "/api/v1/member", //회원가입
                   "/api/v1/member/token", //로그인
                   "/api/v1/member/exists/**").permitAll()
                   .anyRequest().authenticated(); //나머지 url은 모두 토큰인증(회원인증)해야함
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
