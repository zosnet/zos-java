package com.zos.common.ws.client.graphenej.interfaces;

import com.zos.common.ws.client.graphenej.models.BaseResponse;
import com.zos.common.ws.client.graphenej.models.WitnessResponse;

/**
 * Class used to represent any listener to network requests.
 */
public interface WitnessResponseListener {

    void onSuccess(WitnessResponse response);

    void onError(BaseResponse.Error error);
}
