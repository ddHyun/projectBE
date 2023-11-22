package org.koreait.configs.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.koreait.commons.Utils;
import org.koreait.commons.exceptions.BadRequestException;
import org.koreait.models.member.MemberInfo;
import org.koreait.models.member.MemberInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TokenProvider {
    //한 번 설정하면 바뀌지 않도록 상수(final)로 설정 -> 내부에서 못 바꾸게 막기
    private final String secret;
    private final Long accessTokenValidityInSeconds;
    private Key key;

    @Autowired
    private MemberInfoService infoService;

    public TokenProvider(String secret, Long accessTokenValidityInSeconds){ //HMAC(해시) : SHA512 + Message(추가검증)
        this.secret = secret; //인코딩되어 있는 상태라 디코딩 후 hmac
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;

        byte[] bytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /*회원정보를 담은 토큰 만들기*/
    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //getTime():현재시간
        Date expires = new Date((new Date()).getTime()+accessTokenValidityInSeconds * 1000);

        return Jwts.builder()
                .setSubject(authentication.getName())//아이디
                .claim("auth", authorities)//접근권한
                .signWith(key, SignatureAlgorithm.HS512) //HMAC + SHA512
                .setExpiration(expires)//토큰유효시간(밀리세컨즈)
                .compact();
    }

    /* 토큰으로 회원정보 조회, Spring Security에 통합 */
    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJwt(token)
                .getPayload();

        String email = claims.getSubject();
        MemberInfo userDetails = (MemberInfo) infoService.loadUserByUsername(email);

        String auth = claims.get("auth").toString();
        List<? extends GrantedAuthority> authorities = Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new).toList();
        userDetails.setAuthorities(authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, token, authorities);

        return authentication;
    }

    /* 토큰 검증(만료, 불일치 등) 예외 발생시키기 */
    public void validateToken(String token){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(token)
                    .getPayload();
        } catch (ExpiredJwtException e){
            throw new BadRequestException(Utils.getMessage("EXPIRED.JWT_TOKEN", "validation"));
        } catch (UnsupportedJwtException e){
            throw new BadRequestException(Utils.getMessage("UNSUPPORTED.JWT_TOKEN", "validation"));
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException e){
            throw new BadRequestException(Utils.getMessage("INVALID_FORMAT.JWT_TOKEN", "validation"));
        }
    }
}
