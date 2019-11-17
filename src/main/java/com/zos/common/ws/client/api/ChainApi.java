package com.zos.common.ws.client.api;


import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.api.domain.DepositHexRet;
import com.zos.common.ws.client.api.domain.ZOSResponse;
import com.zos.common.ws.client.graphenej.Address;
import com.zos.common.ws.client.graphenej.PublicKey;
import com.zos.common.ws.client.graphenej.models.AccountProperties;
import com.zos.common.ws.client.graphenej.models.Block;
import com.zos.common.ws.client.graphenej.models.DynamicGlobalProperties;
import com.zos.common.ws.client.graphenej.objects.Memo;
import com.zos.common.ws.client.graphenej.objects.OptionalMemoInfo;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by paul on 2019/08/01.
 */
@Slf4j
public class ChainApi {


    private String ws;
    private String http;
    ZosClient zosClient;
    public ChainApi(String httpUrl, String wsUrl){
        this.http = httpUrl;
        this.ws = wsUrl;
        zosClient = new ZosClient(http,ws);
    }

    public ChainApi(String chainId){
        zosClient = new ZosClient(chainId);
    }


    /**
     * 多转账事务
     * @param from
     * @param priKey
     * @param list
     * @param iscb is the transferTransaction callback
     * @return
     * @throws Exception
     */
    public ZOSResponse transferTransaction(String from, String priKey, List<OptionalMemoInfo> list, boolean iscb) throws Exception{
        DepositHexRet depositHexRet = zosClient.transferHex(from,priKey,list);

        log.info("HexRet[{}]",depositHexRet.toString());
        log.info("HexRet[{}]",depositHexRet.getJson());
        ZOSResponse zosResponse =zosClient.setIsCB(iscb).send(depositHexRet.getJson());
        zosResponse.setExpand(depositHexRet.getTxid());
        return zosResponse;
    }

    /**
     * 离线签名
     * @param from
     * @param priKey
     * @param list
     * @param headBlockNumber
     * @param headBlockId
     * @param expirationTime
     * @return
     */
    public DepositHexRet signOffline(String from, String priKey, List<OptionalMemoInfo> list, long headBlockNumber, String headBlockId, long expirationTime, long basicFee, BigDecimal byteFee){
        DepositHexRet depositHexRet = zosClient.transferHexOffline(from,priKey,list,headBlockNumber,headBlockId,expirationTime,basicFee,byteFee);

        log.info("HexRet[{}]",depositHexRet.toString());
        log.info("HexRet[{}]",depositHexRet.getJson());
        return  depositHexRet;
    }


    /**
     * 对签名完成报文进行发送
     * @param json
     * @param iscb
     * @return
     * @throws Exception
     */
    public ZOSResponse sendTransfer(String json,boolean iscb) throws Exception{
        ZOSResponse zosResponse =zosClient.setIsCB(iscb).send(json);
        zosResponse.setExpand(json);
        return zosResponse;
    }


    /**
     * 创建memo
     * @param from
     * @param fromPriKey
     * @param to
     * @param message
     * @return
     * @throws Exception
     */
    public Memo buildMemo(String from,String fromPriKey,String to,String message)throws Exception{
        AccountProperties fromAccountProperties = zosClient.getAccountById(from);
        AccountProperties toAccountProperties = zosClient.getAccountById(to);

        Memo memo =  new Memo(new Address(fromAccountProperties.getMemoPublicKey()),
                new Address(toAccountProperties.getMemoPublicKey()),
                BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                Memo.encryptMessage(DumpedPrivateKey.fromBase58(null, fromPriKey).getKey(),
                        new Address(toAccountProperties.getMemoPublicKey()).getPublicKey(),
                        BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                        message));
        return memo;
    }

    /**
     * 创建Memo，使用Addres，外部记录address，减少与链交互，建议用此接口
     * @param fromAddress
     * @param fromPriKey
     * @param toAddress
     * @param message
     * @return
     * @throws Exception
     */
    public Memo buildMemoWithAddress(String fromAddress,String fromPriKey,String toAddress,String message) throws Exception{
        Memo memo =  new Memo(new Address(fromAddress),
                new Address(toAddress),BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                Memo.encryptMessage(DumpedPrivateKey.fromBase58(null, fromPriKey).getKey(),
                        new Address(toAddress).getPublicKey(),
                        BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                        message));
        return memo;
    }


    /**
     * 创建Memo
     * @param fromPrivateKey 发送方私钥
     * @param toPublicKey 接收方memokey
     * @param message
     * @return
     * @throws Exception
     */
    public Memo buildMemo(ECKey fromPrivateKey, PublicKey toPublicKey,String message) throws Exception{

        Address fromAddress = new Address(ECKey.fromPublicOnly(fromPrivateKey.getPubKey()));
        Address toAddress =new Address(toPublicKey.getAddress());

        Memo memo =  new Memo(fromAddress,toAddress,BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                Memo.encryptMessage(fromPrivateKey,toPublicKey,
                BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),
                message));
        return memo;
    }

    /**
     * 根据json获取Account实体
     * @param json
     * @return
     */
    public AccountProperties getAccountByJson(String json){
        AccountProperties accountProperties = WsGsonUtil.fromJson(json, AccountProperties.class);
        return accountProperties;
    }


    /**
     * 解析memo值，充值用
     * @param priKey
     * @param sendPubKey
     * @param
     * @return
     * @throws Exception
     */
    public static String decryptMemo(String priKey, String sendPubKey,String memoJson) throws Exception{

        Memo memo = WsGsonUtil.fromJson(memoJson, Memo.class);

        return Memo.decryptMessage(DumpedPrivateKey.fromBase58(null, priKey).getKey(),new Address(sendPubKey),memo.getNonce(),memo.getByteMessage());

    }

    /**
     * 根据userid获取账户信息
     * @param userid
     * @return
     */
    public AccountProperties getAccountById(String userid){

        return zosClient.getAccountById(userid);
    }

    /**
     * 根据username获取账户信息
     * @param username
     * @return
     */
    public AccountProperties getAccountByName(String username){

        return zosClient.getAccountByName(username);
    }

    public String getbalance(String userid,String assetid){
        return (String) zosClient.balance(userid,assetid).getData();
    }


    /**
     * 根据高度获取块信息
     * @param height
     * @return
     */
    public Block getBlock(long height){
        return zosClient.getBlock(height);
    }


    /**
     * 获取链当前动态全局属性
     * @return
     */
    public DynamicGlobalProperties getDynamicGloba(){
        return zosClient.getDynamicGlobal();
    }


    /**
     * 获取链websocket接口
     * @return
     */
    public ChainWebSocketClient getWsClient(){
        return zosClient.getWsClient();
    }

    /**
     * 获取链http接口
     * @return
     */
    public ChainApiRestClient getHttpClient(){
        return zosClient.getHttpClient();
    }


    /**
     * 获取chainId
     * @return
     */
    public String getChainId(){
        return zosClient.getHttpClient().getChainId();
    }
    /**
     * 关闭
     */
    public void closeConn(){
        zosClient.closedConn();
    }



}
