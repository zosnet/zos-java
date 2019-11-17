package com.zos.common.ws.client.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.ChainClientFactory;
import com.zos.common.ws.client.ChainWebSocketClient;
import com.zos.common.ws.client.api.domain.ChainInfoRet;
import com.zos.common.ws.client.api.domain.DepositHexRet;
import com.zos.common.ws.client.api.domain.SendRet;
import com.zos.common.ws.client.api.domain.ZOSResponse;
import com.zos.common.ws.client.constant.WSConstants;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.models.*;
import com.zos.common.ws.client.graphenej.objects.*;
import com.zos.common.ws.client.graphenej.objects.Optional;
import com.zos.common.ws.client.graphenej.operations.*;
import com.zos.common.ws.client.util.Config;
import com.zos.common.ws.client.util.SeqIncr;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class ZosClient {

//    private String ws = Config.getConfigProperties("wsUrl");
//    private String http = Config.getConfigProperties("httpUrl");
    private String ws;
    private String http;
    private String chainId;
    private String assetId = "1.3.0";
    private String sendMethod;
    private boolean sendIsCB;
    public ChainWebSocketClient client;
//
    public ChainApiRestClient httpClient;

    public ZosClient(){
//        this.ws = Config.getConfigProperties("wsUrl");
//        this.http = Config.getConfigProperties("httpUrl");
    }



    public ChainWebSocketClient getWsClient(){
        return this.client;
    }
    public ChainApiRestClient getHttpClient(){
        return httpClient;
    }
    public ZosClient setIsCB(boolean iscb){
        sendIsCB = iscb;
        if(iscb){
            sendMethod = "broadcast_transaction_with_callback";
        }else {
            sendMethod = "broadcast_transaction";
        }
        return this;
    }
    public ZosClient(String httpUrl, String wsUrl){
        this.http = httpUrl;
        this.ws = wsUrl;
        client = ChainClientFactory.getInstance().newWebSocketClient(ws);
        httpClient = ChainClientFactory.getInstance().newRestCLient(http);
        this.chainId = httpClient.getChainId();
//        System.out.println(chainId);
    }
    public ZosClient(String chainId){
        this.chainId = chainId;
    }
    public void setAssetId(String assetId){
        this.assetId = assetId;
    }

    public void closedConn(){
        client.close();
    }

    public ZOSResponse kyc(String priKey, String authId, String userId) {

        String privateKey = priKey;
        AccountAuthorZos auth = new AccountAuthorZos(authId, 4, 0, 63072000);

        CertificationOperation certificationOperation = new CertificationOperation();
        certificationOperation.setIssuer(new UserAccount(userId));
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
        transaction.setChainId(chainId);
        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));

        WitnessResponse<JsonElement> o = client.broadcastTransaction(transaction);

        ZOSResponse zosResponse = new ZOSResponse();

        SendRet ret = getSendData(JSON.parseArray(o.getParams().toString()));

        zosResponse.setCode(1);
        zosResponse.setMsg("ok");
        zosResponse.setData(ret);

        return zosResponse;
    }

    public String getRealCoinType(String type) {
        log.debug(Config.getConfigProperties("httpUrl"));
        JsonElement jsonElement = httpClient.getObjects(Arrays.asList(type));
        String ret = StringUtils.strip(jsonElement.getAsJsonArray().get(0).getAsJsonObject().get("real_asset").toString(), "\"[]");
        return ret;
    }

    public AccountProperties getAccountByName(String name){
        AccountProperties accountProperties = httpClient.getAccountByName(name);
        return accountProperties;
    }
    public ZOSResponse getAccounts(String user) {
        log.debug(Config.getConfigProperties("httpUrl"));
        AccountProperties accountProperties = httpClient.getAccountByName(user);
        ZOSResponse zosResponse = new ZOSResponse();
        if (accountProperties == null) {
            zosResponse.setCode(0);
            zosResponse.setMsg("无此用户");
            System.out.println("无此用户");
        } else {
            zosResponse.setCode(1);
            zosResponse.setMsg("用户id：[" + accountProperties.id + "]");
            zosResponse.setData(accountProperties.id);
            System.out.println("用户id：[" + accountProperties.id + "]");
        }
        return zosResponse;
    }

    public ZOSResponse auth(String userId, int type, String assetId, String authId) {
        log.debug(Config.getConfigProperties("httpUrl"));
        String x = httpClient.isAuth(userId, type, assetId, authId);
        ZOSResponse zosResponse = new ZOSResponse();

        int ret = Integer.valueOf(x);
        if (ret > 0) {
            zosResponse.setCode(1);
            zosResponse.setData(ret);
        } else {
            zosResponse.setCode(0);
        }
        return zosResponse;
    }

    public ZOSResponse balance(String userId, String assetId) {
        String amount = "";
        log.debug(Config.getConfigProperties("httpUrl"));
        ArrayList<String> params = new ArrayList<>();
        List<AssetAmount> x = httpClient.getAccountBalance(userId, params);
        for (AssetAmount assetAmount : x) {
            System.out.println(assetAmount.getAsset().getId());
            if (assetAmount.getAsset().getId().equals(assetId)) {
                amount = assetAmount.getAmount() + "";
            }
        }
        ZOSResponse zosResponse = new ZOSResponse();
        zosResponse.setData(amount);
//        if (x.equals("1")) {
//            zosResponse.setCode(1);
//        } else {
//            zosResponse.setCode(0);
//        }
        return zosResponse;
    }

    public ZOSResponse asset(String coinType) {
        log.debug(Config.getConfigProperties("httpUrl"));
        LookupAsset x = httpClient.getAsset(coinType);
        ZOSResponse zosResponse = new ZOSResponse();
        zosResponse.setCode(1);
        zosResponse.setData(x);
        return zosResponse;
    }

    public String getNameById(String id) {
        List<AccountProperties> accountProperties = httpClient.getAccounts(Arrays.asList(id));
        System.out.println(accountProperties.get(0).getName());
        return accountProperties.get(0).getName();
    }

    public ZOSResponse send(String sign) throws Exception {
        ZOSResponse zosResponse = new ZOSResponse();
        int id = SeqIncr.next();
        String tempStr="";
        if(sendIsCB){
            tempStr = id+",";
        }

        String start = "{\"id\":"+id+ ",\"method\": \"call\",\"params\": [2, \""+sendMethod+"\", ["+tempStr;
        String end = "]],\"jsonrpc\": \"2.0\"}";

        String fin = start + sign + end;

        JSONObject json;
        SendRet ret = null;

        WitnessResponse<JsonElement> response = null;
        try {
            json = JSON.parseObject(fin);
            log.info(json.toJSONString());
            response = client.send(json,id,this.sendIsCB);

            log.info(response.toString());
        } catch (Exception e) {
            zosResponse.setCode(0);
            zosResponse.setData(e.getMessage());
            return zosResponse;
        }
        if(response.getError()!=null){
            zosResponse.setCode(0);
            zosResponse.setData(response.getError().message);
            return zosResponse;
        }
        if(response.getParams() == null){
            zosResponse.setCode(9);
            zosResponse.setData(response);
            return zosResponse;
        }

        ret = getSendData(JSON.parseArray(response.getParams().toString()));
        zosResponse.setCode(1);
        zosResponse.setMsg("ok");
        zosResponse.setData(ret);

        return zosResponse;
    }

