package com.vivavu.dream.common;

/**
 * Created by yuja on 14. 1. 8.
 */
public class Constants {
    static public final String settings = "settings";
    static public final String email = "email";
    static public final String token = "token";
    static public final String tokenType = "tokenType";

    static public String url = "http://masunghoon.iptime.org";
    static public String port = "5000";
    static public String baseUrl = url + ":"+port+"/";
    static public String apiUsers = baseUrl+"api/users";
    static public String apiUserInfo = baseUrl+"api/user/{userId}";

    static public String apiResetPassword = baseUrl+"api/reset_password/{email}";
    static public String apiPostBucket = baseUrl+"api/buckets/";
    static public String apiBuckets = baseUrl+"api/buckets/user/{userId}";

    //버킷, 등록, 읽기, 수정, 삭제
    static public String apiBucketInfo = baseUrl+"api/bucket/{bkt_id}";
    
    static public String apiPlanList = baseUrl + "api/plans/{username}";
    static public String apiPlanInfo = baseUrl + "api/plan/{plan_id}";

    static public String apiToken = baseUrl+"api/token";
    static public String apiBaseInfo = baseUrl+"api/resource";
}
