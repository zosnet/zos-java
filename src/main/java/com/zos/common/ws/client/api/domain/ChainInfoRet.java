package com.zos.common.ws.client.api.domain;

import lombok.Data;

@Data
public class ChainInfoRet {
    private String expirationTime;
    private String headBlockId;
    private long headBlockNumber;
    private String chainId;
}
