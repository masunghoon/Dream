package com.vivavu.dream.repository;

import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.util.RestTemplateUtils;

import org.springframework.http.HttpHeaders;

/**
 * Created by yuja on 14. 3. 6.
 */
public abstract class Connector <T> {
    private static DreamApp context;

    public HttpHeaders getBasicAuthHeader(DreamApp context){
        if(context.getTokenType() !=null &&  !"facebook".equals(context.getTokenType())){
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "unused");
        }
        else{
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "facebook");
        }
    }

    public abstract ResponseBodyWrapped<T> post(T data);
    public abstract ResponseBodyWrapped<T> put(T data);
    public abstract ResponseBodyWrapped<T> get(T data);
    public abstract ResponseBodyWrapped<T> delete(T data);

    public static DreamApp getContext() {
        return context;
    }

    public static void setContext(DreamApp context) {
        Connector.context = context;
    }
}
