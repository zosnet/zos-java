package com.zos.common.ws.client.api.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class testTemp {
    private int channelType;
    private String account_no;
    private String accountName;
    private BigDecimal amount;
    private String coin_type;
    private String userId;
}
