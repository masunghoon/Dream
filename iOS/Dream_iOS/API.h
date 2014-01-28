//
//  API.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "AFNetworking.h"
#import "AFHTTPRequestOperationManager.h"

//API call completion block with result as json

typedef void (^JSONResponseBlock)(NSDictionary *json);
@interface API : AFHTTPRequestOperationManager{
    AFHTTPRequestOperationManager *manager;
}

//the authorized user
@property (strong, nonatomic) NSDictionary *user;
@property (nonatomic, strong) NSMutableArray *myBucketList;
@property (nonatomic, strong) AFHTTPRequestOperationManager *manager;

//-(API *)init;
+(API *)sharedInstance;

//send an API command to the server
-(void)getFromURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock;
-(void)postToURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock;
-(void)putToURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock;
-(void)deleteFromURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock;

//Authorization
-(void)getAuthTokenWithID:(NSString *)email andPassword:(NSString *)password  onCompletion:(JSONResponseBlock)completionBlock;
-(void)logout;

@end
