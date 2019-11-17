package com.zos.common.ws.client.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.ChainClientFactory;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.graphenej.models.AccountProperties;
import com.zos.common.ws.client.graphenej.models.LookupAsset;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author liruobin
 * @since 2018/7/5 上午11:20
 */
@Slf4j
public class ChainApiRestClientImplTest {
    ChainApiRestClient client = ChainClientFactory.getInstance().newRestCLient("http://47.75.107.157:8290");

    @Test
    public void getChainId() throws Exception {
        log.info(client.getChainId());
    }

    @Test
    public void getDynamicGlobalProperties() throws Exception {
        log.info(WsGsonUtil.toJson(client.getDynamicGlobalProperties()));
    }

    @Test
    public void getAccountBalance() {
//        List<String> paramsList = new ArrayList<>();
//        paramsList.add("1.3.0");
        client.getAccountBalance("1.2.20", Collections.EMPTY_LIST);
    }

    @Test
    public void getAccountByName() {
        AccountProperties accountProperties = client.getAccountByName("gateway-eth");
        if (accountProperties == null) {
            System.out.println("xxx");
        } else {
            System.out.println(accountProperties.id);
        }
    }

    @Test
    public void isAuth() {
       String x = client.isAuth("1.2.162", 0, "1.3.100", "1.2.190");
       log.debug("xxxxxxx"+x);
    }


    @Test
    public void getAsset() {
        LookupAsset lookupAsset = client.getAsset("BTC");
        if (lookupAsset == null) {
            System.out.println("xxx");
        } else {
            System.out.println(lookupAsset.id);
            System.out.println(lookupAsset.precision);
        }
    }

    @Test
    public void getAccount() {
        List<AccountProperties> accountProperties = client.getAccounts(Arrays.asList("1.2.999"));
        System.out.println(accountProperties.get(0).getName());
//        System.out.println(accountProperties.size());
    }

    @Test
    public void getObjects() {
        JsonElement jsonElement = client.getObjects(Arrays.asList("1.3.20"));
        System.out.println(jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("real_asset"));
    }

    @Test
    public void verifyTransaction() {
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonElement json = jsonParser.parse("{\"ref_block_num\":59543,\"ref_block_prefix\":1864245504,\"expiration\":\"2018-11-06T03:07:10\",\"operations\":[[35,{\"fee\":{\"amount\":\"102402\",\"asset_id\":\"1.3.0\"},\"payer\":\"1.2.31\",\"required_auths\":[],\"id\":9999,\"data\":\"7b226368616e6e656c54797065223a342c226163636f756e745f6e6f223a223078313233313233222c226163636f756e744e616d65223a22222c22616d6f756e74223a302e3030312c22636f696e5f74797065223a22425443222c22757365724964223a226e617468616e227d3\"}]],\"extensions\":[],\"signatures\":[\"1f03d9ebefe9988aab1867e7931106c5f920768b16c8c7bac1b00a55ffd33dabfe08b82cbb19a252fded01ad009a50b8612b1e347af17b01eee190beaa5bf072aa\"]}");
        log.info(json + "");
        JsonElement jsonElement = client.verifyTransaction(json);
        System.out.println(jsonElement.toString());
    }

}