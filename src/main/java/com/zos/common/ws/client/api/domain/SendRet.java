package com.zos.common.ws.client.api.domain;

import lombok.Data;

@Data
public class SendRet {
    private String txId;
    private String trxNum;
    private String blockNum;
}
