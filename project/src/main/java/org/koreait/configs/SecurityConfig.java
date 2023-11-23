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
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) //데이터공유를 다른 도메인에서도 가능하게
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class) //토큰이 요청헤더쪽에서 넘어오면 로그인 되게
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //세션 사용 안함

        http.exceptionHandling(c -> {   //인가 실패시 유입되는 곳. 페이지 이동이 아닌 응답코드만 적기로 함
            c.authenticationEntryPoint((req, res, e) -> {   //토큰이 없을 경우, 로그인 안했을 경우
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED); //401
            });
            
            c.accessDeniedHandler((req, res, e) -> { //로그인은 했지만 특정 권한이 없는 경우
                res.sendError(HttpServletResponse.SC_FORBIDDEN); //403
            });
        });
        
        http.authorizeHttpRequests(c -> { //인가에 대한 주소관련 설정
           c.requestMatchers(
                   "/api/v1/member", //회원가입
                   "/api/v1/member/token" //로그인
//                   "/api/v1/member/exists/**"
                   ).permitAll()  //인증 필요없이 다 사용가능
                   .anyRequest().authenticated(); //나머지 url은 모두 토큰인증(회원인증)해야함
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
