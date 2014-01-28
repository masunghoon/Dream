//
//  LifeScreenAdd.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 9..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LifeScreenAdd : UIViewController{
    NSMutableDictionary *params;
    NSMutableDictionary *bucket;

    IBOutlet UITextField    * __weak inputBucketTitle;

    IBOutlet UIBarButtonItem *     __weak saveBucket;
    IBOutlet UIButton       * __weak setDueBtn;
    IBOutlet UILabel        * __weak dueRange;

    IBOutlet UIView         * __weak dueBtnGroup;

    IBOutlet UIButton       * __weak dueBtn1;
    IBOutlet UIButton       * __weak dueBtn2;
    IBOutlet UIButton       * __weak dueBtn3;
    IBOutlet UIButton       * __weak dueBtn4;
    IBOutlet UIButton       * __weak dueBtn5;
    IBOutlet UIButton       * __weak dueBtn6;
    IBOutlet UIButton       * __weak dueBtnLifetime;
    IBOutlet UIButton       * __weak dueBtnCustom;

    IBOutlet UIDatePicker   * __weak datePicker;

}

@property (nonatomic, strong) NSMutableDictionary *params;
@property (nonatomic, strong) NSMutableDictionary *bucket;
@property (nonatomic, strong) NSString *level;

@property (nonatomic, weak) NSString *saveType;

@property (nonatomic, weak) IBOutlet UIBarButtonItem *saveBucket;


@property (nonatomic, weak) IBOutlet UITextField *inputBucketTitle;
@property (nonatomic, weak) IBOutlet UIBarButtonItem *saveBtn;
@property (nonatomic, weak) IBOutlet UIButton *setDueBtn;
@property (nonatomic, weak) IBOutlet UILabel *dueRange;

@property (nonatomic, weak) IBOutlet UIView *dueBtnGroup;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn1;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn2;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn3;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn4;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn5;
@property (nonatomic, weak) IBOutlet UIButton *dueBtn6;
@property (nonatomic, weak) IBOutlet UIButton *dueBtnLifetime;
@property (nonatomic, weak) IBOutlet UIButton *dueBtnCustom;

@property (nonatomic, weak) IBOutlet UIDatePicker *datePicker;

- (IBAction)setDueBtnTapped:(id)sender;

- (IBAction)dueBtn1Tapped:(id)sender;
- (IBAction)dueBtn2Tapped:(id)sender;
- (IBAction)dueBtn3Tapped:(id)sender;
- (IBAction)dueBtn4Tapped:(id)sender;
- (IBAction)dueBtn5Tapped:(id)sender;
- (IBAction)dueBtn6Tapped:(id)sender;
- (IBAction)dueBtnLifetimeTapped:(id)sender;
- (IBAction)dueBtnCustomTapped:(id)sender;

- (void)setNavTitle:(NSString *)title withSubTitle:(NSString *)subtitle;

@end
