package org.koreait.models.member;

import lombok.RequiredArgsConstructor;
import org.koreait.commons.constants.MemberType;
import org.koreait.entities.Member;
import org.koreait.repositories.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*
    MemberInfo 구현클래스.
 */
@Service
@RequiredArgsConstructor //final이 선언된 모든 필드를 인자값으로 하는 생성자 생성해주는 어노테이션
public class MemberInfoService implements UserDetailsService {

    private final MemberRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = repository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        MemberType type = Objects.requireNonNullElse(member.getType(), MemberType.USER);
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(type.name()));

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .authorities(authorities)
                .member(member)
                .build();
    }
}
