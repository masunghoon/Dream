//
//  PlanningCell.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 22..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PlanningCell : UITableViewCell{
    NSMutableDictionary *plan;
}

@property (nonatomic, assign) NSInteger index;

@property (nonatomic, weak) IBOutlet UILabel *planTitleLabel;
@property (nonatomic, weak) IBOutlet UIButton *planIsDoneBtn;

@end
