//
//  LifeScreenMain.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HHPanningTableViewCell.h"

@interface LifeScreenMain : UITableViewController <HHPanningTableViewCellDelegate> {
    NSMutableArray *bucketListMain;
    UIPinchGestureRecognizer *handlePinch;
}

@property (nonatomic, strong) NSMutableArray *bucketListMain;

-(void) setNavTitle:(NSString *)navTitle;

-(NSInteger) getRemainDays:(NSString *)dueDate;

@end
