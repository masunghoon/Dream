package com.vivavu.dream.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vivavu.dream.common.DreamApp;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Type;
import java.util.Collections;

/**
 * Created by yuja on 14. 2. 21.
 */
public class RestTemplateUtils {
    private static DreamApp context;

    public static DreamApp getContext() {
        return context;
    }

    public static void setContext(DreamApp context) {
        RestTemplateUtils.context = context;
    }

    public RestTemplateUtils(DreamApp context) {
        this.context = context;
    }

    private static HttpHeaders getBasicAuthHeader(){

        if(context.getTokenType() !=null &&  !"facebook".equals(context.getTokenType())){
            return getBasicAuthHeader(context.getToken(), "unused");
        }
        else{
            return getBasicAuthHeader(context.getToken(), "facebook");
        }

    }
    private static HttpHeaders getBasicAuthHeader(String username, String password){
        HttpAuthentication httpAuthentication = new HttpBasicAuthentication(username, password);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAuthorization(httpAuthentication);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));

        return httpHeaders;
    }

    public static <T> T responseToJson(ResponseEntity<String> responseEntity, Type typeOfT ){
        T result = null;
        HttpHeaders headers = responseEntity.getHeaders();
        if(headers != null && headers.getContentType() == MediaType.APPLICATION_JSON){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = gson.fromJson((String) responseEntity.getBody(), typeOfT);
        }

        return result;
    }

}
