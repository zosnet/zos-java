package com.zos.common.ws.client.graphenej.models;

import com.zos.common.ws.client.graphenej.objects.Transaction;
import lombok.Data;

/**
 * @author liruobin
 * @since 2018/7/5 下午4:36
 */
@Data
public class Block {
    public String version;
    public String previous;
    public String timestamp;
    public String witness;
    public String transaction_merkle_root;
    public Object[] extensions;
    public String witness_signature;
    public String block_id;
    public String signing_key;
    public String[] transaction_ids;
    public Transaction[] transactions;
    public String[] trxids;
}
