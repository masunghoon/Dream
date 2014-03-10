package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class Connector {
    private static DreamApp context;

    public HttpHeaders getBasicAuthHeader(DreamApp context){
        if(context.getTokenType() !=null &&  !"facebook".equals(context.getTokenType())){
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "unused");
        }
        else{
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "facebook");
        }
    }

    public ResponseBodyWrapped<List<Bucket>> getBucketList(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;

        try {
            resultString = restTemplate.exchange(Constants.apiBucketGroup, HttpMethod.GET, request, String.class, getContext().getUser().getId());
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<List<Bucket>> result = new ResponseBodyWrapped<List<Bucket>>("error", String.valueOf(resultString.getStatusCode()), new ArrayList<Bucket>());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<List<BucketGroup>>>(){}.getType();
            ResponseBodyWrapped<List<BucketGroup>> bucketGroup = gson.fromJson((String) resultString.getBody(), type);

            result.setStatus(bucketGroup.getStatus());
            result.setDescription(bucketGroup.getDescription());
            List<Bucket> bucketList = result.getData();
            for(BucketGroup group : bucketGroup.getData()){
                    bucketList.addAll(group.getBukets());
            }
            result.setData(bucketList);
        }

        return result;
    }



    public static DreamApp getContext() {
        return context;
    }

    public static void setContext(DreamApp context) {
        Connector.context = context;
    }
}
