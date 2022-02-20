package io.my.image.common;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BaseResponse<T> {
    private int code;
    private String result;
    private T returnValue;

    public BaseResponse() {
        this.result = "성공";
    }

    public BaseResponse(T t) {
        this();
        returnValue = t;
    }
}
