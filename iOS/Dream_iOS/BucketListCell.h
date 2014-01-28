//
//  BucketListCell.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HHPanningTableViewCell.h"

@interface BucketListCell : HHPanningTableViewCell {
    NSMutableDictionary *bucket;
}
//    NSInteger index;
//
//    IBOutlet UILabel *  __weak titleLabel;
//    IBOutlet UILabel *  __weak descLabel;
//    IBOutlet UILabel *  __weak rangeLabel;
//    IBOutlet UILabel *  __weak remainDaysLabel;


@property (nonatomic, assign) NSInteger index;

@property (nonatomic, weak) IBOutlet UIButton *bucketIsDone;
@property (nonatomic, weak) IBOutlet UILabel *titleLabel;
@property (nonatomic, weak) IBOutlet UILabel *rangeLabel;
@property (nonatomic, weak) IBOutlet UILabel *remainDaysLabel;

@end
