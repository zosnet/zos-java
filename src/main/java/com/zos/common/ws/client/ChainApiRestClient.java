package com.zos.common.ws.client;

import com.google.gson.JsonElement;
import com.zos.common.ws.client.graphenej.models.*;
import com.zos.common.ws.client.graphenej.objects.Asset;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.operations.BaseOperation;

import java.util.List;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:39
 */
public interface ChainApiRestClient {
    /**
     * 查询Chain chainId
     *
     * @return
     */
    String getChainId();

    /**
     * 查询全局动态参数
     *
     * @return
     */
    DynamicGlobalProperties getDynamicGlobalProperties();

    /**
     * 获取交易费率
     *
     * @param operations 交易操作
     * @param feeAsset   费用资产
     */
    List<AssetAmount> getRequiredFees(List<BaseOperation> operations, Asset feeAsset);

    /**
     * 查询账户余额
     *
     * @param accountId 账户id
     * @param assetIds  资产id list
     * @return
     */
    List<AssetAmount> getAccountBalance(String accountId, List<String> assetIds);


    /**
     * 查询账户信息
     *
     * @param accountId 账户id
     * @return
     */
    JsonElement getAccountAttachInfo(String accountId, int type);



    /**
     * 根据名称获取公链账户信息
     *
     * @param accountName
     * @return
     */
    AccountProperties getAccountByName(String accountName);

    LookupAsset getAsset(String assetName);

    String isAuth(String userId, int type, String assetId, String authId);

    /**
     * 根据accountId查询公链账户信息
     *
     * @param accountIds
     * @return
     */
    List<AccountProperties> getAccounts(List<String> accountIds);

    /**
     * 根据区块高度获取区块信息
     *
     * @param blockHeight 区块高度
     * @return
     */
    Block getBlock(long blockHeight);

    /**
     * 根据区块高度获取区块信息 with trxids
     *
     * @param blockHeight 区块高度
     * @return
     */
    Block getBlockIds(long blockHeight);

    /**
     * 查询oject
     * 1.2.* 账号
     * 1.3.* 资产
     * 2.1.0 最新区块
     *
     * @param objectIds
     * @return
     */
    JsonElement getObjects(List<String> objectIds);


    JsonElement getLocktokens(List<String> objectIds);

    JsonElement verifyTransaction(JsonElement json);

    JsonElement verifyByTxid(String txid);

    JsonElement verifyByTxidBlockHeight(long blockNum,String txid);

}
