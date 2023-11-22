package org.koreait.commons;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;

/*
    검증 오류코드 및 메시지 설정
 */

public class Utils {
    private static ResourceBundle validationsBundle;
    private static ResourceBundle errorsBundle;

    static {
        validationsBundle = ResourceBundle.getBundle("messages.validations");
        errorsBundle = ResourceBundle.getBundle("messages.errors");
    }

    public static String getMessage(String code, String bundleType) {
        bundleType = Objects.requireNonNullElse(bundleType, "validation");
        ResourceBundle bundle = bundleType.equals("error")? errorsBundle:validationsBundle;
        try {
            return bundle.getString(code);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, List<String>> getMessages(Errors errors){

        try {
            Map<String, List<String>> data = new HashMap<>();
            for (FieldError error : errors.getFieldErrors()) {   //key:field, value: 에러코드
                String field = error.getField();
                //에러코드 발생 시 NotBlank, NotBlank.email, NotBlank.requestJoin.email 등이 있다면 가장
                //정확한 에러코드가 가장 긴 코드일테니 긴 순서대로 정렬. 실제 메시지코드에서 조회, 코드가 null이 아니면
                //list형태로 반환
                List<String> messages = Arrays.stream(error.getCodes()).sorted(Comparator.reverseOrder())
                        .map(c -> getMessage(c, "validation"))
                        .filter(c -> c != null)
                        .toList();

                data.put(field, messages);
            }
            return data;
        } catch(Exception e){
            return null;
        }

    }
}
