package org.koreait.configs.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/* 토큰을 받으면 로그인을 유지시켜주는 역할 */

@Component
@RequiredArgsConstructor
public class CustomJwtFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;

        /* 요청헤더 Authorization 항복의 JWT 토큰 추출 S */
        String header = req.getHeader("Authorization");
        String jwt = null;
        if(StringUtils.hasText(header)){
            //header의 Authoriation은 Bearer ...로 시작할거라 7번째 index부터 가져오면 그게 토큰임
            jwt = header.substring(7);
        }
        /* 요청헤더 Authorization 항복의 JWT 토큰 추출 E */

        /* 로그인 유지 처리 S */
        if(StringUtils.hasText(jwt)){
            tokenProvider.validateToken(jwt);//토큰 이상시 예외 발생

            //이상이 없다면 로그인 유지
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        /* 로그인 유지 처리 E */

        chain.doFilter(request, response);
    }
}
