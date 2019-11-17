package com.zos.common.ws.client.api.domain;

import lombok.Data;

@Data
public class ZOSResponse<T> {
    private int code;
    private String msg;
    private T data;
    private Object expand;
}
