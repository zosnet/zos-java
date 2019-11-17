package com.zos.common.ws.client;

import com.google.gson.JsonElement;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.Transaction;

public interface BitlenderClient {
    /**
     * 发送交易广播
     *
     * @param
     * @return
     */
//    WitnessResponse<JsonElement> broadcastTransaction(Transaction blockTransaction);


    WitnessResponse<JsonElement> listBitlenderOrder();
    /**
     * 重置连接
     */
    void resetConnect();

    void close();
}
