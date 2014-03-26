package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.BucketGroup;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayGroup;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 1. 13.
 */
public class DataRepository {
    public static String TAG = "com.vivavu.dream.repository.DataRepository";
    private static DreamApp context;
    private static DatabaseHelper databaseHelper;

    public DataRepository(DreamApp context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
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
        if(result != null && result.getStatusCode() == HttpStatus.OK || result.getStatusCode()== HttpStatus.NO_CONTENT){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<BaseInfo>>(){}.getType();
            responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            //GsonBuilder
            return responseBodyWrapped;
        }

        return new ResponseBodyWrapped<BaseInfo>();
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
            /*TypeToken.get(user.getClass());
            ResponseBodyWrapped<LoginInfo> usr = RestTemplateUtils.responseToJson(result,type );*/
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

    public static DatabaseHelper getDatabaseHelper() {
        if(databaseHelper == null){
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    public static void saveBuckets(List<Bucket> list){
        deleteAllBuckets();
        for(Bucket bucket : list){
            saveBucket(bucket);
        }
    }

    public static void saveBucket(Bucket bucket){
        if(bucket.getId() != null) {
            getDatabaseHelper().getBucketRuntimeDao().createOrUpdate(bucket);
        }
    }

    public static void deleteAllBuckets(){
        DeleteBuilder<Bucket,Integer> deleteBuilder = getDatabaseHelper().getBucketRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static List<BucketGroup> listBucketGroup(){
        QueryBuilder<Bucket, Integer> qb = getDatabaseHelper().getBucketRuntimeDao().queryBuilder();
        qb.groupBy("range");
        qb.orderBy("range", true);
        qb.orderBy("deadline", true);
        qb.orderBy("id", true);
        List<BucketGroup> bucketGroups = null;
        try {
            List<Bucket> rangeList = qb.query();
            bucketGroups = makeShelfList(rangeList);

            for(BucketGroup range : bucketGroups){
                QueryBuilder<Bucket, Integer> qb2 = getDatabaseHelper().getBucketRuntimeDao().queryBuilder();
                Where where = qb2.where();
                if(range.getRange() == null){
                    where.isNull("range");
                }else{
                    where.eq("range", range.getRange());
                }
                qb2.orderBy("deadline", true);

                List<Bucket> list = qb2.query();
                range.setBukets(list);
            }
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
        return bucketGroups;
    }

    public static List<BucketGroup> makeShelfList(List<Bucket> list){
        List<BucketGroup> returnList = new ArrayList<BucketGroup>();
        returnList.add(new BucketGroup());
        final int maxIndex = 7;
        for (int i = 1; i < maxIndex; i++) {
            returnList.add(new BucketGroup(String.valueOf(i*10)));
        }

        for (Bucket bucket : list){
            String range = bucket.getRange();
            if (range != null) {
                Integer numRange = Integer.parseInt(range);
                if(numRange >= maxIndex * 10){
                    returnList.add(new BucketGroup(String.valueOf(range)));
                }
            }
        }

        return returnList;
    }

    public static Bucket getBucket(Integer id){
        RuntimeExceptionDao<Bucket,Integer> bucketRuntimeDao = getDatabaseHelper().getBucketRuntimeDao();
        Bucket bucket = null;
        if(id != null) {
            bucket = bucketRuntimeDao.queryForId(id);
        }
        if(bucket == null){
            bucket = new Bucket();
        }
        return bucket;
    }

    public static void saveTodays(List<Today> list){
        /*Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        deleteTodays(cal.getTime());*/
        updateTodayGroup();
        for(Today today : list){
            saveToday(today);
        }
    }

    public static void saveToday(Today today){
        if(today.getId() != null) {
            getDatabaseHelper().getTodayRuntimeDao().createOrUpdate(today);
        }
    }

    public static void deleteAllTodays(){
        DeleteBuilder<Today,Integer> deleteBuilder = getDatabaseHelper().getTodayRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static void deleteTodays(Date date){
        DeleteBuilder<Today,Integer> deleteBuilder = getDatabaseHelper().getTodayRuntimeDao().deleteBuilder();
        try {
            Where<Today, Integer> where = deleteBuilder.where();
            where.lt("date", date);
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }

    public static List<TodayGroup> listTodayGroupAndTodayData(){
         List<TodayGroup> todayGroups = null;
        try {
            todayGroups = listTodayGroup();

            for(TodayGroup todayGroup : todayGroups){
                QueryBuilder<Today, Integer> todayQueryBuilder = getDatabaseHelper().getTodayRuntimeDao().queryBuilder();
                Where where = todayQueryBuilder.where();
                if(todayGroup.getDate() == null){
                    where.isNull("date");
                }else{
                    where.eq("date", todayGroup.getDate());
                }
                todayQueryBuilder.orderBy("deadline", true);
                todayQueryBuilder.orderBy("id", true);

                List<Today> list = todayQueryBuilder.query();
                todayGroup.setTodayList(list);
            }
        } catch (SQLException e) {
            todayGroups = new ArrayList<TodayGroup>();
            Log.e("dream", e.getMessage());
        }
        return todayGroups;
    }

    public static void saveTodayGroups(List<TodayGroup> todayGroupList){
        for (TodayGroup todayGroup : todayGroupList){
            saveTodayGroup(todayGroup);
        }
    }
    public static void saveTodayGroup(TodayGroup todayGroup){
        if(todayGroup != null){
            getDatabaseHelper().getTodayGroupRuntimeDao().createOrUpdate(todayGroup);
        }
    }

    public static void updateTodayGroup(){
        deleteAllTodayGroups();
        QueryBuilder<Today, Integer> todayQueryBuilder = getDatabaseHelper().getTodayRuntimeDao().queryBuilder();
        todayQueryBuilder.groupBy("date");
        todayQueryBuilder.orderBy("date", false);
        List<TodayGroup> todayGroupList = new ArrayList<TodayGroup>();
        try {
            List<Today> query = todayQueryBuilder.query();
            for (Today today : query){
                TodayGroup tmp = new TodayGroup();
                tmp.setDate(today.getDate());
                todayGroupList.add(tmp);
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        saveTodayGroups(todayGroupList);

    }

    public static List<TodayGroup> listTodayGroup(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        List<TodayGroup> today = null;
        try {
            today = todayGroupIntegerQueryBuilder.query();
        } catch (SQLException e) {
            today = new ArrayList<TodayGroup>();
            Log.e(TAG, e.getMessage());
        }

        return today;
    }

    public static Date firstTodayDate(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", true);
        TodayGroup today = null;
        try {
            today = todayGroupIntegerQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        if(today == null) {
            return new Date();
        }else{
            return today.getDate();
        }
    }

    public static Date lastTodayDate(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        TodayGroup today = null;
        try {
            today = todayGroupIntegerQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
        if(today == null) {
            return null;
        }else{
            return today.getDate();
        }
    }

    public static boolean isExistsTodayData(Date date){
        List<TodayGroup> todays = getDatabaseHelper().getTodayGroupRuntimeDao().queryForEq("date", date);
        if(todays != null && todays.size() > 0){
            return true;
        }
        return false;
    }

    public static List<Date> getTodayDates(){
        QueryBuilder<TodayGroup, Date> todayGroupIntegerQueryBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().queryBuilder();
        todayGroupIntegerQueryBuilder.orderBy("date", false);
        List<TodayGroup> todayGroups = null;
        List<Date> dateArrayList = new ArrayList<Date>();
        try {
            List<TodayGroup> rangeList = todayGroupIntegerQueryBuilder.query();
            for (TodayGroup today : rangeList){
                dateArrayList.add(today.getDate());
            }

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }

        return dateArrayList;
    }

    public static void deleteAllTodayGroups(){
        DeleteBuilder<TodayGroup,Date> deleteBuilder = getDatabaseHelper().getTodayGroupRuntimeDao().deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            Log.e("dream", e.getMessage());
        }
    }
}
