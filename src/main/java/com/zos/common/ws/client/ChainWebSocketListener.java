package com.zos.common.ws.client;

import com.google.gson.reflect.TypeToken;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.lang.reflect.Type;

@Slf4j
public class ChainWebSocketListener<T> extends WebSocketListener {
    private ChainApiCallback<T> callback;

    private Type eventClass;

    private TypeToken<T> typeToken;

    private boolean closing = false;


    public ChainWebSocketListener(ChainApiCallback<T> callback, Type eventClass) {
        this.callback = callback;
        this.eventClass = eventClass;
    }

    public ChainWebSocketListener(ChainApiCallback<T> callback) {
        this.callback = callback;
        this.typeToken = new TypeToken<T>() {
        };
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        log.info("----text----:"+text);
        T event;
        if (eventClass == null) {
            event = WsGsonUtil.fromJson(text, typeToken.getType());
        } else {
            event = WsGsonUtil.fromJson(text, eventClass);
        }
//        log.info("----event:----"+event.toString());
        callback.onResponse(webSocket, event);
    }

    @Override
    public void onClosing(final WebSocket webSocket, final int code, final String reason) {
        closing = true;
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (!closing) {
            callback.onFailure(t);
        }
    }
}
