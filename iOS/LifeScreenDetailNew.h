//
//  LifeScreenDetailNew.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 24..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LifeScreenDetailNew : UITableViewController{
    NSMutableDictionary *bucket;
    NSMutableArray *bucketDetailList;
    NSMutableArray *bucketDetailListMeta;
}

@property (nonatomic, strong) NSMutableDictionary *bucket;
@property (nonatomic, strong) NSMutableArray *bucketDetailList;
@property (nonatomic, strong) NSMutableArray *bucketDetailListMeta;

@property (nonatomic, weak) IBOutlet UIBarButtonItem *addSubBucketBtn;

@end