//    public ZOSResponse sendNoCB(String sign) throws Exception {
//        log.info("-----client:-----" + client.toString());
//        ZOSResponse zosResponse = new ZOSResponse();
//        int id = SeqIncr.next();
//        String start = "{\"id\":"+id+ ",\"method\": \"call\",\"params\": [2, \"broadcast_transaction\", [";
//        String end = "]],\"jsonrpc\": \"2.0\"}";
//
//        String fin = start + sign + end;
//
//        JSONObject json;
//        try {
//            json = JSON.parseObject(fin);
//            log.debug(json.toJSONString());
//        } catch (Exception e) {
//            log.info("str2json出错");
//            log.info(e.toString());
//
//            zosResponse.setCode(1001);
//            return zosResponse;
//        }
//        SendRet ret = null;
//        WitnessResponse<JsonElement> response = null;
//        try {
//            response = client.sendNoCB(json,id);
//            log.debug(response.toString());
//        } catch (Exception e) {
//            zosResponse.setCode(0);
//            return zosResponse;
//        }
//
//        if(response.getParams() == null){
//            zosResponse.setCode(0);
//            zosResponse.setData(response);
//            return zosResponse;
//        }
//
//        ret = getSendData(JSON.parseArray(response.getParams().toString()));
//        zosResponse.setCode(1);
//        zosResponse.setMsg("ok");
//        zosResponse.setData(ret);
//
//        return zosResponse;
//    }

    public ZOSResponse verify(String sign) throws Exception {
        ZOSResponse zosResponse = new ZOSResponse();
        int id = SeqIncr.next();
        String start = "{\"id\":"+id+ ",\"method\": \"call\",\"params\": [0, \"vertify_transaction\", [";
        String end = "]],\"jsonrpc\": \"2.0\"}";

        String fin = start + sign + end;

        JSONObject json;
        try {
            json = JSON.parseObject(fin);
            log.debug(json.toJSONString());
        } catch (Exception e) {
            log.info("str2json出错");
            log.info(e.toString());

            zosResponse.setCode(1001);
            return zosResponse;
        }
        SendRet ret = null;
        WitnessResponse<JsonElement> response = null;
        try {
            response = client.send(json,id,true);
            log.debug(response.toString());
        } catch (Exception e) {
            zosResponse.setCode(0);
            return zosResponse;
        }

        if(response.getParams() == null){
            zosResponse.setCode(0);
            return zosResponse;
        }

        ret = getSendData(JSON.parseArray(response.getParams().toString()));
        zosResponse.setCode(1);
        zosResponse.setMsg("ok");
        zosResponse.setData(ret);

        return zosResponse;
    }

    /**
     * 转账交易
     *
     * @throws Exception
     */

    public ZOSResponse verifyTransaction(String sign, Class type) throws Exception {
        ZOSResponse zosResponse = new ZOSResponse();
        JSONObject json;
        try {
            json = JSON.parseObject(sign);
        } catch (Exception e) {
            log.info("str2json出错");
            log.info(e.toString());

            zosResponse.setCode(1001);
            return zosResponse;
        }
        WitnessResponse<JsonElement> response = client.verifyTransaction(json);
        String id = StringUtils.strip(response.getResult() + "", "\"[]");
        if (id.equals("null")) {
            log.info("验证未通过");
            zosResponse.setCode(1000);
            return zosResponse;
        }

        //取data值
        JSONObject data = getData(json);

        try {
            zosResponse.setData(JSONObject.toJavaObject(data, type));
        } catch (Exception e) {
            log.info("json2Bean出错");
            log.info(e.toString());
            zosResponse.setCode(1002);
            return zosResponse;
        }

        log.info("验证通过");
        zosResponse.setCode(1);
        zosResponse.setMsg(id);

        return zosResponse;
    }

    public ZOSResponse getChainInfo() {
        ZOSResponse zosResponse = new ZOSResponse();
        ChainInfoRet ret = new ChainInfoRet();

        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();

        long expiration = (dynamicProperties.time.getTime() / 1000) + 180;
        Date expirationTime = new Date(expiration * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(Util.TIME_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        String expirationDate = dateFormat.format(expirationTime);

        ret.setExpirationTime(expirationDate);
        ret.setHeadBlockId(dynamicProperties.head_block_id);
        ret.setHeadBlockNumber(dynamicProperties.head_block_number);
        ret.setChainId(chainId);

        zosResponse.setCode(1);
        zosResponse.setData(ret);

        return zosResponse;
    }

    private SendRet getSendData(JSONArray json) {
//        JSONArray params = json.getJSONArray("params");
        JSONArray array = (JSONArray) json.get(1);
        JSONObject obj = (JSONObject) array.get(0);
        String txId = obj.get("id").toString();
        String trxNum = obj.get("trx_num").toString();
        String blockNum = obj.get("block_num").toString();

        SendRet ret = new SendRet();
        ret.setBlockNum(blockNum);
        ret.setTrxNum(trxNum);
        ret.setTxId(txId);

        return ret;
    }

    private JSONObject getData(JSONObject json) {
        JSONArray params = json.getJSONArray("params");
        JSONArray array1 = (JSONArray) params.get(2);
        JSONObject obj1 = (JSONObject) array1.get(0);
        JSONArray array2 = obj1.getJSONArray("operations");
        JSONArray array3 = (JSONArray) array2.get(0);
        JSONObject obj2 = (JSONObject) array3.get(1);
        String dataStr = obj2.get("data").toString();
        JSONObject data = JSON.parseObject(hexStringToString(dataStr));
        return data;
    }

    public DepositHexRet depositHex(String from, String to, long amount, String assetId) {
        String privateKey = "5JybCsfWpSXT1MCJtuaVUbEgjJeqLfRbFyBDzpwwFJPdrenf4kV";

        DepositOperation depositOperation = new DepositOperation();
        depositOperation.setFrom(new UserAccount(from));
        depositOperation.setTo(new UserAccount(to));
        depositOperation.setDeposit(new AssetAmount(amount, assetId));
        depositOperation.setRequiredAuths(new Extensions());

        ArrayList<BaseOperation> operations = new ArrayList<>();
        operations.add(depositOperation);

        //最新的区块信息
        DynamicGlobalProperties dynamicProperties = httpClient.getDynamicGlobalProperties();
        long expirationTime = (dynamicProperties.time.getTime() / 1000) + Transaction.DEFAULT_EXPIRATION_TIME;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;

        Transaction transaction = new Transaction(privateKey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        transaction.setChainId(chainId);
        //设置交易费用
        transaction.setFees(httpClient.getRequiredFees(transaction.getOperations(), new Asset(WSConstants.ZOS_ASSET_ID)));

        String bufStrHex = transaction.getGrapheneSignature2();
        String json = transaction.toJsonString();

        System.out.println("---toBytes:" + bufStrHex);

        DepositHexRet ret = new DepositHexRet();
        ret.setHex(bufStrHex);
        ret.setJson(json);

        return ret;
    }

//    public DepositHexRet transferHex(String from, String prikey,String to, long amount) {
//        TransferOperation transferOperation = new TransferOperation(new UserAccount(from),new UserAccount(to),new AssetAmount(amount, assetId));
//
//        ArrayList<BaseOperation> operations = new ArrayList();
//        operations.add(transferOperation);
//        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
//        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
//        String headBlockId = dynamicProperties.head_block_id;
//        long headBlockNumber = dynamicProperties.head_block_number;
//
//
//        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
//        transaction.setChainId(chainId);
//        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));
//        String bufStrHex = transaction.getGrapheneSignature2();
//        String json = transaction.toJsonString();
////        System.out.println("---toBytes:" + bufStrHex);
//        DepositHexRet ret = new DepositHexRet();
//        ret.setHex(bufStrHex);
//        ret.setJson(json);
//        System.out.println("*******"+transaction.calculateTxid());
//        return ret;
//    }

    public DepositHexRet transferHex(String from, String prikey,List<OptionalMemoInfo> optionals){


        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;

        return this.transferHex(from,prikey,optionals,headBlockNumber,headBlockId,expirationTime);

    }

    public DepositHexRet transferHex(String from, String prikey,List<OptionalMemoInfo> optionals,
                                     long headBlockNumber,String headBlockId,long expirationTime){

        List<BaseOperation> operations = assemblyOperation(from,optionals);

        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        transaction.setChainId(chainId);
        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));

        transaction.getGrapheneSignature();
        String json = transaction.toJsonString();
        String txid = transaction.calculateTxid();
        DepositHexRet ret = new DepositHexRet();
        ret.setJson(json);
        ret.setTxid(txid);
        return ret;
    }

    public DepositHexRet transferHexOffline(String from, String prikey, List<OptionalMemoInfo> optionals,
                                            long headBlockNumber, String headBlockId, long expirationTime,
                                            long basicFee, BigDecimal byteFee){

        List<BaseOperation> operations = assemblyOperation(from,optionals);

        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        transaction.setChainId(chainId);
        List<AssetAmount> assetAmountList = new ArrayList<>();
        for(OptionalMemoInfo optionalMemoInfo:optionals){

            long byteFeeLong = 0;
            if(optionalMemoInfo.getMemo()!=null){
                byteFeeLong = (new BigDecimal(optionalMemoInfo.getMemo().getByteMessage().length)).multiply(byteFee).longValue();
            }
            long grossFee = byteFeeLong+basicFee;
            assetAmountList.add(new AssetAmount(grossFee,assetId));
        }

        transaction.setFees(assetAmountList);

        transaction.getGrapheneSignature();
        String json = transaction.toJsonString();
        String txid = transaction.calculateTxid();
        DepositHexRet ret = new DepositHexRet();
        ret.setJson(json);
        ret.setTxid(txid);
        return ret;
    }


    public List<BaseOperation> assemblyOperation(String from,List<OptionalMemoInfo> optionals){
        ArrayList<BaseOperation> operations = new ArrayList();

        for(OptionalMemoInfo optionalMemoInfo:optionals){

            TransferOperation transferOperation = null;
            if(optionalMemoInfo.getMemo()==null){
                transferOperation = new TransferOperation(new UserAccount(from),
                        new UserAccount(optionalMemoInfo.getTo()),new AssetAmount(optionalMemoInfo.getAmount(), assetId));
            }else{
                transferOperation =new TransferOperationBuilder().
                        setTransferAmount(new AssetAmount(optionalMemoInfo.getAmount(), assetId)).//交易金额
                        setSource(new UserAccount(from)).
                        setDestination(new UserAccount(optionalMemoInfo.getTo())).
                        setFee(new AssetAmount(0L, assetId)).
                        setMemo(optionalMemoInfo.getMemo()).
                        build();
            }
            operations.add(transferOperation);
        }
        return operations;
    }

    public DepositHexRet locktokenHex(String fromissuer,String prikey,String tokenid,String touid,long amount,int type){
        ArrayList<BaseOperation> operations = new ArrayList();
        AssetAmount fee = new AssetAmount(0L, assetId);
        UserAccount issuer = new UserAccount(fromissuer);
        UserAccount to = new UserAccount(touid);
        Locktoken locktoken = new Locktoken(tokenid);
//        int optype = 1;
        int period = 0;
        AssetAmount toamount = new AssetAmount(amount, assetId);
        LocktokenNodeOperation locktokenNodeOperation = new LocktokenNodeOperation(fee,issuer,locktoken,type,to,toamount,period);
        operations.add(locktokenNodeOperation);

        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;


        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        transaction.setChainId(chainId);
        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));

        transaction.getGrapheneSignature();
        String json = transaction.toJsonString();
        String txid = transaction.calculateTxid();
        DepositHexRet ret = new DepositHexRet();
        ret.setJson(json);
        ret.setTxid(txid);
        return ret;
    }

    public DepositHexRet locktokenremoveHex(String fromissuer,String prikey,String tokenid){
        ArrayList<BaseOperation> operations = new ArrayList();
        AssetAmount fee = new AssetAmount(0L, assetId);
        UserAccount issuer = new UserAccount(fromissuer);
        Locktoken locktoken = new Locktoken(tokenid);

        LocktokenRemoveOperation locktokenNodeOperation = new LocktokenRemoveOperation(fee,issuer,locktoken);
        operations.add(locktokenNodeOperation);

        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
        String headBlockId = dynamicProperties.head_block_id;
        long headBlockNumber = dynamicProperties.head_block_number;


        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
        transaction.setChainId(chainId);
        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));

        transaction.getGrapheneSignature();
        String json = transaction.toJsonString();
        String txid = transaction.calculateTxid();
        DepositHexRet ret = new DepositHexRet();
        ret.setJson(json);
        ret.setTxid(txid);
        return ret;
    }

    public DynamicGlobalProperties getDynamicGlobal(){
        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
//        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
//        String headBlockId = dynamicProperties.head_block_id;
//        long headBlockNumber = dynamicProperties.head_block_number;

        return dynamicProperties;
    }


