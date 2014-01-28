//
//  PlanningScreenMain.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 21..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "HHPanningTableViewCell.h"

@interface PlanningScreenMain : UITableViewController <HHPanningTableViewCellDelegate> {
    NSMutableDictionary *dailyPlans;
    NSMutableArray *planList;
}

@property (nonatomic, strong) NSMutableArray *planList;
@property (nonatomic, strong) NSMutableDictionary *dailyPlans;


@end
