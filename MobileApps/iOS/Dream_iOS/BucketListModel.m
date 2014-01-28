//
//  BucketListModel.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "BucketListModel.h"

@implementation BucketListModel

@synthesize operationQueue;

- (id)init {
    if(self = [super init]){
        self.operationQueue = [[NSOperationQueue alloc] init];
        [self.operationQueue setMaxConcurrentOperationCount:10];
    }

    return self;
}

+ (BucketListModel *)sharedInstance {
    static BucketListModel *instance = nil;

    @synchronized (self) {
        if(instance == nil){
            instance = [[BucketListModel alloc] init];
        }
    }
    return instance;
}

@end