//    public DepositHexRet transferHexWithMemo(String from, String prikey,List<OptionalMemoInfo> optionals){
//        ArrayList<BaseOperation> operations = new ArrayList();
//        for(OptionalMemoInfo info:optionals){
//
//            TransferOperation transferOperation =new TransferOperationBuilder().
//                    setTransferAmount(new AssetAmount(info.getAmount(), assetId)).//交易金额
//                    setSource(new UserAccount(from)).
//                    setDestination(new UserAccount(info.getTo())).
//                    setFee(new AssetAmount(0L, assetId)).
//                    setMemo(info.getMemo()).
//                    build();
//            operations.add(transferOperation);
//        }
//
//        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
//        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
//        String headBlockId = dynamicProperties.head_block_id;
//        long headBlockNumber = dynamicProperties.head_block_number;
//
//
//        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
//        transaction.setChainId(chainId);
//        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));
//
//        transaction.getGrapheneSignature();
//        String json = transaction.toJsonString();
//        String txid = transaction.calculateTxid();
//        DepositHexRet ret = new DepositHexRet();
//        ret.setJson(json);
//        ret.setTxid(txid);
//        return ret;
//    }

//    public DepositHexRet transferHexWithMemo(String from, String prikey,String to, long amount,Memo memo) {
////        TransferOperation transferOperation = new TransferOperation(new AssetAmount(amount, assetId),new AssetAmount(10000, assetId),
////                new UserAccount(from),new UserAccount(to),memo);
//        TransferOperation transferOperation =new TransferOperationBuilder().
//                setTransferAmount(new AssetAmount(amount, assetId)).//交易金额
//                setSource(new UserAccount(from)).
//                setDestination(new UserAccount(to)).
//                setFee(new AssetAmount(0L, assetId)).
//                setMemo(memo).
//                build();
//        ArrayList<BaseOperation> operations = new ArrayList();
//        operations.add(transferOperation);
//        DynamicGlobalProperties dynamicProperties = this.httpClient.getDynamicGlobalProperties();
//        long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
//        String headBlockId = dynamicProperties.head_block_id;
//        long headBlockNumber = dynamicProperties.head_block_number;
//
//
//        Transaction transaction = new Transaction(prikey, new BlockData(headBlockNumber, headBlockId, expirationTime), operations);
//        transaction.setChainId(chainId);
//        transaction.setFees(this.httpClient.getRequiredFees(transaction.getOperations(), new Asset(assetId)));
//        String bufStrHex = transaction.getGrapheneSignature2();
//        String json = transaction.toJsonString();
////        System.out.println("---toBytes:" + bufStrHex);
//        DepositHexRet ret = new DepositHexRet();
//        ret.setHex(bufStrHex);
//        ret.setJson(json);
//        return ret;
//    }
    public String getAccountAttachInfo(String accountId){
        return httpClient.getAccountAttachInfo(accountId,20).toString();
    }

    public AccountProperties getAccountById(String userid){
        List<String> ids = new ArrayList<>();
        ids.add(userid);
        List<AccountProperties> list = httpClient.getAccounts(ids);
        if(list.size()!=0){
            return list.get(0);
        }
        return null;
    }

    public Block getBlock(long blockHeight){
        return httpClient.getBlockIds(blockHeight);
//        System.out.println(block.block_id+block.timestamp+block.version+(block.transactions)[0].toJsonString());
//        System.out.println(block.getTransactions().length);
//        Transaction transaction = block.getTransactions()[0];
//        transaction.setChainId(getHttpClient().getChainId());
//        transaction.setExtensions();
//        System.out.println(transaction.toJsonString());
//        System.out.println(transaction.calculateTxid());
//        Transaction[] transactions =block.transactions;

    }

    public JsonElement getVerityByTxid(String txid){
        JsonElement jsonElement = httpClient.verifyByTxid(txid);
        String str = jsonElement.toString();
//        System.out.println(StringUtils.isBlank(str));
//        System.out.println(StringUtils.isEmpty(str));
        System.out.println("null".equals(str)|| str==null);
//        System.out.println(jsonElement==null);
//        System.out.println(jsonElement.toString());
        return jsonElement;
    }
    public void verifyByTxidBlockHeight(long blockNum,String txid){
        JsonElement jsonElement = httpClient.verifyByTxidBlockHeight(blockNum,txid);
        String jsonArray = jsonElement.getAsJsonObject().get("operations").getAsJsonArray().get(0).getAsJsonArray().get(1).getAsJsonObject().get("amount").getAsJsonObject().get("amount").getAsString();
        System.out.println(jsonArray);
        System.out.println(jsonElement.getAsJsonObject().get("operations"));
        System.out.println(jsonElement);

    }
    public void getLockToken(String uid){
        List<String> lists = new ArrayList<>();
        lists.add(uid);
        lists.add(2+"");//0 是活期，1 是定期 2 是节点
        JsonElement jsonElement = httpClient.getLocktokens(lists);
        JsonArray jsonArray = jsonElement.getAsJsonArray();
//        System.out.println(jsonArray.size());
        if(jsonArray.size()>0){
//            System.out.println(jsonArray.get(0).getAsString());
        }

//        System.out.println(jsonArray.isJsonNull());
//        jsonArray.
        String str = jsonElement.toString();
        String sgr = str.substring(2);
        System.out.println(uid+"==="+sgr.substring(0,sgr.lastIndexOf("\"")));
    }
    public void getLockObject(String ids){

        List<String> lists = new ArrayList<>();
        lists.add(ids);
        JsonElement jsonElement = httpClient.getObjects(lists);
        System.out.println("----"+jsonElement);
    }
    public static void main(String[] args) throws Exception {
//        String json = "{\"jsonrpc\": \"2.0\", \"method\": \"call\", \"params\": [0, \"vertify_transaction\", [{\"ref_block_num\":59543,\"ref_block_prefix\":1864245504,\"expiration\":\"2018-11-06T03:07:10\",\"operations\":[[35,{\"fee\":{\"amount\":\"102402\",\"asset_id\":\"1.3.0\"},\"payer\":\"1.2.31\",\"required_auths\":[],\"id\":9999,\"data\":\"7b226368616e6e656c54797065223a342c226163636f756e745f6e6f223a223078313233313233222c226163636f756e744e616d65223a22222c22616d6f756e74223a302e3030312c22636f696e5f74797065223a22425443222c22757365724964223a226e617468616e227d3\"}]],\"extensions\":[],\"signatures\":[\"1f03d9ebefe9988aab1867e7931106c5f920768b16c8c7bac1b00a55ffd33dabfe08b82cbb19a252fded01ad009a50b8612b1e347af17b01eee190beaa5bf072aa\"]}]], \"id\": 1}";
//        JSONObject json = JSON.parseObject("{\"jsonrpc\": \"2.0\", \"method\": \"call\", \"params\": [0, \"vertify_transaction\", [{\"ref_block_num\":59543,\"ref_block_prefix\":1864245504,\"expiration\":\"2018-11-06T03:07:10\",\"operations\":[[35,{\"fee\":{\"amount\":\"102402\",\"asset_id\":\"1.3.0\"},\"payer\":\"1.2.31\",\"required_auths\":[],\"id\":9999,\"data\":\"7b226368616e6e656c54797065223a342c226163636f756e745f6e6f223a223078313233313233222c226163636f756e744e616d65223a22222c22616d6f756e74223a302e3030312c22636f696e5f74797065223a22425443222c22757365724964223a226e617468616e227d3\"}]],\"extensions\":[],\"signatures\":[\"1f03d9ebefe9988aab1867e7931106c5f920768b16c8c7bac1b00a55ffd33dabfe08b82cbb19a252fded01ad009a50b8612b1e347af17b01eee190beaa5bf072aa\"]}]], \"id\": 1}");
//        log.info(json + "");
//        String json = "{\"ref_block_num\":64243,\"ref_block_prefix\":4247653888,\"expiration\":\"2019-01-23T08:21:45\",\"operations\":[[48,{\"fee\":{\"amount\":\"10000\",\"asset_id\":\"1.3.0\"},\"from\":\"1.2.123\",\"to\":\"1.2.105\",\"withdraw\":{\"amount\":\"100000000\",\"asset_id\":\"1.3.23\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1f5faec2ab083ca159c1b21d73d575cb17ac37425df2a9e7a7d508d115a9ad9066538f27b6c6008d7aa783c74d744a3a52acf834cbca7923bdf4414c092ff9777d\"]}";

//        long precision = 8;
//        double c = Math.pow(10, precision);
//        long l = Math.round(c);
//        System.out.println(l);
//
        String httpUrl  = "http://47.75.107.157:8290";
        String wsUrl  = "ws://47.75.107.157:8290";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
//        String jsonElement = zosClient.getAccounts("1.2.105");
        AccountProperties accountProperties = zosClient.getAccountById("1.2.162");
        System.out.println(accountProperties.toString());
        String memoKey  =accountProperties.getOptions().getMemoKey().getKey().toString();
        String memoKey2  =accountProperties.getOptions().getMemoKey().getAddress();
        String memoKey3  =accountProperties.getOptions().getMemoKey().toString();
        log.info("memokey="+memoKey);
        log.info("memokey2="+memoKey2);
        log.info("memokey3="+memoKey3);

//        AccountProperties accountProperties = zosClient.httpClient.getAccounts();
        log.debug(accountProperties.toString());

//        try {
//            ZOSResponse response = zosClient.balance("1.2.20", "1.3.0");
//            System.out.println(response.getData() + "");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.debug(e.toString());
//        }
//        String x = zosClient.kyc("5Ji5xc17cKcs351LLSePpQrx84oFhyk6cM1FyYC3kG84ZJBHz12", "1.2.123", "1.2.154");
//        if (x.indexOf("error") == -1) {
//            System.out.println("********" + x);
//        }
//        ZosClient zosClient = new ZosClient();
//        zosClient.getRealCoinType("1.2.100");






    }

    public String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    private int seq = 6;
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



}
