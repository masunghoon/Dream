package com.vivavu.dream.repository;

import android.util.Log;

import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.model.Status;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketWrapped;
import com.vivavu.dream.model.bucket.Plan;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.model.user.UserWrapped;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuja on 14. 1. 13.
 */
public class DataRepository {
    private static DreamApp context;

    public DataRepository(DreamApp context) {
        this.context = context;
    }

    private static HttpHeaders getBasicAuthHeader(){
        return getBasicAuthHeader(context.getToken(), "unused");
    }
    private static HttpHeaders getBasicAuthHeader(String username, String password){
        HttpAuthentication httpAuthentication = new HttpBasicAuthentication(username, password);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAuthorization(httpAuthentication);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));

        return httpHeaders;
    }

    public static SecureToken getToken(String email, String password){

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(email, password);
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        try{
            ResponseEntity<SecureToken> result = restTemplate.exchange(Constants.apiToken, HttpMethod.GET, request, SecureToken.class);
            return result.getBody();
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        return null;
    }

    public static BaseInfo getBaseInfo(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        try{
            ResponseEntity<BaseInfo> result = RestTemplateFactory.getInstance().exchange(Constants.apiBaseInfo, HttpMethod.GET, request, BaseInfo.class);
            return  result.getBody();
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        return null;
    }

    public static List<Bucket>  getBuckets() {
        return getBucketsV2(context.getUsername());
    }
    public static List<Bucket> getBuckets(String username) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);

        //Users users = restTemplate.getForObject(Constants.apiUsers, Users.class);
        ResponseEntity<Bucket[]> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBuckets, HttpMethod.GET, request, Bucket[].class, username);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Bucket[] buckets = result.getBody();

        return new ArrayList<Bucket>(Arrays.asList(buckets));
    }

    public static List<Bucket> getBucketsV2(String username) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);

        //Users users = restTemplate.getForObject(Constants.apiUsers, Users.class);
        ResponseEntity<Bucket[]> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketsV2, HttpMethod.GET, request, Bucket[].class, username);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Bucket[] buckets = result.getBody();

        return new ArrayList<Bucket>(Arrays.asList(buckets));
    }

    public static Bucket getBucket(Integer id){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);
        //HttpEntity request = new HttpEntity<String>( new HttpHeaders());

        ResponseEntity<Bucket> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.GET, request, Bucket.class, id);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Bucket bucket = result.getBody();

        return bucket;
    }

    public static Bucket getBucketV2(Integer id){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);
        //HttpEntity request = new HttpEntity<String>( new HttpHeaders());

        ResponseEntity<Bucket> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfoV2, HttpMethod.GET, request, Bucket.class, id);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Bucket buckets = result.getBody();

/*
        ResponseEntity<Bucket> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfoV2, HttpMethod.GET, request, Bucket.class, id);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Bucket bucket = result.getBody();
*/

        return buckets;
    }

    public static Bucket postBucketDefault(Bucket bucket, Object... variable){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<Bucket>(bucket, requestHeaders);

        ResponseEntity<BucketWrapped> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBuckets, HttpMethod.POST, request, BucketWrapped.class, variable);
            return result.getBody().getBucket();
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        Log.d("dream", String.valueOf(result));

        return null;
    }

    public static Bucket updateBucketInfo(Bucket bucket){
        //return sendBucketInfo(Constants.apiBucketInfo, HttpMethod.PUT, bucket);

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<Bucket>(bucket, requestHeaders);

        ResponseEntity<BucketWrapped> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.PUT, request, BucketWrapped.class, bucket.getId());
            return result.getBody().getBucket();
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        Log.d("dream", String.valueOf(result));
        return null;
    }

    public static void deleteBucket(Integer bucketId){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<String> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.DELETE, request, String.class, bucketId);
            return ;
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        Log.d("dream", String.valueOf(result));

        return ;
    }

    public static User getUserInfo(String username){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);
        //HttpEntity request = new HttpEntity<String>( new HttpHeaders());

        ResponseEntity<UserWrapped> result = null;
        try {
            result = restTemplate.exchange(Constants.apiUserInfo, HttpMethod.GET, request, UserWrapped.class, username);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        User user = result.getBody().getUser();

        return user;
    }

    public static List<Plan> getPlanList(Object... variable){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<Plan[]> result = null;
        try {
            result = restTemplate.exchange(Constants.apiPlanList, HttpMethod.GET, request, Plan[].class, variable);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Plan[] user = result.getBody();

        return new ArrayList<Plan>(Arrays.asList(user));
    }

    public static Status updatePlanStatus(Plan plan, Object... variable){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();


        HashMap<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("id", plan.getId());
        requestBody.put("isDone", plan.getIsDone()?1:0);

        HttpEntity request = new HttpEntity<Map>(requestBody, requestHeaders);

        ResponseEntity<Status> result = null;
        try {
            result = restTemplate.exchange(Constants.apiPlanInfo, HttpMethod.PUT, request, Status.class, plan.getId());
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        return result.getBody();

    }
    public static DreamApp getContext() {
        return context;
    }

    public static void setContext(DreamApp context) {
        DataRepository.context = context;
    }
}
