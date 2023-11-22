package org.koreait.commons.rests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor @RequiredArgsConstructor
public class JSONData {
    private boolean success = true;
    private HttpStatus status = HttpStatus.OK;
    @NonNull
    private Object data;
    private Object message; //success=false일 경우 에러메시지. 여러개일 경우를 대비해 Object 사용
}
