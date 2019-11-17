package com.zos.common.ws.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.gson.*;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.enums.OperationType;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.objects.Locktoken;
import com.zos.common.ws.client.graphenej.objects.Memo;
import com.zos.common.ws.client.graphenej.objects.UserAccount;

import java.lang.reflect.Type;

/**
 * Class used to encapsulate the LocktokenNodeOperation operation related functionalities.
 */
public class LocktokenNodeOperation extends BaseOperation {

    public static final String KEY_FROM = "issuer";
    public static final String LOCKTOKEN = "locktoken_id";
    public static final String OPTYPE = "op_type";
    public static final String KEY_TO = "to";
    public static final String KEY_AMOUNT = "to_amount";
    public static final String PERIOD = "period";



    private AssetAmount fee;
    private UserAccount issuer;
    private Locktoken locktoken;
    private int optype;
    private UserAccount to;
    private AssetAmount toamount;
    private int period;

//    public LocktokenNodeOperation(UserAccount from, UserAccount to, AssetAmount transferAmount, AssetAmount fee) {
//        super(OperationType.TRANSFER_OPERATION);
//        this.from = from;
//        this.to = to;
//        this.amount = transferAmount;
//        this.fee = fee;
//    }

//    public LocktokenNodeOperation(UserAccount from, UserAccount to, AssetAmount transferAmount) {
//        super(OperationType.TRANSFER_OPERATION);
//        this.from = from;
//        this.to = to;
//        this.amount = transferAmount;
//    }

    public LocktokenNodeOperation(AssetAmount fee,UserAccount issuer,Locktoken locktoken,int optype,UserAccount to,AssetAmount toamount,int period) {
        super(OperationType.LOCKTOKEN_AWARDS);
        this.fee = fee;
        this.issuer = issuer;
        this.locktoken = locktoken;
        this.optype = optype;
        this.to = to;
        this.toamount = toamount;
        this.period = period;
    }


    public UserAccount getTo() {
        return this.to;
    }


    public AssetAmount getFee() {
        return this.fee;
    }

    public void setPeriod(int period){
        this.period = period;
    }

    public void setTo(UserAccount to) {
        this.to = to;
    }


    @Override
    public void setFee(AssetAmount newFee) {
        this.fee = newFee;
    }

    @Override
    public byte[] toBytes() {
        byte[] feeBytes = fee.toBytes();
        byte[] issuerBytes = issuer.toBytes();
        byte[] locktokenBytes = locktoken.toBytes();
        byte[] optypeBytes = Varint.intToByteArray(optype);
        byte[] toBytes = to.toBytes();
        byte[] toamountBytes =toamount.toBytes();
        byte[] periodBytes = Varint.intToByteArray(period);
        byte[] extensions = this.extensions.toBytes();
        return Bytes.concat(feeBytes, issuerBytes,locktokenBytes,optypeBytes,toBytes,toamountBytes,periodBytes, extensions);
    }

    @Override
    public String toJsonString() {
        //TODO: Evaluate using simple Gson class to return a simple string representation and drop the TransferSerializer class
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocktokenNodeOperation.class, new TransferSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null)
            jsonObject.add(KEY_FEE, fee.toJsonObject());
        jsonObject.addProperty(KEY_TO, to.getObjectId());
        jsonObject.addProperty(KEY_FROM,issuer.getObjectId());
        jsonObject.addProperty(LOCKTOKEN,locktoken.getObjectId());
        jsonObject.addProperty(OPTYPE,optype);
        jsonObject.add(KEY_AMOUNT, toamount.toJsonObject());
        jsonObject.addProperty(PERIOD,period);
        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        array.add(jsonObject);
        return array;

    }


    public static class TransferSerializer implements JsonSerializer<LocktokenNodeOperation> {

        @Override
        public JsonElement serialize(LocktokenNodeOperation transfer, Type type, JsonSerializationContext jsonSerializationContext) {
//            JsonArray arrayRep = new JsonArray();
//            arrayRep.add(transfer.getId());
//            arrayRep.add(transfer.toJsonObject());
//            return arrayRep;
            return transfer.toJsonObject();
        }
    }

    /**
     * This deserializer will work on any transfer operation serialized in the 'array form' used a lot in
     * the Graphene Blockchain API.
     * <p>
     * An example of this serialized form is the following:
     * <p>
     * [
     * 0,
     * {
     * "fee": {
     * "amount": 264174,
     * "asset_id": "1.3.0"
     * },
     * "from": "1.2.138632",
     * "to": "1.2.129848",
     * "amount": {
     * "amount": 100,
     * "asset_id": "1.3.0"
     * },
     * "extensions": []
     * }
     * ]
     * <p>
     * It will convert this data into a nice TransferOperation object.
     */
    public static class TransferDeserializer implements JsonDeserializer<LocktokenNodeOperation> {

        @Override
        public LocktokenNodeOperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
            } else {
                // This block is called in the second recursion and takes care of deserializing the
                // transfer data itself.
                JsonObject jsonObject = json.getAsJsonObject();

                // Deserializing AssetAmount objects
                AssetAmount toamount = context.deserialize(jsonObject.get(KEY_AMOUNT), AssetAmount.class);
                int period = Integer.parseInt(jsonObject.get(PERIOD).getAsString());
                AssetAmount fee = context.deserialize(jsonObject.get(KEY_FEE), AssetAmount.class);

                // Deserializing UserAccount objects
                UserAccount issuer = new UserAccount(jsonObject.get(KEY_FROM).getAsString());
                UserAccount to = new UserAccount(jsonObject.get(KEY_TO).getAsString());
                Locktoken locktoken = new Locktoken(jsonObject.get(LOCKTOKEN).getAsString());
                int optype = Integer.parseInt(jsonObject.get(OPTYPE).getAsString());

                LocktokenNodeOperation locktokenNodeOperation = new LocktokenNodeOperation(fee,issuer,locktoken,optype,to,toamount,period);

                return locktokenNodeOperation;
            }
        }
    }
}
