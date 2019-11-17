package com.zos.common.ws.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.zos.common.ws.client.ChainApiCallback;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.ChainWebSocketListener;
import com.zos.common.ws.client.exception.SocketConnectFailException;
import com.zos.common.ws.client.graphenej.Hash256;
import com.zos.common.ws.client.graphenej.RPC;
import com.zos.common.ws.client.graphenej.models.ApiCall;
import com.zos.common.ws.client.graphenej.models.BaseResponse;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.Transaction;
import com.zos.common.ws.client.util.BeanUtils;
import com.zos.common.ws.client.util.Config;
import com.zos.common.ws.client.util.SeqIncr;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.commons.lang3.StringUtils;

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
public class ChainWebSocketClientImpl implements ChainWebSocketClient {
    private String wsUrl;
    private OkHttpClient client;

    private WebSocket webSocket;

    private ChainWebSocketListener listener;

    //private  oprtzyion

    private boolean isConnect = false;

    private boolean isLogin = false;

    private boolean isCallBack;
    /**
     * 广播api id
     */
    private Integer broadcastApiId;



    Map<Integer, Response> socketMap = new HashMap();
    //为是否callback准备
    Map<Integer, Boolean> socketbcMap = new HashMap();

    private BroadcastCallBack broadcastCallBack = new BroadcastCallBack();

    public ChainWebSocketClientImpl(String wsUrl) {
        Dispatcher d = new Dispatcher();
        d.setMaxRequestsPerHost(100);
        this.client = new OkHttpClient.Builder().dispatcher(d).pingInterval(60, TimeUnit.SECONDS).build();
        this.wsUrl = wsUrl;
    }

    @Override
    public void close() {
        final int code = 1000;
        if(listener!=null){
            listener.onClosing(webSocket, code, null);
            webSocket.close(code, null);
            listener.onClosed(webSocket, code, null);
            client.dispatcher().executorService().shutdown();
        }
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
        broadcastApiId = null;
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
     * 发送withdraw
     */
    private Response sendWithdraw(JSONObject json,int id) {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }
        if (!isLogin) {
            login();
        }
        if (broadcastApiId == null) {
            networkBroadcast();
        }

        if (webSocket == null) {
            throw new NullPointerException("webSocket is null,create webSocket first please");
        }

        Response response = new Response();
//        int id = SeqIncr.next();
        System.out.println("id========="+id);
        json.put("id", id);
        socketMap.put(id, response);
        socketbcMap.put(id,!isCallBack);
        System.out.println("%%%%%%%%%%%%"+json.toJSONString());
        boolean isSuccess = webSocket.send(json.toString());
        if (!isSuccess) {
            throw new SocketConnectFailException("connect Chain node fail");
        }
        latchAwait(response.latch);
        socketMap.remove(id);
        return response;
    }
    /**
     * 发送withdraw
     */
    private Response sendWithdrawNoCB(JSONObject json,int id) {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }
        if (!isLogin) {
            login();
        }
        if (broadcastApiId == null) {
            networkBroadcast();
        }

        if (webSocket == null) {
            throw new NullPointerException("webSocket is null,create webSocket first please");
        }

        Response response = new Response();
//        int id = seqIncr();
        System.out.println("id========="+id);
        json.put("id", id);
        socketbcMap.put(id,true);
        socketMap.put(id, response);
        System.out.println("^^^^^^^^^^"+json.toJSONString());

        boolean isSuccess = webSocket.send(json.toString());
        if (!isSuccess) {
            throw new SocketConnectFailException("connect Chain node fail");
        }
        latchAwait(response.latch);
        socketMap.remove(id);
        return response;
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

