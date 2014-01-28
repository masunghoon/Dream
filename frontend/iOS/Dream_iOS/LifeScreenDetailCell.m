//
//  LifeScreenDetailCell.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 24..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "LifeScreenDetailCell.h"

@implementation LifeScreenDetailCell

@synthesize index;
@synthesize bucket;

@synthesize bucketTitle;
@synthesize bucketTitleView;
@synthesize bucketDue;
@synthesize bucketLevel;
@synthesize bucketExpandBtn;

@synthesize bucketDesc;
@synthesize bucketDescView;
@synthesize bucketDescBtn;

@synthesize bucketPriv;
@synthesize bucketPrivBtn;
@synthesize bucketShareBtn;
@synthesize bucketPhotoBtn;
@synthesize bucketDeleteBtn;
@synthesize bucketRepeatBtn;


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
