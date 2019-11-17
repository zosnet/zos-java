package com.zos.common.ws.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.gson.*;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.enums.OperationType;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.objects.Locktoken;
import com.zos.common.ws.client.graphenej.objects.UserAccount;

import java.lang.reflect.Type;

/**
 * Class used to encapsulate the LocktokenNodeOperation operation related functionalities.
 */
public class LocktokenRemoveOperation extends BaseOperation {

    public static final String KEY_FROM = "issuer";
    public static final String LOCKTOKEN = "locktoken_id";


    private AssetAmount fee;
    private UserAccount issuer;
    private Locktoken locktoken;

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

    public LocktokenRemoveOperation(AssetAmount fee, UserAccount issuer, Locktoken locktoken) {
        super(OperationType.LOCKTOKEN_REMOVE);
        this.fee = fee;
        this.issuer = issuer;
        this.locktoken = locktoken;
    }




    public AssetAmount getFee() {
        return this.fee;
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
        byte[] extensions = this.extensions.toBytes();
        return Bytes.concat(feeBytes, issuerBytes,locktokenBytes, extensions);
    }

    @Override
    public String toJsonString() {
        //TODO: Evaluate using simple Gson class to return a simple string representation and drop the TransferSerializer class
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocktokenRemoveOperation.class, new TransferSerializer());
        return gsonBuilder.create().toJson(this);
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null)
            jsonObject.add(KEY_FEE, fee.toJsonObject());
        jsonObject.addProperty(KEY_FROM,issuer.getObjectId());
        jsonObject.addProperty(LOCKTOKEN,locktoken.getObjectId());
        jsonObject.add(KEY_EXTENSIONS, new JsonArray());
        array.add(jsonObject);
        return array;

    }


    public static class TransferSerializer implements JsonSerializer<LocktokenRemoveOperation> {

        @Override
        public JsonElement serialize(LocktokenRemoveOperation transfer, Type type, JsonSerializationContext jsonSerializationContext) {
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
    public static class TransferDeserializer implements JsonDeserializer<LocktokenRemoveOperation> {

        @Override
        public LocktokenRemoveOperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray()) {
                // This block is used just to check if we are in the first step of the deserialization
                // when we are dealing with an array.
                JsonArray serializedTransfer = json.getAsJsonArray();
                if (serializedTransfer.get(0).getAsInt() != OperationType.TRANSFER_OPERATION.ordinal()) {
                    // If the operation type does not correspond to a transfer operation, we return null
                    return null;
                } else {
                    // Calling itself recursively, this is only done once, so there will be no problems.
                    return context.deserialize(serializedTransfer.get(1), LocktokenRemoveOperation.class);
                }
            } else {
                // This block is called in the second recursion and takes care of deserializing the
                // transfer data itself.
                JsonObject jsonObject = json.getAsJsonObject();

                // Deserializing AssetAmount objects
                AssetAmount fee = context.deserialize(jsonObject.get(KEY_FEE), AssetAmount.class);

                // Deserializing UserAccount objects
                UserAccount issuer = new UserAccount(jsonObject.get(KEY_FROM).getAsString());
                Locktoken locktoken = new Locktoken(jsonObject.get(LOCKTOKEN).getAsString());

                LocktokenRemoveOperation locktokenNodeOperation = new LocktokenRemoveOperation(fee,issuer,locktoken);

                return locktokenNodeOperation;
            }
        }
    }
}
