package com.zos.common.ws.client;


import com.zos.common.ws.client.constant.WSConstants;
import com.zos.common.ws.client.impl.BitlenderClientImpl;
import com.zos.common.ws.client.impl.ChainApiRestClientImpl;
import com.zos.common.ws.client.impl.ChainWebSocketClientImpl;

/**
 * Chain client 工厂类
 *
 * @author liruobin
 * @since 2018/7/3 上午10:15
 */
public class ChainClientFactory {
    private static ChainClientFactory clientFactory = new ChainClientFactory();

    public static ChainClientFactory getInstance() {
        return clientFactory;
    }
    /**
     * 创建webSocket client
     *
     * @param wsUrl
     * @return
     */
    public ChainWebSocketClient newWebSocketClient(String wsUrl) {
        return new ChainWebSocketClientImpl(wsUrl);
    }

    /**
     * 创建webSocket client
     *
     * @return
     */
    public ChainWebSocketClient newWebSocketClient() {
        return new ChainWebSocketClientImpl(WSConstants.WS_URL);
    }

    /**
     * 创建webSocket client
     *
     * @return
     */
    public BitlenderClient lenderClient(String url) {
        return new BitlenderClientImpl(url);
    }

    /**
     * 创建http client
     * @param url
     * @return
     */
    public ChainApiRestClient lenderRestCLient(String url) {
        return new ChainApiRestClientImpl(url);
    }

    /**
     * 创建http client
     * @param url
     * @return
     */
    public ChainApiRestClient newRestCLient(String url) {
        return new ChainApiRestClientImpl(url);
    }
}
