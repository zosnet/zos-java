package com.zos.common.ws.client.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.core.ChainApiFactory;
import com.zos.common.ws.client.exception.ChainApiException;
import com.zos.common.ws.client.graphenej.RPC;
import com.zos.common.ws.client.graphenej.interfaces.JsonSerializable;
import com.zos.common.ws.client.graphenej.models.*;
import com.zos.common.ws.client.graphenej.objects.Asset;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.operations.BaseOperation;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author paul
 * @since 2019/7/25 上午10:39
 */
@Slf4j
public class ChainApiRestClientImpl implements ChainApiRestClient {

    private ChainApiService apiService;

    private Integer bitlenderApiId;

    private String chainId;

    public ChainApiRestClientImpl(String url) {
        apiService = ChainApiFactory.builder().baseUrl(url).build().newApi(ChainApiService.class);
    }

    private String execute(ApiCall apiCall) {
        try {
            WitnessResponse<JsonElement> response = apiService.call(apiCall).execute().body();
            if (response.error != null) {
                throw new ChainApiException(response.error);
            }

            return WsGsonUtil.toJson(response.getResult());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ChainApiException(e);
        }
    }

    @Override
    public String getChainId() {
        if (StringUtils.isNotBlank(chainId)) {
            return chainId;
        }
        ArrayList<Serializable> emptyParams = new ArrayList<>();
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_CHAIN_ID, emptyParams, RPC.VERSION, 0);
        return execute(apiCall).replace("\"", "");
    }

    @Override
    public DynamicGlobalProperties getDynamicGlobalProperties() {
        ArrayList<Serializable> emptyParams = new ArrayList<>();
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_DYNAMIC_GLOBAL_PROPERTIES, emptyParams, RPC.VERSION, 0);
        return (DynamicGlobalProperties) WsGsonUtil.fromJson(execute(apiCall), DynamicGlobalProperties.class);
    }

    @Override
    public List<AssetAmount> getRequiredFees(List<BaseOperation> operations, Asset feeAsset) {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        accountParams.add((Serializable) operations);
        accountParams.add(feeAsset.getObjectId());
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_REQUIRED_FEES, accountParams, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), new TypeToken<List<AssetAmount>>() {
        }.getType());

    }


    @Override
    public List<AssetAmount> getAccountBalance(String accountId, List<String> assetIds) {
        ArrayList<Serializable> params = new ArrayList<>();
        ArrayList<Serializable> assetList = new ArrayList<>();
        assetList.addAll(assetIds);
        params.add(accountId);
        params.add(assetList);
        ApiCall apiCall = new ApiCall(0, RPC.GET_ACCOUNT_BALANCES, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), new TypeToken<List<AssetAmount>>() {
        }.getType());
    }


    @Override
    public JsonElement getAccountAttachInfo(String accountId, int type) {
        ArrayList<Serializable> params = new ArrayList<>();
        params.add(accountId);
        params.add(type);
        ApiCall apiCall = new ApiCall(0, RPC.GET_ACCOUNT_ATTACHINFO, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);
    }

    @Override
    public AccountProperties getAccountByName(String accountName) {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        accountParams.add(accountName);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_ACCOUNT_BY_NAME, accountParams, RPC.VERSION, 0);
        String ret = execute(apiCall);
        System.out.println(ret);
        return WsGsonUtil.fromJson(ret, AccountProperties.class);
    }

    @Override
    public LookupAsset getAsset(String assetName) {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        ArrayList<Serializable> assetNameList = new ArrayList<>();
        assetNameList.add(assetName);
        accountParams.add(assetNameList);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_LOOKUP_ASSET_SYMBOLS, accountParams, RPC.VERSION, 0);
        String ret = execute(apiCall);
        String newStr = ret.substring(1, ret.length() - 1);
        System.out.println(ret);
        return WsGsonUtil.fromJson(newStr, LookupAsset.class);
    }

    @Override
    public String isAuth(String userId, int type, String assetId, String authId) {
        ArrayList<Serializable> msg = new ArrayList<>();
        msg.add(userId);
        msg.add(type);
        msg.add(assetId);
        msg.add(authId);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_AUTH, msg, RPC.VERSION, 0);
        String ret;
        try {
            ret = execute(apiCall);
        } catch (Exception e) {
            ret = "0";
        }
        return ret;
    }

    @Override
    public List<AccountProperties> getAccounts(List<String> accountIds) {
        ArrayList<Serializable> params = new ArrayList<>();
        ArrayList<Serializable> accountIdList = new ArrayList<>();
        for (String account : accountIds) {
            accountIdList.add(account);
        }
        params.add(accountIdList);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_ACCOUNTS, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), new TypeToken<List<AccountProperties>>() {
        }.getType());
    }

    @Override
    public Block getBlock(long blockHeight) {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        accountParams.add(blockHeight);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_BLOCK, accountParams, RPC.VERSION, 0);
        System.out.println("block=="+execute(apiCall));
        Block block = WsGsonUtil.fromJson(execute(apiCall), Block.class);
