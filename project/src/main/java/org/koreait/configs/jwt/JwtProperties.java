package org.koreait.configs.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/*
    토큰을 발급하고 토큰에 데이터를 담아 전달, 검증하는 JWT의 전달객체
 */

@Data
@ConfigurationProperties(prefix = "jwt")    //jwt로 시작하는 하위속성을 변수에 자동으로 담아준다
public class JwtProperties {
    private String header;
    private String secret;
    private Long accessTokenValidityInSeconds;
}
