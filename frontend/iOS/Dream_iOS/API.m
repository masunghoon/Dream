//
//  API.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"

#import "UICKeyChainStore.h"
#import "UIAlertView+error.h"

//the web location of the service
#define kAPIHost @"http://masunghoon.iptime.org:5000/"

@implementation API

@synthesize user;
@synthesize myBucketList;
@synthesize manager;

#pragma mark - Singleton Methods
/**
 * Singleton Methods
 */
+(API *)sharedInstance{
    static API *sharedInstance = nil;
    static dispatch_once_t oncePredicate;
    dispatch_once(&oncePredicate, ^{
        sharedInstance = [[self alloc] init];
//        sharedInstance = [[self alloc] initWithBaseURL:[NSURL URLWithString:kAPIHost]];
    });
    
    return sharedInstance;
}

#pragma mark - init
//initialize the API class with the destination host name
-(API *)init{
    //call super init
    self = [super init];

    manager = [[AFHTTPRequestOperationManager alloc] initWithBaseURL:[NSURL URLWithString:kAPIHost]];
    [manager setRequestSerializer:[AFHTTPRequestSerializer serializer]];
    [manager.requestSerializer setAuthorizationHeaderFieldWithUsername:[UICKeyChainStore stringForKey:@"token"] password:@"unsued"];

    return self;
}

//GET
-(void)getFromURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock {
    [manager GET:uri
      parameters:nil
         success:^(AFHTTPRequestOperation *operation, id responseObject){
             completionBlock(responseObject);
         }
         failure:^(AFHTTPRequestOperation *operation, NSError *error){
             completionBlock([NSDictionary dictionaryWithObject:[error localizedDescription] forKey:@"error"]);
         }
    ];
    
}

//POST
-(void)postToURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock {
    [manager POST:uri
       parameters:params
          success:^(AFHTTPRequestOperation *operation, id responseObject){
              completionBlock(responseObject);
          }
          failure:^(AFHTTPRequestOperation *operation, NSError *error){
              completionBlock([NSDictionary dictionaryWithObject:[error localizedDescription] forKey:@"error"]);
          }
     ];
}

//PUT
-(void)putToURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock {
    [manager PUT:uri
      parameters:params
            success:^(AFHTTPRequestOperation *operation, id responseObject){
                completionBlock(responseObject);
            }
            failure:^(AFHTTPRequestOperation *operation, NSError *error){
                completionBlock([NSDictionary dictionaryWithObject:[error localizedDescription] forKey:@"error"]);
            }
    ];
}

//DELETE
-(void)deleteFromURI:(NSString *)uri withParams:(NSMutableDictionary *)params onCompletion:(JSONResponseBlock)completionBlock {
    [manager DELETE:uri
      parameters:params
         success:^(AFHTTPRequestOperation *operation, id responseObject){
             completionBlock(responseObject);
         }
         failure:^(AFHTTPRequestOperation *operation, NSError *error){
             completionBlock([NSDictionary dictionaryWithObject:[error localizedDescription] forKey:@"error"]);
         }
    ];
}

//Get Auth Token
-(void)getAuthTokenWithID:(NSString *)email andPassword:(NSString *)password onCompletion:(JSONResponseBlock)completionBlock {
    [manager.requestSerializer setAuthorizationHeaderFieldWithUsername:email password:password];
    [manager GET:@"api/token"
      parameters:nil
         success:^(AFHTTPRequestOperation *operation, id responseObject){
             [UICKeyChainStore setString:[responseObject objectForKey:@"token"] forKey:@"token"];
             [manager.requestSerializer setAuthorizationHeaderFieldWithUsername:[UICKeyChainStore stringForKey:@"token"] password:@"unused"];
             completionBlock([responseObject objectForKey:@"user"]);
         }
         failure:^(AFHTTPRequestOperation *operation, NSError *error){
             completionBlock([NSDictionary dictionaryWithObject:@"Unauthorized" forKey:@"error"]);
         }
    ];
}

//Logout
-(void)logout{
    [[API sharedInstance] setUser:nil];
    [UICKeyChainStore removeItemForKey:@"token"];
    [UICKeyChainStore removeItemForKey:@"password"];
    [manager.requestSerializer setAuthorizationHeaderFieldWithUsername:[UICKeyChainStore stringForKey:@"email"] password:@"unsued"];
    [self getFromURI:@"api/resource"
          withParams:nil
        onCompletion:^(NSDictionary *json){
            [UIAlertView error:[NSString stringWithFormat:@"bye bye %@", [UICKeyChainStore stringForKey:@"email"]]];
    }];
}

@end