package com.zos.common.ws.client.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"ret_code", "ret_msg", "data"})
public class ReturnInfo {

    public final static ReturnInfo SUCCESS = new ReturnInfo("1", "success");
    public final static ReturnInfo FAILED = new ReturnInfo("0", "failed");

    @JsonProperty(value = "ret_code")
    private String retCode;

    @JsonProperty(value = "ret_msg")
    private String retMsg;

    private Object data;

    public ReturnInfo(String retCode, String retMsg) {
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    public static void main(String[] args) {
        System.out.println();
    }

}
