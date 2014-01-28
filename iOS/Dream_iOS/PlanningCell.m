//
//  PlanningCell.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 22..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "PlanningCell.h"

@implementation PlanningCell

@synthesize index;

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
