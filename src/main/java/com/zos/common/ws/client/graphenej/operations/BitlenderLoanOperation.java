package com.zos.common.ws.client.graphenej.operations;


import com.google.common.primitives.Bytes;
import com.google.gson.*;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.enums.OperationType;
import com.zos.common.ws.client.graphenej.objects.*;

import java.lang.reflect.Type;

public class BitlenderLoanOperation extends BaseOperation {


    private static final String CARRIER = "carrier";
    private static final String AMOUNT_TO_LOAN = "amount_to_loan";
    private static final String ORDER = "order";
    private static final String LOAN_PERIOD ="loan_period";
    private static final String INTEREST_RATE = "interest_rate";
    private static final String REPAYMENT_TYPE = "repayment_type";
    private static final String AMOUNT_TO_COLLATERALIZE = "amount_to_collateralize";
    private static final String COLLATERAL_RATE = "collateral_rate";
    private static final String BID_PERIOD ="bid_period";
    private static final String MEMO = "memo";
    private static final String ISSUER ="issuer";

    private AssetAmount fee;
    //借款人
    private UserAccount issuer;
    //运营商
    private UserAccount carrier;
    //借款单号
    private String order;
    //法币
    private AssetAmount amount_to_loan;
    //借款周期
    private int loan_period;
    //借款利息
    private int interest_rate;
    //还款方式
    private int repayment_type;
    //抵押币
    private AssetAmount amount_to_collateralize;
    //抵押率
    private int collateral_rate;
    //挂单秒数
    private int bid_period;
    private Memo memo;

    public BitlenderLoanOperation(AssetAmount fee, UserAccount issuer, UserAccount carrier, String order, AssetAmount amount_to_loan, int loan_period, int interest_rate, int repayment_type, AssetAmount amount_to_collateralize, int collateral_rate, int bid_period, Memo memo, Extensions extensions) {
        super(OperationType.LIMIT_ORDER_CREATE_OPERATION);
        this.fee = fee;
        this.issuer = issuer;
        this.carrier = carrier;
        this.order = order;
        this.amount_to_loan = amount_to_loan;
        this.loan_period = loan_period;
        this.interest_rate = interest_rate;
        this.repayment_type = repayment_type;
        this.amount_to_collateralize = amount_to_collateralize;
        this.collateral_rate = collateral_rate;
        this.bid_period = bid_period;
        this.memo = memo;
        this.extensions = extensions;
    }

    @Override
    public byte[] toBytes() {
        byte[] feeBytes = fee.toBytes();
        byte[] issuerBytes = issuer.toBytes();
        byte[] carrierBytes = carrier.toBytes();
        byte[] ordrtBytes = order.getBytes();
        byte[] amount_to_loanBates = amount_to_loan.toBytes();
        byte[] loan_periodBytes = Varint.intToByteArray(loan_period);
        byte[] inter_restByte = Varint.intToByteArray(interest_rate);
        byte[] repayment_typeByte = Varint.intToByteArray(repayment_type);
        byte[] amount_to_collateralizeBytes = amount_to_collateralize.toBytes();
        byte[] collateral_rateBytes = Varint.intToByteArray(collateral_rate);
        byte[] bid_periodBytes =  Varint.intToByteArray(bid_period);
        byte[] memoBytes =memo.toBytes();
        byte[] extensionsBytes = extensions.toBytes();
        return Bytes.concat(feeBytes, issuerBytes,carrierBytes,ordrtBytes,amount_to_loanBates,loan_periodBytes,inter_restByte, repayment_typeByte,amount_to_collateralizeBytes
        ,collateral_rateBytes,bid_periodBytes,memoBytes,extensionsBytes);
    }

