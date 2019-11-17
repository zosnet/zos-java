package com.zos.common.ws.client;


import okhttp3.WebSocket;

@FunctionalInterface
public interface ChainApiCallback<T> {

    void onResponse(WebSocket webSocket, T response);

    default void onFailure(Throwable cause) {
        cause.printStackTrace();
    }
}
