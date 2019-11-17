package com.zos.common.ws.client.graphenej.operations;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLong;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zos.common.ws.client.graphenej.PublicKey;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.enums.OperationType;
import com.zos.common.ws.client.graphenej.objects.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CertificationOperation extends BaseOperation {

    private AssetAmount fee;
    private UserAccount issuer;
    private int op_type;
    private Optional<Authority> auth_flag;
    private Optional<Authority> lock_balance;
    private Optional<Authority> auth_key;
    private Optional<AccountAuthorZos> auth_account;
    private Optional<Authority> auth_data;
    private Optional<Authority> flags;
    private Optional<Authority> need_auth;
    private Optional<Authority> trust_auth;
    private Optional<Authority> user_info;
    private Extensions requiredAuths;

    public CertificationOperation() {
        super(OperationType.ACCOUNT_AUTHENTICATE);
        this.auth_flag = new Optional<>(null);
        this.lock_balance = new Optional<>(null);
        this.auth_key = new Optional<>(null);
        this.auth_data = new Optional<>(null);
        this.flags = new Optional<>(null);
        this.need_auth = new Optional<>(null);
        this.trust_auth = new Optional<>(null);
        this.user_info = new Optional<>(null);
//        this.auth_account = new Optional<>(null);
    }

    @Override
    public byte[] toBytes() {

        byte[] x = new byte[]{1};
        byte[] feeBytes = fee.toBytes();
        byte[] issuerBytes = issuer.toBytes();
        byte[] op_typeBytes = IntToByteArray(op_type);
        byte[] auth_flagBytes = auth_flag.toBytes();
        byte[] lock_balanceBytes = lock_balance.toBytes();
        byte[] auth_keyBytes = auth_key.toBytes();
        byte[] auth_accountBytes = auth_account.toBytes();
        byte[] auth_dataBytes = auth_data.toBytes();
        byte[] flagsBytes = flags.toBytes();
        byte[] need_authBytes = need_auth.toBytes();
        byte[] trust_authBytes = trust_auth.toBytes();
        byte[] user_infoBytes = user_info.toBytes();
        byte[] requireAuthsBytes = requiredAuths.toBytes();
        return Bytes.concat(feeBytes, issuerBytes, op_typeBytes, auth_flagBytes, lock_balanceBytes, auth_keyBytes, auth_accountBytes,
                auth_dataBytes, flagsBytes, need_authBytes, trust_authBytes, user_infoBytes);
//        return Bytes.concat(feeBytes, issuerBytes, op_typeBytes,auth_accountBytes,requireAuthsBytes);
    }

    @Override
    public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override
    public JsonElement toJsonObject() {
        JsonArray array = new JsonArray();
        array.add(this.getId());
        JsonObject jsonObject = new JsonObject();
        if (fee != null)
            jsonObject.add("fee", fee.toJsonObject());
        jsonObject.addProperty("issuer", issuer.getObjectId());
        jsonObject.addProperty("op_type", op_type);
        jsonObject.add("auth_account", auth_account.toJsonObject());
        jsonObject.add("extensions", new JsonArray());
        array.add(jsonObject);
        return array;
    }

    public byte[] IntToByteArray(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
}
