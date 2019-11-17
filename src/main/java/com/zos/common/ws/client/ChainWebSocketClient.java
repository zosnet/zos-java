package com.zos.common.ws.client;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.Transaction;

/**
 * @author liruobin
 * @since 2018/7/3 上午10:13
 */
public interface ChainWebSocketClient {

    /**
     * 发送交易广播
     *
     * @param blockTransaction
     * @return
     */
    WitnessResponse<JsonElement> broadcastTransaction(Transaction blockTransaction);

    /**
     * 发送交易广播
     *  不适用callback
     * @param blockTransaction
     * @return
     */
    WitnessResponse<JsonElement> broadcastTransactionNoCB(Transaction blockTransaction);

    WitnessResponse<JsonElement> verifyTransaction(JSONObject jbroadcastTransactionson);

    WitnessResponse<JsonElement> send(JSONObject json,int id,boolean isCallBack);

    WitnessResponse<JsonElement> sendNoCB(JSONObject json,int id);

    WitnessResponse<JsonElement> get_transaction_hash(Transaction blockTransaction);
    /**
     * 重置连接
     */
    void resetConnect();

    void close();

}
