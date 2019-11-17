package com.zos.common.ws.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zos.common.ws.client.graphenej.enums.OperationType;
import com.zos.common.ws.client.graphenej.objects.AssetAmount;
import com.zos.common.ws.client.graphenej.objects.Extensions;
import com.zos.common.ws.client.graphenej.objects.UserAccount;
import lombok.Data;

@Data
public class VertifyTransactionOperation {
//    private AssetAmount fee;
//    private AssetAmount deposit;
//    private UserAccount from;
//    private UserAccount to;
//    private Extensions requiredAuths;
//
//    public VertifyTransactionOperation() {
////        super(OperationType.DEPOSIT_OPERATION);
//    }
//
//    @Override
//    public byte[] toBytes() {
//        byte[] feeBytes = fee.toBytes();
//        byte[] depositBytes = deposit.toBytes();
//        byte[] fromBytes = from.toBytes();
//        byte[] toBytes = to.toBytes();
//        byte[] requireAuthsBytes = requiredAuths.toBytes();
//        return Bytes.concat(feeBytes, fromBytes, toBytes, depositBytes, requireAuthsBytes);
//    }
//
//    @Override
//    public String toJsonString() {
//        return toJsonObject().toString();
//    }
//
//    @Override
//    public JsonElement toJsonObject() {
//        JsonArray array = new JsonArray();
//        array.add(this.getId());
//        JsonObject jsonObject = new JsonObject();
//        if (fee != null)
//            jsonObject.add("fee", fee.toJsonObject());
//        jsonObject.add("deposit", deposit.toJsonObject());
//        jsonObject.addProperty("from", from.getObjectId());
//        jsonObject.addProperty("to", to.getObjectId());
//        array.add(jsonObject);
//        return array;
//    }
}
