//
//  LifeScreenDetail.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LifeScreenDetail : UIViewController {
    NSMutableDictionary *bucket;

    IBOutlet UILabel *__weak bucketTitle;
    IBOutlet UILabel *__weak bucketDue;
    IBOutlet UILabel *__weak bucketDesc;
}

@property (nonatomic, strong) NSMutableDictionary *bucket;
@property (nonatomic, weak) IBOutlet UIView *bucketTitleView;
@property (nonatomic, weak) IBOutlet UILabel *bucketTitle;
@property (nonatomic, weak) IBOutlet UILabel *bucketDue;

@property (nonatomic, weak) IBOutlet UIView *bucketDescView;
@property (nonatomic, weak) IBOutlet UILabel *bucketDesc;
@property (nonatomic, weak) IBOutlet UIButton *bucketDescBtn;

@property (nonatomic, weak) IBOutlet UIButton *bucketPriv;
@property (nonatomic, weak) IBOutlet UIButton *bucketPrivBtn;
@property (nonatomic, weak) IBOutlet UIButton *bucketShareBtn;
@property (nonatomic, weak) IBOutlet UIButton *bucketPhotoBtn;
@property (nonatomic, weak) IBOutlet UIButton *bucketDeleteBtn;
@property (nonatomic, weak) IBOutlet UIButton *bucketPlanBtn;

- (IBAction)backButtonPressed:(id)sender;

@end
