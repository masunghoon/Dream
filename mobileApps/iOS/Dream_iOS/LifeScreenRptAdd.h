//
//  LifeScreenRptAdd.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 21..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MultiSelectSegmentedControl.h"

#define kFillingComponent 0
#define kBreadComponent 1

@interface LifeScreenRptAdd : UIViewController <UIPickerViewDelegate, UIPickerViewDataSource> {
    IBOutlet UIPickerView *doublePicker;
    NSArray *fillingTypes;
    NSArray *breadTypes;

    NSDictionary *bucket;
    NSMutableDictionary *params;

    NSString *rptType;
    NSString *rptCndt;
}

@property (nonatomic, retain) UIPickerView *doublePicker;
@property (nonatomic, retain) NSArray *fillingTypes;
@property (nonatomic, retain) NSArray *breadTypes;

@property (nonatomic, strong) NSDictionary *bucket;
@property (nonatomic, strong) NSMutableDictionary *params;

@property (nonatomic, strong) IBOutlet UIView *daysBtnGrp;
@property (nonatomic, strong) IBOutlet UIView *howManyTimesGrp;

@property (nonatomic, weak) IBOutlet UIButton *sunBtn;
@property (nonatomic, weak) IBOutlet UIButton *monBtn;
@property (nonatomic, weak) IBOutlet UIButton *tueBtn;
@property (nonatomic, weak) IBOutlet UIButton *wedBtn;
@property (nonatomic, weak) IBOutlet UIButton *thuBtn;
@property (nonatomic, weak) IBOutlet UIButton *friBtn;
@property (nonatomic, weak) IBOutlet UIButton *satBtn;

@property (nonatomic, weak) IBOutlet UIButton *chgRptMthdBtn;

@property (nonatomic, weak) IBOutlet UILabel *customRptCycleLabel;

@property (nonatomic, weak) NSString *repeatCode;
@property (nonatomic, strong) IBOutlet MultiSelectSegmentedControl *daysControl;

@property (nonatomic, strong) NSString *rptType;
@property (nonatomic, strong) NSString *rptCndt;

@property (nonatomic) BOOL sunBtnTappedYN;
@property (nonatomic) BOOL monBtnTappedYN;
@property (nonatomic) BOOL tueBtnTappedYN;
@property (nonatomic) BOOL wedBtnTappedYN;
@property (nonatomic) BOOL thuBtnTappedYN;
@property (nonatomic) BOOL friBtnTappedYN;
@property (nonatomic) BOOL satBtnTappedYN;

- (IBAction)pickerButtonPressed;
- (void)setNavTitle:(NSString *)title withSubTitle:(NSString *)subtitle;

@end
