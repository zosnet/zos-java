package com.zos.common.ws.client.exception;

import com.zos.common.ws.client.graphenej.models.BaseResponse;
import lombok.Getter;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:20
 */
public class ChainApiException extends RuntimeException {
    @Getter
    private BaseResponse.Error error;

    public ChainApiException() {
        super();
    }


    public ChainApiException(String message) {
        super(message);
    }

    public ChainApiException(BaseResponse.Error error) {
        this.error = error;
    }

    public ChainApiException(Throwable cause) {
        super(cause);
    }


    public ChainApiException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        if (error != null) {
            return error.getMessage();
        }
        return super.getMessage();
    }
}
