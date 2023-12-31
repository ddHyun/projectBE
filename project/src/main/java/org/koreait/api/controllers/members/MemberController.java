package org.koreait.api.controllers.members;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.commons.Utils;
import org.koreait.commons.exceptions.BadRequestException;
import org.koreait.commons.rests.JSONData;
import org.koreait.entities.Member;
import org.koreait.models.member.MemberInfo;
import org.koreait.models.member.MemberLoginService;
import org.koreait.models.member.MemberSaveService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberSaveService saveService;
    private final MemberLoginService loginService;

    @PostMapping
    public ResponseEntity<JSONData> join(@RequestBody @Valid RequestJoin form, Errors errors){
        //@RequestBody : json형태로 데이터 내보내기
        saveService.save(form, errors);

        errorProcess(errors);

        JSONData data = new JSONData();
        data.setStatus(HttpStatus.CREATED);

        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/token")
    public ResponseEntity<JSONData> token(@RequestBody @Valid RequestLogin form, Errors errors){
        errorProcess(errors);

        String accessToken = loginService.login(form);

        /* 토큰을 두 군데에 다 담았음
        * 1. 응답바디 - JSONData 형식으로
        * 2. 응답헤더 - Authorization : Bearer 토큰
        */

        JSONData data = new JSONData(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+accessToken);

        return ResponseEntity.status(data.getStatus()).headers(headers).body(data);
    }

    @GetMapping("/info")
    public JSONData info(@AuthenticationPrincipal MemberInfo memberInfo){
        Member member = memberInfo.getMember();

        return new JSONData(member);
    }

    private void errorProcess(Errors errors){
        if(errors.hasErrors()) {
            throw new BadRequestException(Utils.getMessages(errors));
        }
    }
}