//        System.out.println("transactions=="+block.transactions[0].toJsonString());

        return WsGsonUtil.fromJson(execute(apiCall), Block.class);
    }

    @Override
    public Block getBlockIds(long blockHeight) {
        ArrayList<Serializable> accountParams = new ArrayList<>();
        accountParams.add(blockHeight);
        ApiCall apiCall = new ApiCall(0, RPC.CALL_GET_BLOCK_IDS, accountParams, RPC.VERSION, 0);
//        System.out.println("block=="+execute(apiCall));
//        Block block = WsGsonUtil.fromJson(execute(apiCall), Block.class);
//        System.out.println("transactions=="+block.transactions[0].toJsonString());
        String blockStr = execute(apiCall);
        log.info("获取区块高度[{}]的区块信息{}",blockHeight,blockStr);
        return WsGsonUtil.fromJson(blockStr, Block.class);
    }


    @Override
    public JsonElement getObjects(List<String> objectIds) {
        if (objectIds == null || objectIds.isEmpty()) {
            return null;
        }
        ArrayList<Serializable> params = new ArrayList<>();
        ArrayList<Serializable> params2 = new ArrayList<>();
        for (String objectId : objectIds) {
            params2.add(objectId);
        }
        params.add(params2);

        ApiCall apiCall = new ApiCall(0, RPC.GET_OBJECTS, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);
    }

    @Override
    public JsonElement getLocktokens(List<String> objectIds) {
        ArrayList<Serializable> params = new ArrayList<>();
        for(String param:objectIds){
            params.add(param);
        }
//        params.add(objectIds.get(0));//uid
//        params.add(objectIds.get(1));
//        params.add(objectIds.get(2));
        ApiCall apiCall = new ApiCall(0, RPC.LOCKTOKENS, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);

    }

    @Override
    public JsonElement verifyTransaction(JsonElement json) {
        ArrayList<Serializable> params = new ArrayList<>();
        JsonSerializable x = (JsonSerializable) json;
        params.add(x);

        ApiCall apiCall = new ApiCall(0, RPC.GET_OBJECTS, params, RPC.VERSION, 0);
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);
    }

    @Override
    public JsonElement verifyByTxid(String txid){
        ArrayList<Serializable> params = new ArrayList<>();
        params.add(0);//高度
        params.add(txid);

        ApiCall apiCall = new ApiCall(0, RPC.VERTIFY_TXID, params, RPC.VERSION, 0);
//        System.out.println("$$$$$"+execute(apiCall));
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);
    }

    @Override
    public JsonElement verifyByTxidBlockHeight(long blockNum,String txid){
        ArrayList<Serializable> params = new ArrayList<>();
        params.add(blockNum);//高度
        params.add(txid);

        ApiCall apiCall = new ApiCall(0, RPC.VERTIFY_TXID, params, RPC.VERSION, 0);
        String exeReturn = execute(apiCall);
        if(exeReturn==null||"null".equals(exeReturn)){
            return null;
        }
        return WsGsonUtil.fromJson(execute(apiCall), JsonElement.class);
    }
}
