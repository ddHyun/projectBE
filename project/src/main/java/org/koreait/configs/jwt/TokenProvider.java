package org.koreait.configs.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

import java.security.Key;

public class TokenProvider {
    //한 번 설정하면 바뀌지 않도록 상수(final)로 설정 -> 내부에서 못 바꾸게 막기
    private final String secret;
    private final Long accessTokenValidityInSeconds;
    private Key key;

    public TokenProvider(String secret, Long accessTokenValidityInSeconds){ //HMAC(해시) : SHA512 + Message(추가검증)
        this.secret = secret; //인코딩되어 있는 상태라 디코딩 후 hmac
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;

        byte[] bytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /*회원정보를 담은 토큰 만들기*/
    public String createToken(Authentication authentication){
        return null;
    }
}
