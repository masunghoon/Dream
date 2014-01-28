//
//  BucketListCell.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "BucketListCell.h"
#import "HHPanningTableViewCell.h"
#import "UIAlertView+error.h"


@implementation BucketListCell

@synthesize index;
//@synthesize titleLabel;
//@synthesize descLabel;
//@synthesize rangeLabel;
//@synthesize remainDaysLabel;

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
