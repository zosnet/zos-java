package com.zos.common.ws.client.impl;

import com.google.gson.JsonElement;
import com.zos.common.ws.client.graphenej.models.ApiCall;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author liruobin
 * @since 2018/7/5 上午10:33
 */
public interface ChainApiService {
    @POST("/rpc")
    Call<WitnessResponse<JsonElement>> call(@Body ApiCall apiCall);
}
