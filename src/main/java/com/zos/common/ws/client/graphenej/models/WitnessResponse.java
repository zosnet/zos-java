package com.zos.common.ws.client.graphenej.models;

import lombok.Data;

/**
 * Generic witness response
 */
@Data
public class WitnessResponse<T> extends BaseResponse {
    public static final String KEY_ID = "id";
    public static final String KEY_RESULT = "result";
    public static final String KEY_METHOD = "method";
//    public static final String KEY_ERROR = "error";

    public T result;
    public T method;
    public T params;
//    public T error;
}
