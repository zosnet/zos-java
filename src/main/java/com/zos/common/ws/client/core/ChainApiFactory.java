package com.zos.common.ws.client.core;


import com.zos.common.ws.client.constant.WSConstants;
import com.zos.common.ws.client.exception.HttpAccessFailException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.zos.common.ws.client.util.Config;

public class ChainApiFactory {
    private static final Logger logger = LoggerFactory.getLogger(ChainApiFactory.class);
    private ConcurrentHashMap<Class<?>, Object> typeCache = new ConcurrentHashMap<>();

    private Retrofit retrofit;

    private ChainApiFactory(final String baseUrl, final Long timeout) {
        OkHttpClient httpClient = new OkHttpClient().newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).addInterceptor(new Interceptor() {

            /*
             * 记录访问日志，统一接口异常处理
             *
             * @see okhttp3.Interceptor#intercept(okhttp3.Interceptor.Chain)
             */
            @Override
            public Response intercept(Chain chain) throws IOException {
//                logger.info("Chain api request:" + chain.request().toString());
                Response response = chain.proceed(chain.request());
//                logger.info("Chain response:" + response.toString());
                if (!response.isSuccessful()) {
                    throw new HttpAccessFailException(response.body().string());
                } else {
                    return response;
                }
            }

        }).writeTimeout(timeout, TimeUnit.MILLISECONDS).build();
        httpClient.dispatcher().setMaxRequestsPerHost(100);
        httpClient.dispatcher().setMaxRequests(150);

        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ChainGsonConverterFactory.create()).callFactory(httpClient).build();
    }


    public <T> T newApi(Class<T> clz) {
        Object object = typeCache.get(clz);
        if (object != null) {
            return (T) object;
        } else {
            if (clz.isInterface()) {
                T result = retrofit.create(clz);
                typeCache.putIfAbsent(clz, result);
                return result;
            } else {
                throw new IllegalArgumentException("interface class required");
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String baseUrl = WSConstants.HTTP_URL;
        private Long timeout = 15000L; // default 15s;

        public Builder baseUrl(String url) {
            this.baseUrl = url;
            return this;
        }

        public Builder timeout(Long ms) {
            this.timeout = ms;
            return this;
        }

        public ChainApiFactory build() {
            if (this.baseUrl == null || this.baseUrl.isEmpty()) {
                this.baseUrl = Config.getConfigProperties("httpUrl");
            }
            if (this.baseUrl == null || this.baseUrl.isEmpty()) {
                throw new IllegalArgumentException("baseUrl invalid");
            }
            return new ChainApiFactory(this.baseUrl, timeout);
        }
    }
}
