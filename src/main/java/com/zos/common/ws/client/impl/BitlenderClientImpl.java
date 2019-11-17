package com.zos.common.ws.client.impl;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.zos.common.ws.client.BitlenderClient;
import com.zos.common.ws.client.ChainApiCallback;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.ChainWebSocketListener;
import com.zos.common.ws.client.exception.SocketConnectFailException;
import com.zos.common.ws.client.graphenej.RPC;
import com.zos.common.ws.client.graphenej.models.ApiCall;
import com.zos.common.ws.client.graphenej.models.BaseResponse;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.Transaction;
import com.zos.common.ws.client.util.BeanUtils;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Chain webSocket client
 *
 * @author liruobin
 * @since 2018/7/3 上午10:14
 */
@AllArgsConstructor
@Slf4j
public class BitlenderClientImpl implements BitlenderClient {
    private String wsUrl;
    private OkHttpClient client;

    private WebSocket webSocket;

    private ChainWebSocketListener listener;

    //private  oprtzyion

    private boolean isConnect = false;

    private boolean isLogin = false;
    /**
     * 广播api id
     */
    private Integer lenderApiId;

    private int seq = 6;

    Map<Integer, Response> socketMap = new HashMap();

    private BroadcastCallBack broadcastCallBack = new BroadcastCallBack();

    public BitlenderClientImpl(String wsUrl) {
        Dispatcher d = new Dispatcher();
        d.setMaxRequestsPerHost(100);
        this.client = new OkHttpClient.Builder().dispatcher(d).pingInterval(60, TimeUnit.SECONDS).build();
        this.wsUrl = wsUrl;
    }

    @Override
    public void close() {
        final int code = 1000;
        listener.onClosing(webSocket, code, null);
        webSocket.close(code, null);
        listener.onClosed(webSocket, code, null);
        client.dispatcher().executorService().shutdown();
        webSocket = null;
        listener = null;
        client = null;
        socketMap = null;
        broadcastCallBack = null;
    }

    @Override
    public void resetConnect() {
        final int code = 1000;
        isConnect = false;
        isLogin = false;
        lenderApiId = null;
        listener.onClosing(webSocket, code, null);
        webSocket.close(code, null);
        listener.onClosed(webSocket, code, null);
    }

    /**
     * 创建socket连接
     *
     * @param wsUrl
     * @param listener
     * @return
     */
    private void createNewWebSocket(String wsUrl, ChainWebSocketListener<?> listener) {
        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = client.newWebSocket(request, listener);
        this.listener = listener;
        this.isConnect = true;
    }

    /**
     * 发送
     *
     * @param apiCall
     */
    private Response send(ApiCall apiCall) {
        if (webSocket == null) {
            throw new NullPointerException("webSocket is null,create webSocket first please");
        }
        Response response = new Response();
        socketMap.put(apiCall.sequenceId, response);
        boolean isSuccess = webSocket.send(apiCall.toJsonString());
        if (!isSuccess) {
            throw new SocketConnectFailException("connect Chain node fail");
        }
        latchAwait(response.latch);
        socketMap.remove(apiCall.sequenceId);
        return response;
    }

    /**
     * 创建socket连接
     *
     * @param callback
     */
    private void connect(ChainApiCallback<WitnessResponse<JsonElement>> callback) {
        synchronized (this) {
            if (isConnect) {
                return;
            }
            createNewWebSocket(wsUrl, new ChainWebSocketListener<>(callback, new TypeToken<WitnessResponse<JsonElement>>() {
            }.getType()));
        }
    }

    /**
     * 登录
     */
    private void login() {
        synchronized (this) {
            if (isLogin) {
                return;
            }
            ArrayList<Serializable> loginParams = new ArrayList<>();
            loginParams.add("");//用户名 默认为空
            loginParams.add("");//密码 默认为空
            ApiCall apiCall = new ApiCall(1, RPC.CALL_LOGIN, loginParams, RPC.VERSION, 1);
            send(apiCall);
        }
    }

    /**
     * 获取network broadcast 的 api id
     */
    private void lenderBroadcast() {
        synchronized (this) {
            if (lenderApiId != null) {
                return;
            }
            ArrayList<Serializable> emptyParams = new ArrayList<>();
            ApiCall apiCall = new ApiCall(1, RPC.CALL_BITLENDER, emptyParams, RPC.VERSION, 3);
            send(apiCall);
        }
    }

    /**
     * 发送广播
     *
     * @param
     */
    private Response broadcastWithCallback() {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }

        if (!isLogin) {
            login();
        }
        if (lenderApiId == null) {
            lenderBroadcast();
            log.info("----apiid:----"+lenderApiId);
        }

        ArrayList<Serializable> params = new ArrayList<>();
        ArrayList<Serializable> params1 = new ArrayList<>();
        ArrayList<Serializable> params2 = new ArrayList<>();
        int id = seqIncr();
        params1.add("1.3.20");
        params2.add("1.3.0");

        params.add(params1);
        params.add(params2);
        params.add(0);
        params.add(0);
        params.add(1);

        ApiCall apiCall = new ApiCall(lenderApiId, RPC.LIST_BITLENDER_ORDER, params, RPC.VERSION, id);
        log.debug("-----apiCall-----:::" + apiCall.toJsonString());
        return send(apiCall);
    }

    @Override
    public WitnessResponse<JsonElement> listBitlenderOrder() {
        Response response = broadcastWithCallback();
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }

    /**
     * 最多阻塞4秒
     *
     * @param latch
     */
    private void latchAwait(CountDownLatch latch) {
        try {
            latch.await(4, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("阻塞超时:{}", e);
        }
    }

    private int seqIncr() {
        synchronized (this) {
            if (Integer.MAX_VALUE == seq) {
                seq = 7;
            } else {
                seq++;
            }
            return seq;
        }
    }

    private class Response {
        WitnessResponse<JsonElement> witnessResponse = new WitnessResponse();
        CountDownLatch latch = new CountDownLatch(1);
    }

    private class BroadcastCallBack implements ChainApiCallback<WitnessResponse<JsonElement>> {

        @Override
        public void onResponse(WebSocket webSocket, WitnessResponse<JsonElement> witnessResponse) {
            log.info(WsGsonUtil.toJson(witnessResponse));
            Response response = socketMap.get((int) witnessResponse.getId());
            BeanUtils.copyProperties(witnessResponse, response.witnessResponse);
            if (witnessResponse.error != null) {
                log.error(WsGsonUtil.toJson(witnessResponse.error));
                response.latch.countDown();
                return;
            }
            if (witnessResponse.getId() == 1) {
                isLogin = true;
                response.latch.countDown();
            } else if (witnessResponse.getId() == 3) {
                lenderApiId = witnessResponse.getResult().getAsInt();
                response.latch.countDown();
            } else if (witnessResponse.getId() > 6) {
                log.info("----result:----"+witnessResponse.getResult());
                log.info(witnessResponse.getId() + " broadcast success");
                response.latch.countDown();
            }
        }
    }
}
