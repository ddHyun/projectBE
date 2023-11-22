package org.koreait.commons.rests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor @RequiredArgsConstructor
public class JSONData<T> {
    private boolean success = true;
    private HttpStatus status = HttpStatus.OK;
    @NonNull
    private T data;
    private String message; //success=false일 경우 에러메시지
}
