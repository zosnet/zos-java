package com.zos.common.ws.client.impl;

import com.google.gson.JsonElement;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.ChainClientFactory;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.constant.WSConstants;
import com.zos.common.ws.client.graphenej.models.DynamicGlobalProperties;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.*;
import com.zos.common.ws.client.graphenej.operations.BaseOperation;
import com.zos.common.ws.client.graphenej.operations.TransferOperation;
import com.zos.common.ws.client.graphenej.operations.TransferOperationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;

@Slf4j
public class lenderTest {
    ChainWebSocketClient client = ChainClientFactory.getInstance().newWebSocketClient("ws://47.75.107.157:8290");

    ChainApiRestClient httpClient = ChainClientFactory.getInstance().newRestCLient("http://47.75.107.157:8290");

    /**
     * 转账交易
     *
     * @throws Exception
     */
    @Test
    public void transferOperation() throws Exception {

        //构建转账交易体
        UserAccount from = new UserAccount("1.2.162");//发起方
        String privateKey = "5JVWFZEPkXWtyPEvcWY1Y7oKTuSzhhc6hEEVR7ekZLr8VutQ7gD";//发起方私钥
        UserAccount to = new UserAccount("1.2.164");//接收方

        //转账交易体
        TransferOperation transferOperation =
                new TransferOperationBuilder().
                        setTransferAmount(new AssetAmount(99100000, WSConstants.ZOS_ASSET_ID)).//交易金额
                        setSource(from).
                        setDestination(to).
                        build();

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(transferOperation);
        Transaction transaction = new Transaction(privateKey, null, operations);

        //最新的区块信息
        transaction.setChainId(httpClient.getChainId());
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;
        transaction.setBlockData(new BlockData(headBlockNumber, headBlockId, expirationTime));

        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));
        WitnessResponse<JsonElement> response = client.broadcastTransaction(transaction);
        if (response.getError() == null) {
            log.debug("success~~~~~~");
        } else {
            log.debug("failed!!!!!!!");
            log.debug(response.getError().getData().getCode() + "");
        }
    }
}