    @Override
    public String toJsonString() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(BitlenderLoanOperation.class, new BitlenderLoanOperation.TransferSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public byte getId() {
        return super.getId();
    }

    @Override
    public void setFee(AssetAmount assetAmount) {
        this.fee = assetAmount;

    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null)
            jsonObject.add(KEY_FEE, fee.toJsonObject());
        jsonObject.addProperty(CARRIER, carrier.getObjectId());
        jsonObject.add(AMOUNT_TO_LOAN,amount_to_loan.toJsonObject());
        jsonObject.addProperty(ORDER,order);
        jsonObject.addProperty(LOAN_PERIOD,loan_period);
        jsonObject.addProperty(INTEREST_RATE, interest_rate);
        jsonObject.addProperty(REPAYMENT_TYPE,repayment_type);
        jsonObject.add(AMOUNT_TO_COLLATERALIZE,amount_to_collateralize.toJsonObject());
        jsonObject.addProperty(COLLATERAL_RATE,collateral_rate);
        jsonObject.addProperty(BID_PERIOD, bid_period);
        jsonObject.add(MEMO,memo.toJsonObject());
        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        jsonObject.addProperty(ISSUER, issuer.getObjectId());
        array.add(jsonObject);
        return array;
    }


    public static class TransferSerializer implements JsonSerializer<LocktokenRemoveOperation> {

        @Override
        public JsonElement serialize(LocktokenRemoveOperation transfer, Type type, JsonSerializationContext jsonSerializationContext) {
            return transfer.toJsonObject();
        }
    }

    public static class TransferDeserializer implements JsonDeserializer<BitlenderLoanOperation> {

        @Override
        public BitlenderLoanOperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {


            if (json.isJsonArray()) {
                // This block is used just to check if we are in the first step of the deserialization
                // when we are dealing with an array.
                JsonArray serializedTransfer = json.getAsJsonArray();
                if (serializedTransfer.get(0).getAsInt() != OperationType.TRANSFER_OPERATION.ordinal()) {
                    // If the operation type does not correspond to a transfer operation, we return null
                    return null;
                } else {
                    // Calling itself recursively, this is only done once, so there will be no problems.
                    return context.deserialize(serializedTransfer.get(1), LocktokenNodeOperation.class);
                }
            }else{
                // This block is called in the second recursion and takes care of deserializing the
                // transfer data itself.
                JsonObject jsonObject = json.getAsJsonObject();
                UserAccount userAccoun = context.deserialize(jsonObject.get(ISSUER), UserAccount.class);
                UserAccount userAccount = context.deserialize(jsonObject.get(CARRIER), UserAccount.class);
                AssetAmount aserAccount = context.deserialize(jsonObject.get(AMOUNT_TO_LOAN), AssetAmount.class);
                String order = jsonObject.get(ORDER).getAsString();
                int loan_period = Integer.parseInt(jsonObject.get(LOAN_PERIOD).getAsString());
                int interest_rate = Integer.parseInt(jsonObject.get(INTEREST_RATE).getAsString());
                int repayment_type = Integer.parseInt(jsonObject.get(REPAYMENT_TYPE).getAsString());
                AssetAmount assetAmount = context.deserialize(jsonObject.get(AMOUNT_TO_COLLATERALIZE), AssetAmount.class);
                int collateral_rate = Integer.parseInt(jsonObject.get(COLLATERAL_RATE).getAsString());
                int bid_period = Integer.parseInt(jsonObject.get(BID_PERIOD).getAsString());
                Memo memo = context.deserialize(jsonObject.get(MEMO), Memo.class);
                Extensions extensions = context.deserialize(jsonObject.get(KEY_EXTENSIONS), Extensions.class);
                AssetAmount fee = context.deserialize(jsonObject.get(KEY_FEE), AssetAmount.class);

                BitlenderLoanOperation bitlenderLoanOperation = new BitlenderLoanOperation(fee,userAccoun,userAccount,
                        order,aserAccount,loan_period,interest_rate,repayment_type,assetAmount,collateral_rate,bid_period, memo,extensions);
                return bitlenderLoanOperation;
            }
        }
    }



}
