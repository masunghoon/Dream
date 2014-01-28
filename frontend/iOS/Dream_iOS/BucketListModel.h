//
//  BucketListModel.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BucketListModel : NSObject {
    //thread queue
    NSOperationQueue *operationQueue;
}

@property (nonatomic, strong) NSOperationQueue *operationQueue;
+ (BucketListModel *)sharedInstance;

@end
