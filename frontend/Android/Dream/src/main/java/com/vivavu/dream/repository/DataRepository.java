package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.model.Status;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketWrapped;
import com.vivavu.dream.model.bucket.Plan;
import com.vivavu.dream.model.user.User;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
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

    public static ResponseBodyWrapped<SecureToken> registUser(LoginInfo loginInfo){
        loginInfo.setCommand("register");
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpEntity request = new HttpEntity<LoginInfo>(loginInfo);
        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiUsers, HttpMethod.POST, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type type = new TypeToken<ResponseBodyWrapped<SecureToken>>(){}.getType();
        ResponseBodyWrapped<SecureToken> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);

        return responseBodyWrapped;
    }

    public static ResponseBodyWrapped<SecureToken> getToken(String email, String password){

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(email, password);
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiToken, HttpMethod.GET, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
            return new ResponseBodyWrapped<SecureToken>("error", "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
        }

        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<SecureToken>>(){}.getType();
            ResponseBodyWrapped<SecureToken> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            return responseBodyWrapped;
        }else if (result.getStatusCode() == HttpStatus.UNAUTHORIZED){
            return new ResponseBodyWrapped<SecureToken>("error", "사용자 정보 확인 필요", null);
        }
        return new ResponseBodyWrapped<SecureToken>();
    }

    public static ResponseBodyWrapped<BaseInfo> getBaseInfo(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiBaseInfo, HttpMethod.GET, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        ResponseBodyWrapped<BaseInfo> responseBodyWrapped = null;
        if(result.getStatusCode() == HttpStatus.OK || result.getStatusCode()== HttpStatus.NO_CONTENT){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<BaseInfo>>(){}.getType();
            responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            //GsonBuilder
            return responseBodyWrapped;
        }

        return new ResponseBodyWrapped<BaseInfo>();
    }

    public static List<Bucket>  getBuckets() {
        return getBuckets(context.getUser().getId());
    }
    public static List<Bucket> getBuckets(Integer userId) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();

        HttpHeaders requestHeaders = getBasicAuthHeader();

        HttpEntity request = new HttpEntity<String>(requestHeaders);

        //Users users = restTemplate.getForObject(Constants.apiUsers, Users.class);
        ResponseEntity<String> result = null;
        try {
            result = restTemplate.exchange(Constants.apiBuckets, HttpMethod.GET, request, String.class, userId);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        if(result != null){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<ArrayList<Bucket>>>(){}.getType();
            //ResponseBodyWrapped<ResponseBodyWrapped> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
        }

        return new ArrayList<Bucket>();
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

    public static User getUserInfo(Integer userId){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> result = null;

        try {
            result = restTemplate.exchange(Constants.apiUserInfo, HttpMethod.GET, request, String.class, userId);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Type type = new TypeToken<ResponseBodyWrapped<User>>(){}.getType();
        ResponseBodyWrapped<User> user = gson.fromJson((String) result.getBody(), type);


        return user.getData();
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

    public static ResponseBodyWrapped<LoginInfo> resetPassword(String email){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader();
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> result = null;

        try {
            result = restTemplate.exchange(Constants.apiResetPassword, HttpMethod.POST, request, String.class, email);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<LoginInfo>>(){}.getType();
            ResponseBodyWrapped<LoginInfo> user = gson.fromJson((String) result.getBody(), type);
            return user;
        }

        return new ResponseBodyWrapped<LoginInfo>();
    }
    public static DreamApp getContext() {
        return context;
    }

    public static void setContext(DreamApp context) {
        DataRepository.context = context;
    }
}
