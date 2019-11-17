package com.zos.common.ws.client.graphenej.objects;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedLong;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.zos.common.ws.client.graphenej.Address;
import com.zos.common.ws.client.graphenej.PublicKey;
import com.zos.common.ws.client.graphenej.Util;
import com.zos.common.ws.client.graphenej.Varint;
import com.zos.common.ws.client.graphenej.interfaces.ByteSerializable;
import com.zos.common.ws.client.graphenej.interfaces.GrapheneSerializable;
import com.zos.common.ws.client.graphenej.interfaces.JsonSerializable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AccountAuthorZos extends GrapheneObject implements ByteSerializable, JsonSerializable, GrapheneSerializable {
    public static final String KEY_HASH64 = "hash64";
    public static final String KEY_STATE = "state";
    public static final String KEY_EXPIRATION = "expiration";
    @Expose
    private UnsignedLong hash64;

    @Expose
    private int state;

    @Expose
    private int expiration;

    public AccountAuthorZos(String id, int state, long hash64, int expiration) {
        super(id);
        this.state = state;
        this.expiration = expiration;
        this.hash64 = UnsignedLong.valueOf(hash64);
    }

    public byte[] toBytes() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteArrayOutputStream);
        try {
            Varint.writeUnsignedVarLong(this.instance, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] x = new byte[]{1};
        byte[] state = IntToByteArray(this.state);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(this.expiration);
        byte[] expirationBytes = Util.revertBytes(buffer.array());
        byte[] account = byteArrayOutputStream.toByteArray();
        byte[] hash64 = Util.revertLong(this.hash64.longValue());

        return Bytes.concat(x, state, expirationBytes, account, hash64);
//        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public JsonElement toJsonObject() {
        JsonObject jsonAmount = new JsonObject();
        jsonAmount.addProperty(KEY_STATE, state);
        jsonAmount.addProperty(KEY_EXPIRATION, expiration);
        jsonAmount.addProperty("account", id);
        jsonAmount.addProperty(KEY_HASH64, hash64);
        return jsonAmount;
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
