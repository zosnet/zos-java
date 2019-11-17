package com.zos.common.ws.client.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.zos.common.ws.client.ChainApiRestClient;
import com.zos.common.ws.client.api.ChainApi;
import com.zos.common.ws.client.api.ZosClient;
import com.zos.common.ws.client.api.domain.DepositHexRet;
import com.zos.common.ws.client.api.domain.ZOSResponse;
import com.zos.common.ws.client.graphenej.Address;
import com.zos.common.ws.client.graphenej.PublicKey;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.models.AccountProperties;
import com.zos.common.ws.client.graphenej.models.Block;
import com.zos.common.ws.client.graphenej.models.DynamicGlobalProperties;
import com.zos.common.ws.client.graphenej.objects.Memo;
import com.zos.common.ws.client.graphenej.objects.OptionalMemoInfo;
import com.zos.common.ws.client.graphenej.objects.Transaction;
import com.zos.common.ws.client.util.WsGsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Test {

//    private String httpUrl = "http://106.14.181.91:8290";
    private String httpUrl = "http://47.75.107.157:8290";
//    private String httpUrl = "http://node04.zostu.com:8190";
//    private String wsUrl = "ws://106.14.181.91:8290";
    private String wsUrl = "ws://47.75.107.157:8290";
//    private String wsUrl = "ws://node04.zostu.com:8190";
    private String from = "1.2.162";
    private String priKey="5J6pEviwZGj5XUAZgZdBDdCDVHpLu1HneeLYp3ciof3xQmVXF8b";
    private String to="1.2.164";
//    zosnodehttpurl: https://node04.zostu.com:443
//    zosnodewsurl: wss://node04.zostu.com:443
//    public void transfer(){
//        String from = "1.2.162";
//        String priKey="5J6pEviwZGj5XUAZgZdBDdCDVHpLu1HneeLYp3ciof3xQmVXF8b";
//        String to="1.2.164";
//        long amount = 300000+RandomUtils.nextLong(10000,100000);
////        long amount =79328899199L;
////        String httpUrl = "http://47.75.107.157:8290";
////        String httpUrl = "http://106.14.181.91:8290";
////        String wsUrl = "ws://47.75.107.157:8290";
////        String wsUrl = "ws://106.14.181.91:8290";
//        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
//        try {
////            ZOSResponse zosResponse = chainApi.transferTransaction(from,priKey,to,amount);
//            ZOSResponse zosResponse = chainApi.transferTransaction(from,priKey,to,amount);
//            System.out.println(zosResponse.getData().toString());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

//    public void mutiTransfer(int num) {
//        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
//
//        for(int i=0;i<num;i++){
//            long amount = 300000+(i*10000);
////            long amount = 300000+RandomUtils.nextLong(10000,100000);
//            DepositHexRet depositHexRet = zosClient.transferHex(from,priKey,to,amount);
////            log.info("------"+depositHexRet.getJson());
//            try{
//                ZOSResponse zosResponse = zosClient.sendNoCB(depositHexRet.getJson());
//                System.out.println(zosResponse.getData().toString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//    }

//    public void mutiTransferwithmemo(int num) {
//        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
//        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
//        AccountProperties fromAccountProperties = chainApi.getAccountById(from);
//        AccountProperties toAccountProperties = chainApi.getAccountById(to);
//
//        for(int i=0;i<num;i++){
//            try{
//                long amount = 300000+(i*10000);
////            long amount = 300000+RandomUtils.nextLong(10000,100000);
//                Memo memo = chainApi.buildMemoWithAddress(fromAccountProperties.getMemoPublicKey(),priKey,
//                        toAccountProperties.getMemoPublicKey(),"");
//                DepositHexRet depositHexRet = zosClient.transferHexWithMemo(from,priKey,to,amount,memo);
////            log.info("------"+depositHexRet.getJson());
//
//                ZOSResponse zosResponse = zosClient.sendNoCB(depositHexRet.getJson());
//                System.out.println(zosResponse.getData().toString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//
//    }
    public void mutiTransferOT(){
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
        AccountProperties fromAccountProperties = chainApi.getAccountById(from);
        AccountProperties toAccountProperties = chainApi.getAccountById(to);

        List<OptionalMemoInfo> infos = new ArrayList<>();

        try{
            for(int i=0;i<2;i++){
                OptionalMemoInfo info = new OptionalMemoInfo();
                info.setTo(to);
                info.setAmount(300000+RandomUtils.nextLong(10000,100000));
                ChainApi api = new ChainApi(httpUrl,wsUrl);
                Memo memo = api.buildMemoWithAddress(fromAccountProperties.getMemoPublicKey(),priKey,
                        toAccountProperties.getMemoPublicKey(),"11111111111111888888888888881");
//                info.setMemo(api.buildMemo(from,priKey,to,BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE)),""));
                System.out.println("长度=="+memo.getByteMessage().length);
                System.out.println("长度=="+Util.bytesToHex(memo.getByteMessage()));
                System.out.println("长度=="+Util.bytesToHex(memo.getByteMessage()).length());
                System.out.println("长度=="+memo.getPlaintextMessage());
                System.out.println("长度=="+memo.getByteMessage().toString());
                info.setMemo(memo);
                infos.add(info);
            }
//            DepositHexRet depositHexRet = zosClient.transferHexWithMemo(from,priKey,infos);
            DepositHexRet depositHexRet = zosClient.transferHex(from,priKey,infos);
            ZOSResponse zosResponse =zosClient.setIsCB(false).send(depositHexRet.getJson());
//            ZOSResponse zosResponse1 =zosClient.sendNoCB(depositHexRet.getJson());
////            DepositHexRet depositHexRet = zosClient.transferHexWithMemo(from,priKey,to,amount,memo);
//            ZOSResponse zosResponse = chainApi.transferTransaction(from,priKey,infos,true);
//            chainApi.closeConn();
            System.out.println(zosResponse.getData().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void locktokenaward(){
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        String objectid = "1.26.63";
        this.to = "1.2.278";
        long amount = 2400000000L;
        int type = 0 ;//0利息，1一级奖励 2二级奖励
        try {
            DepositHexRet depositHexRet = zosClient.locktokenHex(from,priKey,objectid,to,amount,type);
            System.out.println("---"+depositHexRet.getTxid());
            ZOSResponse zosResponse =zosClient.setIsCB(true).send(depositHexRet.getJson());
            System.out.println(zosResponse.getCode());
            System.out.println("=="+zosResponse.getData().toString());
            zosClient.closedConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLocktoken(){
        try {
            ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
            String objectid = "1.26.45";
            DepositHexRet depositHexRet = zosClient.locktokenremoveHex(from,priKey,objectid);
            System.out.println("---"+depositHexRet.getTxid());
            ZOSResponse zosResponse =zosClient.setIsCB(true).send(depositHexRet.getJson());
            System.out.println(zosResponse.getCode());
            System.out.println("=="+zosResponse.getData().toString());
            zosClient.closedConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void transferwithmemo(){
////        String httpUrl = "http://47.75.107.157:8290";
////        String wsUrl = "ws://47.75.107.157:8290";
//        String from = "1.2.162";
//        String priKey="5J6pEviwZGj5XUAZgZdBDdCDVHpLu1HneeLYp3ciof3xQmVXF8b";
//        String to="1.2.164";
////        long amount = 1200000;
//        long amount =79328899199L;
//        String message = "123456";
////        com.zos.common.ws.client.graphenej.Address from, Address to, BigInteger nonce, byte[] message
//        try {
//            ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
//
//
//            BigInteger nonce = BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE));
//            Address fromAddress = new Address(zosClient.getAccountById(from).getMemoPublicKey());
//            Address toAddress = new Address(zosClient.getAccountById(to).getMemoPublicKey());
//            byte[] messageByte = Memo.encryptMessage(DumpedPrivateKey.fromBase58(null, priKey).getKey(),toAddress.getPublicKey()
//                    ,nonce,message);
//
//
//            Memo memo =  new Memo(fromAddress,toAddress,
//                    nonce,
////                    Util.hexToBytes(message));
//                    messageByte);
//
////            DepositHexRet depositHexRet = zosClient.transferHexWithMemo(from,priKey,to,amount,assetId,memo);
//            DepositHexRet depositHexRet = zosClient.transferHexWithMemo(from,priKey,to,amount,memo);
////            ZOSResponse zosResponse = zosClient.send(depositHexRet.getJson());
//
//
//            ZOSResponse zosResponse = zosClient.send(depositHexRet.getJson());
//
//            Object result =( (WitnessResponse)zosResponse.getData()).getResult();
//            //查看result是否为null，如果不为空这result为txid，为空则查看message，message是失败原因
//            if(result==null){
//                String messsage = ((WitnessResponse)(zosResponse.getData())).getError().message;
//                log.info("#######"+messsage);
//            }else{
//                log.info("$$$$$$$$"+zosResponse.getData().toString());
//            }
//
//
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void block(long blockHeight){
//        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
//        zosClient.getBlock(blockHeight);c
        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
        Block block = chainApi.getBlock(blockHeight);
        String[] ids = block.getTrxids();
        System.out.println(ids.length);
        System.out.println(ids[0]);
    }

    public void decryptMemo(){
//        String httpUrl = "http://47.75.107.157:8290";
//        String wsUrl = "ws://47.75.107.157:8290";
        String prikey="5K4BKvw3BcwXr44mWvc8SAdk9XkiuFPFLNauoPby8JktUqeA8QM";

        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        String from = "1.2.162";
//        ECKey privateKey, Address sourceAddress, BigInteger nonce, byte[] message

        try {
            Address fromAddress = new Address(zosClient.getAccountById(from).getMemoPublicKey());
            String str = Memo.decryptMessage(DumpedPrivateKey.fromBase58(null, prikey).getKey(),
                    fromAddress,new BigInteger("2668740240541604864"),
                    new Memo("bc7df8f94ce806ac07807e0981149812").getByteMessage());
//                    "bc7df8f94ce806ac07807e0981149812".getBytes());
//                    Util.hexToBytes("7dbc58cdbddc7d89deec257c298d575f"));
            System.out.println("str====="+str);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void encryptDecrypMessage(){
//        String httpUrl = "http://47.75.107.157:8290";
//        String wsUrl = "ws://47.75.107.157:8290";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        String fromPriKey="5J6pEviwZGj5XUAZgZdBDdCDVHpLu1HneeLYp3ciof3xQmVXF8b";
        String toPriKey="5K4BKvw3BcwXr44mWvc8SAdk9XkiuFPFLNauoPby8JktUqeA8QM";
        String from = "1.2.162";
        String to="1.2.164";
        BigInteger nonce = BigInteger.valueOf(RandomUtils.nextLong(0L, Long.MAX_VALUE));
        String message = "6";
        try {
            Address fromAddress = new Address(zosClient.getAccountById(from).getMemoPublicKey());
            Address toAddress = new Address(zosClient.getAccountById(to).getMemoPublicKey());

            for(int i=1;i<999999999;i++){
                byte[] messageByte = Memo.encryptMessage(DumpedPrivateKey.fromBase58(null, fromPriKey).getKey(),toAddress.getPublicKey()
                        ,nonce,i+"");

//            String aa  ="2646e6e9b299e3f25084f40446156dda";
//            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
//            byte[] sha256Msg = sha256.digest(aa.getBytes());
//            byte[] checksum = Arrays.copyOfRange(sha256Msg, 0, 4);

                // Concatenating checksum + message bytes
//            byte[] msgFinal = Bytes.concat(checksum, aa.getBytes());


//            String json = "{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":\"8644134650905067520\",\"message\":\"67fb96f4cd2dcde99194fb96fd71fc50\"}";
//            Memo memo = WsGsonUtil.fromJson(json, Memo.class);
//            memo.getByteMessage();

                String str = Memo.decryptMessage(DumpedPrivateKey.fromBase58(null, toPriKey).getKey(),fromAddress,nonce,messageByte);
                if(!str.equals(i+"")){
                    System.out.println("----------------");
                    System.out.println("str====="+str);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getTxid(){



        return null;
    }

    public  String toRIPEMD160(String in)
    {

        try{


        byte[] addr = in.getBytes();
        byte[] out = new byte[20];
        RIPEMD160Digest digest = new RIPEMD160Digest();
        byte[] sha256 = sha256(addr);

        String encodedSha256 = getHexString(sha256);
        byte[] strBytes = encodedSha256.getBytes("UTF-8");

//        digest.update(sha256,0,sha256.length);
        digest.update(strBytes,0,strBytes.length);
        digest.doFinal(out,0);
        System.out.println(Util.bytesToHex(out));
        System.out.println(getHexString(out));
            return getHexString(out);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public  String toRIPEMD1602(String in)
    {

        try{


            byte[] addr = in.getBytes();
            byte[] out = new byte[20];
            RIPEMD160Digest digest = new RIPEMD160Digest();
            byte[] sha256 = sha256(addr);

            digest.update(sha256,0,sha256.length);
            digest.doFinal(out,0);
            System.out.println(Util.bytesToHex(out));
            System.out.println(getHexString(out));
            return getHexString(out);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public  byte[] sha256(byte[] data)
    {
        byte[] sha256 = new byte[32];
        try
        {
            sha256 = MessageDigest.getInstance("SHA-256").digest(data);
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return sha256;
    }
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }
    private  final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public  String getHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public void getTransBytxid(){
        String txid = "2bcd8c4ad25927211cf9ef0c0c6f4781671651e2";
//        String httpUrl = "http://47.75.107.157:8290";
//        String wsUrl = "ws://47.75.107.157:8290";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        JsonElement jsonElement = zosClient.getVerityByTxid(txid);
        String str = jsonElement.toString();
        if("null".equals(str)|| str==null){
            System.out.println("no");
        }else {
            System.out.println("yes");
        }

    }
    public void getTransBytxidBlockHeight(){
        String txid = "ed394ae09f6dd8caf584c14d6686174bb37c105b";
//        String httpUrl = "http://47.75.107.157:8290";
//        String wsUrl = "ws://47.75.107.157:8290";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        long num = 2757512;
        zosClient.verifyByTxidBlockHeight(num,txid);
    }

    public void parseBlock(){
        String blockStr = "{\"version\":3,\"previous\":\"002af202009cd2d6536e3257beaea8fbdf70ae8a\",\"timestamp\":\"2019-08-01T14:52:57\",\"witness\":\"1.6.2\",\"transaction_merkle_root\":\"2aa448067d394399283ce9d69500f19982b1386e\",\"extensions\":[],\"witness_signature\":\"1f5ba16c3728e125fe5c4f7105119d62dfe4c819b9f6bc6c73a4a806b5792d2e9474cf17634f9bd600f3398fe35c37cd8612542e9aa9a3b0317f792f3f3e8fb6b3\",\"transactions\":[{\"ref_block_num\":61954,\"ref_block_prefix\":3604126720,\"expiration\":\"2019-08-01T14:53:45\",\"operations\":[[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":395582,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":\"6104683790163809280\",\"message\":\"acbb0e230803eaafe661cb82b2ee7045\"},\"extensions\":[]}],[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":326948,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":\"2426130342929251328\",\"message\":\"d9e32344e780b6576424ea958e5fd16a\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1c3357b4ad28e45219cae5fead0614e310367c102efd9d1884ea37aed2f5cbf6ef472ae950bc82e93e4d62a88c2ffbaca0bc4b2d85fca1441ca8d1a356be9cbeb1\"],\"operation_results\":[[0,{}],[0,{}]]}]}";
        Block block = WsGsonUtil.fromJson(blockStr, Block.class);
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        System.out.println(block.getTransactions().length);
        Transaction transaction = block.getTransactions()[0];
        transaction.setChainId(zosClient.getHttpClient().getChainId());
        transaction.setExtensions();
        System.out.println(transaction.calculateTxid());
//        Block block = new Block();
    }
    public void parseTrans(){
//        String trans = "{\"ref_block_num\":62542,\"ref_block_prefix\":859319552,\"expiration\":\"2019-08-01T15:27:33\",\"operations\":[[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":383143,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":5515656328279224320,\"message\":\"50c83ce6c0e0a9f45e07f06b2d93f02a\"},\"extensions\":[]}],[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":331146,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":7882627137380656128,\"message\":\"1043593285bf1b77aa121d2bf59b9115\"},\"extensions\":[]}]],\"signatures\":[\"1c29275931c5a4dddcb19708567597faee6c23fc52e0c1f90ca5ce4e8f1d5e44246609eb48ea8a8202b2e9786a41b8074e08bb664afd49ca7c07dc024dbc6b7ec2\"],\"extensions\":[]}";
        String trans = "{\"ref_block_num\":62542,\"ref_block_prefix\":859319552,\"expiration\":\"2019-08-01T15:27:33\",\"operations\":[[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":383143,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":\"5515656328279224320\",\"message\":\"50c83ce6c0e0a9f45e07f06b2d93f02a\"},\"extensions\":[]}],[0,{\"fee\":{\"amount\":10449,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":331146,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"ZOS8d7wisspbNVjqnydBbCqyXYe7TsxewVev3cdHEwtnjjcU5Uz8q\",\"to\":\"ZOS75N8CK2s85364uvvfuZoan9WGuxAJKDjjXqaWrPVX7QCVPL8F5\",\"nonce\":\"7882627137380656128\",\"message\":\"1043593285bf1b77aa121d2bf59b9115\"},\"extensions\":[]}]],\"signatures\":[\"1c29275931c5a4dddcb19708567597faee6c23fc52e0c1f90ca5ce4e8f1d5e44246609eb48ea8a8202b2e9786a41b8074e08bb664afd49ca7c07dc024dbc6b7ec2\"],\"extensions\":[]}";
        Transaction transaction = new Transaction();
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);

        transaction = WsGsonUtil.fromJson(trans, Transaction.class);
        transaction.setChainId(zosClient.getHttpClient().getChainId());
        transaction.setExtensions();
        System.out.println(transaction.calculateTxid());

    }

    public void transferlastone(){

        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
        AccountProperties fromAccountProperties = chainApi.getAccountById(from);
        AccountProperties toAccountProperties = chainApi.getAccountById(to);


        try {

            List<OptionalMemoInfo> infos = new ArrayList<>();
            for(int i=0;i<1;i++){
                OptionalMemoInfo info1 = new OptionalMemoInfo();
                info1.setTo(to);
                info1.setAmount(300000+RandomUtils.nextLong(10000,100000));
                Memo memo = chainApi.buildMemoWithAddress(fromAccountProperties.getMemoPublicKey(),priKey,
                        toAccountProperties.getMemoPublicKey(),"");
                info1.setMemo(memo);
                infos.add(info1);
            }

            for(int i=0;i<2;i++){
                OptionalMemoInfo info = new OptionalMemoInfo();
                info.setTo(to);
                info.setAmount(300000+RandomUtils.nextLong(10000,100000));
                infos.add(info);
            }

            ZOSResponse zosResponse = chainApi.transferTransaction(from,priKey,infos,true);
            System.out.println(zosResponse);
            chainApi.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getlocktoken(){
        String uids = "1.2.2088,1.2.1905,1.2.6358,1.2.1325,1.2.3505,1.2.6359,1.2.6360,1.2.6361,1.2.6345,1.2.6362,1.2.6363,1.2.6364,1.2.6365,1.2.6340,1.2.1654,1.2.6308,1.2.1576,1.2.6366,1.2.6367,1.2.1313,1.2.3737,1.2.6368,1.2.6369,1.2.6343,1.2.6348,1.2.6325,1.2.1989,1.2.6317,1.2.2757,1.2.2076,1.2.5759,1.2.6371,1.2.6372,1.2.6373,1.2.2116,1.2.6374,1.2.1970,1.2.6375,1.2.2085,1.2.6376,1.2.6377,1.2.6378,1.2.2519,1.2.4576,1.2.6379,1.2.6380,1.2.6381,1.2.6318,1.2.2119,1.2.6344,1.2.6383,1.2.6384,1.2.6385,1.2.6386,1.2.6387,1.2.6388,1.2.6449,1.2.6389,1.2.6390,1.2.6391,1.2.6392,1.2.6394,1.2.6398,1.2.1996,1.2.6407,1.2.6409,1.2.6410,1.2.6411,1.2.6413,1.2.6414,1.2.6415,1.2.6416,1.2.6417,1.2.6419,1.2.6421,1.2.6423,1.2.6425,1.2.6426,1.2.6428,1.2.6429,1.2.6431,1.2.6433,1.2.6435,1.2.6438,1.2.6439,1.2.6336,1.2.6349,1.2.3755,1.2.6440,1.2.6437,1.2.6436,1.2.6434,1.2.1889,1.2.2238,1.2.6432,1.2.2065,1.2.6321,1.2.3749,1.2.6430,1.2.2533,1.2.6324,1.2.2106,1.2.2433,1.2.6320,1.2.6319,1.2.6427,1.2.3820,1.2.6424,1.2.6422,1.2.6420,1.2.6418,1.2.6412,1.2.6408,1.2.6406,1.2.6405,1.2.6309,1.2.6316,1.2.1969,1.2.6310,1.2.6443,1.2.1906,1.2.803,1.2.6444,1.2.6404,1.2.3770,1.2.6403,1.2.6402,1.2.6401,1.2.3752,1.2.6445,1.2.2102,1.2.6400,1.2.6399,1.2.1993";
        String[] ids = uids.split(",");
//        String uid = "1.2.1325";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        for(String uid : ids){
            zosClient.getLockToken(uid);
        }


    }
    public void getlockobj(){
        String ids = "1.26.47";
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        zosClient.getLockObject(ids);

    }
    public void parseJson(){
        String json = "{\"ref_block_prefix\":1226815744,\"extensions\":[],\"operations\":[[107,{\"period\":20,\"extensions\":[],\"autolock\":0,\"fee\":{\"amount\":\"100000\",\"asset_id\":\"1.3.0\"},\"to\":\"1.2.162\",\"locked\":{\"amount\":\"5000\",\"asset_id\":\"1.3.0\"},\"type\":0,\"issuer\":\"1.2.31\"}]],\"expiration\":\"2019-08-07T13:21:12\",\"ref_block_num\":27970,\"signatures\":[\"1f7f2fec49d1a12fd99f8977181187acc1c50d8cf1f3deb7c7cc471ca741ab15bf73ae0f4398d5d8b10a58fefcbc8016033924fbb55e939a9bf8bb3797997185bb\"]}";
        String amount = null;
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            String operationsString = jsonObject.getString("operations");
            JSONArray jsonArray = JSON.parseArray(operationsString);
            String str1 = jsonArray.get(0).toString();
            JSONArray jsonArray1 = JSON.parseArray(str1);
            JSONObject jsonObject2 = JSON.parseObject(jsonArray1.get(1).toString());
            String lockedStr = jsonObject2.getString("locked");
            amount = JSON.parseObject(lockedStr).getString("amount");
        } catch (Exception e) {

        }
        System.out.println(amount);

    }

    public void closed(){
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);

        zosClient.closedConn();
    }

    public void dynamicGlobalPropertie(){
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        DynamicGlobalProperties dynamicGlobalProperties = zosClient.getDynamicGlobal();
        System.out.println(dynamicGlobalProperties.toString());
    }

    public void getAccountByName(){
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        System.out.println(zosClient.getAccountByName("vicky001"));
    }

    public void getBalance(){
        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
        String amount = chainApi.getbalance(from,"1.3.0");
        System.out.println(amount);
    }

    public void offlinetransfer(){

        ChainApi chainApi = new ChainApi(httpUrl,wsUrl);
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        ChainApiRestClient httpClient =  zosClient.getHttpClient();
        AccountProperties fromAccountProperties = chainApi.getAccountById(from);
        AccountProperties toAccountProperties = chainApi.getAccountById(to);

        ChainApi chainApiOffLine = new ChainApi("6202d61065732dea57057bf4d9d60ed0a85d3a7712621516dce18d9da404fc79");
        try {

            List<OptionalMemoInfo> infos = new ArrayList<>();
            for(int i=0;i<1;i++){
                OptionalMemoInfo info1 = new OptionalMemoInfo();
                info1.setTo(to);
                info1.setAmount(200000+RandomUtils.nextLong(10000,100000));
                Memo memo = chainApi.buildMemoWithAddress(fromAccountProperties.getMemoPublicKey(),priKey,
                        toAccountProperties.getMemoPublicKey(),"");
                ECKey privateKey = DumpedPrivateKey.fromBase58(null, priKey).getKey();
                PublicKey publicKey = new Address(toAccountProperties.getMemoPublicKey()).getPublicKey();
                Memo memo1 = chainApi.buildMemo(privateKey,publicKey,"");
                info1.setMemo(memo1);
                infos.add(info1);
            }

            for(int i=0;i<2;i++){
                OptionalMemoInfo info = new OptionalMemoInfo();
                info.setTo(to);
                info.setAmount(200000+RandomUtils.nextLong(10000,100000));
                infos.add(info);
            }

            DynamicGlobalProperties dynamicProperties =httpClient.getDynamicGlobalProperties();
            long expirationTime = dynamicProperties.time.getTime() / 1000L + 30L;
            String headBlockId = dynamicProperties.head_block_id;
            long headBlockNumber = dynamicProperties.head_block_number;

            long basicFee = 10371;
            BigDecimal byteFee = new BigDecimal("4.875");

            DepositHexRet depositHexRet = chainApiOffLine.signOffline(from,priKey,infos,headBlockNumber,headBlockId,expirationTime,basicFee,byteFee);
            System.out.println(depositHexRet);

            ZOSResponse zosResponse = chainApi.sendTransfer(depositHexRet.getJson(),true);
            System.out.println(zosResponse);
            chainApi.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void key(){
//        ECPoint senderPoint = ECKey.compressPoint(from.getPublicKey().getKey().getPubKeyPoint());
//        PublicKey senderPublicKey = new PublicKey(ECKey.fromPublicOnly(senderPoint));
        ZosClient zosClient = new ZosClient(httpUrl,wsUrl);
        AccountProperties fromAccountProperties = zosClient.getAccountById(from);
        String pub = fromAccountProperties.getMemoPublicKey();
        System.out.println(pub);
//        new Address(fromAccountProperties.getMemoPublicKey());


        ECKey privateKey = DumpedPrivateKey.fromBase58(null, priKey).getKey();

        Address address = new Address(ECKey.fromPublicOnly(privateKey.getPubKey()));


        System.out.println(address.toString());


    }

    public static void main(String[] args) {
        Test test =  new Test();
//        test.key();
        test.offlinetransfer();
//        test.getBalance();
//        test.getAccountByName();
//        test.dynamicGlobalPropertie();
//        test.closed();
//        test.parseJson();
//        test.getlockobj();
//        test.getlocktoken();  
//        test.locktokenaward();
//        test.removeLocktoken();
//        test.getlocktoken();
//        test.transferlastone();
//        test.parseTrans();
//        test.parseBlock();
//        test.mutiTransferwithmemo(3);
//        test.mutiTransferOT();
//        test.mutiTransferOnSend();
//        test.transfer();
//        test.block(3744564);
//        test.transferDeserializerTest();
//        test.getTransBytxid();
//        test.getTransBytxidBlockHeight();
//        test.block(1231123);
//        String str = test.toRIPEMD160("{\"fee\":{\"amount\":10000,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":200000,\"asset_id\":\"1.3.0\"},\"extensions\":[]}");
//        String str2 = test.toRIPEMD1602("{\"fee\":{\"amount\":10000,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":200000,\"asset_id\":\"1.3.0\"},\"extensions\":[]}");
//        System.out.println(str);
//        System.out.println(str2);
//
//        String str3 = test.toRIPEMD160("{\"ref_block_num\":56669,\"ref_block_prefix\":1307042816,\"expiration\":\"2019-07-24T04:27:48\",\"operations\":[[0,{\"fee\":{\"amount\":10000,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":200000,\"asset_id\":\"1.3.0\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1b6905d2811e72df8486b79ff88a1cddd709ee6332f4eeaafd9ba4f236ace742bc431b89bd91677ca6f766abd19e0dac049be107535be4522d99278cbdddf4686e\"]}");
//        String str4 = test.toRIPEMD1602("{\"ref_block_num\":56669,\"ref_block_prefix\":1307042816,\"expiration\":\"2019-07-24T04:27:48\",\"operations\":[[0,{\"fee\":{\"amount\":10000,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.162\",\"to\":\"1.2.164\",\"amount\":{\"amount\":200000,\"asset_id\":\"1.3.0\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1b6905d2811e72df8486b79ff88a1cddd709ee6332f4eeaafd9ba4f236ace742bc431b89bd91677ca6f766abd19e0dac049be107535be4522d99278cbdddf4686e\"]}");
//        System.out.println(str3);
//        System.out.println(str4);

//        String memekey = "ZOS76n78LV7uRF6nuRTfQN3us5NvBsDPaWv58sBYUWkHau13zPxrt";
//
//        try {
//            Address address =  new Address(memekey);
//            System.out.println(address.toString());
//        }catch (Exception e){
//            e.printStackTrace();
////        }
//        String json  ="{\"id\":\"1.2.1、09\",\"membership_expiration_date\":\"1969-12-31T23:59:59\",\"register_date\":\"2018-11-21T06:17:03\",\"registrar\":\"1.2.109\",\"referrer\":\"1.2.109\",\"lifetime_referrer\":\"1.2.109\",\"network_fee_percentage\":10000,\"lifetime_referrer_fee_percentage\":0,\"referrer_rewards_percentage\":2000,\"name\":\"eric\",\"owner\":{\"weight_threshold\":1,\"account_auths\":[],\"key_auths\":[[\"ZOS6MrfqvfpMUWFuwjZrGqyGvDixqaKEYjKiWuszzpZNYbXwnUkXG\",1]],\"address_auths\":[]},\"active\":{\"weight_threshold\":1,\"account_auths\":[],\"key_auths\":[[\"ZOS76n78LV7uRF6nuRTfQN3us5NvBsDPaWv58sBYUWkHau13zPxrt\",1]],\"address_auths\":[]},\"limitactive\":{\"weight_threshold\":0,\"account_auths\":[],\"key_auths\":[],\"address_auths\":[]},\"auth_data\":{\"auth_state\":[[\"1.2.838\",{\"state\":4,\"expiration\":63072000,\"hash64\":\"140721762249504\",\"auth_time\":\"2019-04-24T07:17:27\"}],[\"1.2.839\",{\"state\":4,\"expiration\":63072000,\"hash64\":\"140721762249504\",\"auth_time\":\"2019-04-11T06:20:00\"}],[\"1.2.867\",{\"state\":4,\"expiration\":63072000,\"hash64\":\"140721762249504\",\"auth_time\":\"2019-04-24T07:18:27\"}],[\"1.2.908\",{\"state\":4,\"expiration\":63072000,\"hash64\":\"140721762249504\",\"auth_time\":\"2019-04-22T03:48:12\"}]],\"hash64\":\"140721762249504\",\"key\":{\"from\":\"ZOS1111111111111111111111111111111114T1Anm\",\"to\":\"ZOS1111111111111111111111111111111114T1Anm\",\"nonce\":0,\"message\":\"\"}},\"user_info\":{\"from\":\"ZOS76n78LV7uRF6nuRTfQN3us5NvBsDPaWv58sBYUWkHau13zPxrt\",\"to\":\"ZOS5snkJVVBhKYThH1UYvdn9EQZcQJ3EqENWdygihGT9cmtHr3rAU\",\"nonce\":\"399179366346367\",\"message\":\"99b772020a01d99550f3ce17d01efb5f16294afacc9149cbd182854ca5d7e630fba404548338e3c48d93212203bb3594\"},\"options\":{\"memo_key\":\"ZOS76n78LV7uRF6nuRTfQN3us5NvBsDPaWv58sBYUWkHau13zPxrt\",\"auth_key\":\"ZOS6MRyAjQq8ud7hVNYcfnVPJqcVpscN5So8BhtHuGYqET5GDW5CV\",\"voting_account\":\"1.2.5\",\"num_witness\":0,\"num_committee\":0,\"num_budget\":0,\"votes\":[],\"extensions\":[]},\"statistics\":\"2.6.109\",\"whitelisting_accounts\":[],\"blacklisting_accounts\":[],\"whitelisted_accounts\":[],\"blacklisted_accounts\":[],\"owner_special_authority\":[0,{}],\"active_special_authority\":[0,{}],\"top_n_control_flags\":0,\"uaccount_property\":112,\"configs\":[]}";
//        AccountProperties accountProperties = WsGsonUtil.fromJson(json, AccountProperties.class);
//        System.out.println(accountProperties.getName());
//        String httpUrl = "http://47.75.107.157:8290";
//        String wsUrl = "ws://47.75.107.157:8290";
//        ChainApi chainApi =  new ChainApi(httpUrl,wsUrl);
//        AccountProperties accountProperties = chainApi.getAccountByJson(json);
//        accountProperties.getMemoPublicKey();
//        accountProperties.get


    }
}