    private Response verify(JSONObject json) {
        json.put("id", 4);
        log.debug("-----json-----:::" + json.toString());
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }
        if (webSocket == null) {
            throw new NullPointerException("webSocket is null,create webSocket first please");
        }
        Response response = new Response();
        socketMap.put(4, response);
        boolean isSuccess = webSocket.send(json.toString());
        if (!isSuccess) {
            throw new SocketConnectFailException("connect Chain node fail");
        }
        latchAwait(response.latch);
        socketMap.remove(4);
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
//            if (this.wsUrl == null || this.wsUrl.isEmpty()) {
//                this.wsUrl = Config.getConfigProperties("wsUrl");
//            }
            createNewWebSocket(this.wsUrl, new ChainWebSocketListener<>(callback, new TypeToken<WitnessResponse<JsonElement>>() {
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
    private void networkBroadcast() {
        synchronized (this) {
            if (broadcastApiId != null) {
                return;
            }
            ArrayList<Serializable> emptyParams = new ArrayList<>();
            ApiCall apiCall = new ApiCall(1, RPC.CALL_NETWORK_BROADCAST, emptyParams, RPC.VERSION, 3);
            send(apiCall);
        }
    }

    /**
     * 发送广播
     *
     * @param blockTransaction
     */
    private Response broadcastWithCallback2(Transaction blockTransaction) {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }

        if (!isLogin) {
            login();
        }
        if (broadcastApiId == null) {
            networkBroadcast();
        }

        ArrayList<Serializable> params = new ArrayList<>();
        int id = SeqIncr.next();
//        params.add(id);
        params.add(blockTransaction);

        ApiCall apiCall = new ApiCall(broadcastApiId, RPC.GET_TRANSACTION_HASH, params, RPC.VERSION, id);
        log.debug("get_transaction_hash:::" + apiCall.toJsonString());
        return send(apiCall);
    }

    /**
     * 发送广播
     *
     * @param blockTransaction
     */
    private Response broadcastWithCallback(Transaction blockTransaction) {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }

        if (!isLogin) {
            login();
        }
        if (broadcastApiId == null) {
            networkBroadcast();
        }

        ArrayList<Serializable> params = new ArrayList<>();
        int id = SeqIncr.next();
        params.add(id);
        params.add(blockTransaction);
        blockTransaction.setsignatures(Hash256.hash);

        ApiCall apiCall = new ApiCall(broadcastApiId, RPC.BROADCAST_TRANSACTION_WITH_CALLBACK, params, RPC.VERSION, id);
        log.debug("-----apiCall-----:::" + apiCall.toJsonString());
        return send(apiCall);
    }

    /**
     * 发送广播
     *
     * @param blockTransaction
     */
    private Response broadcast(Transaction blockTransaction) {
        if (!isConnect) {
            this.connect(broadcastCallBack);
        }

        if (!isLogin) {
            login();
        }
        if (broadcastApiId == null) {
            networkBroadcast();
        }

        ArrayList<Serializable> params = new ArrayList<>();
        int id = SeqIncr.next();
//        params.add(id);
        params.add(blockTransaction);
        blockTransaction.setsignatures(Hash256.hash);

        ApiCall apiCall = new ApiCall(broadcastApiId, RPC.BROADCAST_TRANSACTION, params, RPC.VERSION, id);
        log.debug("-----apiCall-----:::" + apiCall.toJsonString());
        return send(apiCall);
    }
    @Override
    public WitnessResponse<JsonElement> get_transaction_hash(Transaction blockTransaction) {
        Response response = broadcastWithCallback2(blockTransaction);
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }
    @Override
    public WitnessResponse<JsonElement> send(JSONObject json,int id,boolean isCallBack) {
        this.isCallBack = isCallBack;
        Response response = null;
//        if(isCallBack){
//            response = sendWithdraw(json,id);
//        }else {
//            response = sendWithdrawNoCB(json,id);
//        }
        response = sendWithdraw(json,id);
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }
    @Override
    public WitnessResponse<JsonElement> sendNoCB(JSONObject json,int id) {
        Response response = sendWithdrawNoCB(json,id);
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }

    @Override
    public WitnessResponse<JsonElement> verifyTransaction(JSONObject json) {
        Response response = verify(json);
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }

    @Override
    public WitnessResponse<JsonElement> broadcastTransaction(Transaction blockTransaction) {
        Response response = broadcastWithCallback(blockTransaction);
        if (response.witnessResponse.getId() <= 6) {
            response.witnessResponse.error = new BaseResponse.Error("time out, broadcast fail");
        }
        return response.witnessResponse;
    }
    @Override
    public WitnessResponse<JsonElement> broadcastTransactionNoCB(Transaction blockTransaction) {
        Response response = broadcast(blockTransaction);
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
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("阻塞超时:{}", e);
        }
    }

//    private int seqIncr() {
//        synchronized (this) {
//            if (Integer.MAX_VALUE == seq) {
//                seq = 7;
//            } else {
//                seq++;
//            }
//            return seq;
//        }
//    }

    private class Response {
        WitnessResponse<JsonElement> witnessResponse = new WitnessResponse();
        CountDownLatch latch = new CountDownLatch(1);
    }

    private class BroadcastCallBack implements ChainApiCallback<WitnessResponse<JsonElement>> {

        @Override
        public void onResponse(WebSocket webSocket, WitnessResponse<JsonElement> witnessResponse) {
            //如果是操作，with_callback会有两次返回，第一次是广播成功，第二次是一个块后，返回txid、block_num、trx_把第一次拦截
            if (witnessResponse.getId() > 6) {
                log.info(witnessResponse.getId() + " broadcast success");
                if(witnessResponse.getResult()==null){

                    Response response = socketMap.get((int) witnessResponse.getId());
                    BeanUtils.copyProperties(witnessResponse, response.witnessResponse);
//                    Hash256.hash = witnessResponse.result.toString();
                    response.latch.countDown();
                    return;
                }else {
                    Response response = socketMap.get((int) witnessResponse.getId());
                    BeanUtils.copyProperties(witnessResponse, response.witnessResponse);
                    Hash256.hash = witnessResponse.result.toString();
                    System.out.println(witnessResponse.getId());
                    if(socketbcMap.get((int)witnessResponse.getId())){
                        response.latch.countDown();
                    }

                }
                return;
            }
            if (witnessResponse.getId() == 0) {
                log.debug("******************" + witnessResponse.getParams().toString());

                if (StringUtils.strip((witnessResponse.getMethod().toString()), "\"").equals("notice")) {
                    String params = witnessResponse.getParams().getAsJsonArray().toString();
                    JSONArray paramsArray = JSON.parseArray(params);
                    int id = paramsArray.getInteger(0);
                    witnessResponse.setId(id);
                    log.info(id + "");
                }
            }

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
                broadcastApiId = witnessResponse.getResult().getAsInt();
                response.latch.countDown();
            } else if (witnessResponse.getId() == 4) {//验证
                response.latch.countDown();
            } else if (witnessResponse.getId() > 6) {
                response.latch.countDown();
            }
        }
    }
}
