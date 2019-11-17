package com.zos.common.ws.client.impl;

import com.google.gson.JsonElement;
import com.zos.common.ws.client.BitlenderClient;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.ChainClientFactory;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.constant.WSConstants;
import com.zos.common.ws.client.graphenej.Hash256;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.models.DynamicGlobalProperties;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import com.zos.common.ws.client.graphenej.objects.*;
import com.zos.common.ws.client.graphenej.operations.*;
import com.zos.common.ws.client.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * @author liruobin
 * @since 2018/7/3 下午2:52
 */
@Slf4j
public class ChainWebSocketClientImplTest {
    ChainWebSocketClient client = ChainClientFactory.getInstance().newWebSocketClient("ws://47.75.107.157:8290");

    ChainApiRestClient httpClient = ChainClientFactory.getInstance().newRestCLient("http://47.75.107.157:8290");

    BitlenderClient bitlenderClient = ChainClientFactory.getInstance().lenderClient("ws://47.75.107.157:8290");

    /**
     * 转账交易
     *
     * @throws Exception
     */
    @Test
    public void transferOperation() throws Exception {

        //构建转账交易体
        //UserAccount from = new UserAccount("1.2.154");//发起方
        //String privateKey = "5Ji5xc17cKcs351LLSePpQrx84oFhyk6cM1FyYC3kG84ZJBHz12";//发起方私钥
        UserAccount from = new UserAccount("1.2.162");
        String privateKey = "P5JuUQ2qmZJTCJ3Hmwv5LiAr7Dgjz4WepDS7WGbTfYjnu";
        UserAccount to = new UserAccount("1.2.164");//接收方

        //转账交易体
        TransferOperation transferOperation =
                new TransferOperationBuilder().
                        setTransferAmount(new AssetAmount(99, WSConstants.ZOS_ASSET_ID)).//交易金额
                        setSource(from).
                        setDestination(to).
                        build();

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(transferOperation);
        Transaction transaction = new Transaction(privateKey,null, operations);

        //最新的区块信息
        transaction.setChainId(httpClient.getChainId());
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;
        transaction.setBlockData(new BlockData(headBlockNumber, headBlockId, expirationTime));

        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));
        String bufStrHex = Util.bytesToHex(transaction.toBytes());
        System.out.println("---toBytes:"+bufStrHex );
        System.out.println("---toJson:"+transaction.toJsonString() );
       /* WitnessResponse<JsonElement> response = client.broadcastTransaction(transaction);
        if (response.getError() == null) {
            log.debug("success~~~~~~");
            log.debug(response.getParams() + "");
        } else {
            log.debug("failed!!!!!!!");
            log.debug(response.getError().getData().getCode() + "");
        }*/
    }

    @Test
    public void diyOperation() throws Exception {
        String privateKey = "5KPavPm42d9Lq9LQHRYB6hwus9RVZFYeJKycHijjXExJUgRSyue";

        DiyOperation diyOperation = new DiyOperation();
        diyOperation.setD(0);
        diyOperation.setData("");
        diyOperation.setPayer(new UserAccount("1.2.393"));
        diyOperation.setRequiredAuths(new Extensions());

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(diyOperation);

        //最新的区块信息
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;

        Transaction transaction = new Transaction(privateKey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        //设置chainId
        String chainId = httpClient.getChainId();
        transaction.setChainId(chainId);
        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));

        client.broadcastTransaction(transaction);
    }

    @Test
    public void depositOperation() throws Exception {
        String privateKey = "5K4hfkYyJoaU7ossH7MZg9tgJC55pQysp8JjvFmHZ7EMg4S2zJu";
        DepositOperation depositOperation = new DepositOperation();
        depositOperation.setFrom(new UserAccount("1.2.100"));
        depositOperation.setTo(new UserAccount("1.2.123"));
        depositOperation.setDeposit(new AssetAmount(1000000000, WSConstants.CNY_ASSET_ID));
        depositOperation.setRequiredAuths(new Extensions());

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(depositOperation);

        //最新的区块信息
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;

        Transaction transaction = new Transaction(privateKey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        //设置chainId
        String chainId = httpClient.getChainId();
        transaction.setChainId(chainId);
        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));

        String bufStrHex = Util.bytesToHex(transaction.toBytes());
        String xx = Util.bytesToHex(transaction.getGrapheneSignature());
        String xxx = transaction.getGrapheneSignature2();
//
//        System.out.println("---toBytes:"+bufStrHex );
//        System.out.println("---toBytes2:"+xxx );
//        System.out.println("---sign:"+xx );

//        System.out.println("---toJson:"+transaction.toJsonString() );

        WitnessResponse<JsonElement> o = client.broadcastTransaction(transaction);
    }

    @Test
    public void certificationOperation() throws Exception {
        String privateKey = "5JEC3UN2WiemxxtJxBkAaDwP1JiyACxBaaQiQqf5RUHkqkCXN7b";
        AccountAuthorZos auth = new AccountAuthorZos("1.2.162",4,0,63072000);

        CertificationOperation certificationOperation = new CertificationOperation();
        certificationOperation.setIssuer(new UserAccount("1.2.179"));
        certificationOperation.setAuth_account(new Optional<AccountAuthorZos>(auth));
        certificationOperation.setOp_type(6);
        certificationOperation.setRequiredAuths(new Extensions());

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(certificationOperation);

        //最新的区块信息
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;

        Transaction transaction = new Transaction(privateKey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        //设置chainId
        String chainId = httpClient.getChainId();
        transaction.setChainId(chainId);
        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));

//        transaction.setsignatures("222ec9cf3e85c7176f4a7796b00e59880ccb09fca06bbe717d9f6555ee36bc93");
//        WitnessResponse<JsonElement> x = client.get_transaction_hash(transaction);

//        log.debug("*****"+x.getResult().toString());
//
//        transaction.setsignatures(Hash256.hash);
        WitnessResponse<JsonElement> o = client.broadcastTransaction(transaction);
    }

    @Test
    public void test() {
//        String privateKey = "5JybCsfWpSXT1MCJtuaVUbEgjJeqLfRbFyBDzpwwFJPdrenf4kV";
//
//        DepositOperation depositOperation = new DepositOperation();
//        depositOperation.setFrom(new UserAccount("1.2.100"));
//        depositOperation.setTo(new UserAccount("1.2.199"));
//        depositOperation.setDeposit(new AssetAmount(999, WSConstants.CNY_ASSET_ID));
//        depositOperation.setRequiredAuths(new Extensions());
//
//        ArrayList<BaseOperation> operations = new ArrayList<>();
//        operations.add(depositOperation);
//
//        //最新的区块信息
//        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
//        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
//        String headBlockId = dynamicProperties.head_block_id;
//        long headBlockNumber = dynamicProperties.head_block_number;
//
//        Transaction transaction = new Transaction(privateKey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
//        //设置chainId
//        String chainId = httpClient.getChainId();
//        transaction.setChainId(chainId);
//        //设置交易费用
//        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));
        log.info(Config.getConfigProperties("test"));
        WitnessResponse<JsonElement> o = bitlenderClient.listBitlenderOrder();
//        System.out.println(Util.bytesToHex(new byte[]{(byte) 44}));
//        System.out.println(Util.hexToBytes(16 + "")[0]);
    }
}
