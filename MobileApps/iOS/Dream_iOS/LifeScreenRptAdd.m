//
//  LifeScreenRptAdd.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 21..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "LifeScreenRptAdd.h"

#import "UIAlertView+error.h"

@interface LifeScreenRptAdd () <MultiSelectSegmentedControlDelegate>

@end

@implementation LifeScreenRptAdd

@synthesize params;
@synthesize breadTypes;
@synthesize fillingTypes;
@synthesize bucket;
@synthesize repeatCode;
@synthesize doublePicker;
@synthesize rptType, rptCndt;
@synthesize daysBtnGrp, howManyTimesGrp;
@synthesize sunBtn, monBtn, tueBtn, wedBtn, thuBtn, friBtn , satBtn;
@synthesize chgRptMthdBtn;
@synthesize customRptCycleLabel;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view.

    howManyTimesGrp.frame = CGRectMake(14, 112, 293, 49);
    NSArray *breadArray = [[NSArray alloc] initWithObjects:
            @"in every Week",@"in every Month",@"in every 3 Months",@"in Every 6 Months",@"in every Year", nil];
    self.breadTypes = breadArray;

    NSArray *fillingArray = [[NSArray alloc] initWithObjects:
            @"1 day",@"2 days",@"3 days",@"4 days",@"5 days",@"6 days",@"7 days",nil];
    self.fillingTypes = fillingArray;

    rptType = [bucket objectForKey:@"rptType"];
    rptCndt = [bucket objectForKey:@"rptCndt"];

    if (rptType == [NSNull null]) {
        daysBtnGrp.hidden = NO;
        doublePicker.hidden = YES;
        howManyTimesGrp.hidden = YES;
        rptType = @"WKRP";
        rptCndt = @"0000000";
    }  else if ([rptType isEqualToString: @"WKRP"] && rptCndt != [NSNull null]){
        daysBtnGrp.hidden = NO;
        doublePicker.hidden = YES;
        howManyTimesGrp.hidden = YES;
        if([[rptCndt substringWithRange:NSMakeRange(0, 1)] isEqualToString:@"1"]){
            _sunBtnTappedYN = YES;
            [sunBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(1, 1)] isEqualToString:@"1"]){
            _monBtnTappedYN = YES;
            [monBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(2, 1)] isEqualToString:@"1"]){
            _tueBtnTappedYN = YES;
            [tueBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(3, 1)] isEqualToString:@"1"]){
            _wedBtnTappedYN = YES;
            [wedBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(4, 1)] isEqualToString:@"1"]){
            _thuBtnTappedYN = YES;
            [thuBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(5, 1)] isEqualToString:@"1"]){
            _friBtnTappedYN = YES;
            [friBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
        if([[rptCndt substringWithRange:NSMakeRange(6, 1)] isEqualToString:@"1"]){
            _satBtnTappedYN = YES;
            [satBtn setBackgroundColor:[UIColor darkGrayColor]];
        }
    } else {
        daysBtnGrp.hidden = YES;
        doublePicker.hidden = NO;
        howManyTimesGrp.hidden = NO;

    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setDaysControl:(MultiSelectSegmentedControl *)daysControl {
    _daysControl = daysControl;
    self.daysControl.tag = 1;
    self.daysControl.delegate = self;
}

- (IBAction)selectAll:(id)sender {
    [self.daysControl selectAllSegments:YES];
}

- (IBAction)selectNone {
    [self.daysControl selectAllSegments:NO];
}

-(void)multiSelect:(MultiSelectSegmentedControl *)multiSelecSegmendedControl didChangeValue:(BOOL)value atIndex:(NSUInteger)index{

    if (value) {
        NSLog(@"multiSelect with tag %li selected button at index: %lu", (long)multiSelecSegmendedControl.tag, (unsigned long)index);
    } else {
        NSLog(@"multiSelect with tag %li deselected button at index: %lu", (long)multiSelecSegmendedControl.tag, (unsigned long)index);
    }
}

- (IBAction)saveBtnTapped:(id)sender {
    params = [NSMutableDictionary dictionaryWithObjectsAndKeys:rptType, @"rptType",
                                                               rptCndt, @"rptCndt", nil];
    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@",[bucket objectForKey:@"id"]]
                        withParams:params
                      onCompletion:^(id json){
                          if(![json objectForKey:@"error"]){
                              [UIAlertView error:@"Succeeded"];
//                                   LifeScreenDetail *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjBucket"];
//                                   [self presentViewController:viewController animated:YES completion:nil];
                          } else {
                              [UIAlertView error:@"Failed"];

                          }
                      }
    ];
}


- (IBAction)sunBtnTapped:(id)sender {
    _sunBtnTappedYN = !_sunBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:[NSString stringWithFormat:@"%d", _sunBtnTappedYN]];
    if(_sunBtnTappedYN){
        [sunBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [sunBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)monBtnTapped:(id)sender {
    _monBtnTappedYN = !_monBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(1, 1) withString:[NSString stringWithFormat:@"%d", _monBtnTappedYN]];
    if(_monBtnTappedYN){
        [monBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [monBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)tueBtnTapped:(id)sender {
    _tueBtnTappedYN = !_tueBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(2, 1) withString:[NSString stringWithFormat:@"%d", _tueBtnTappedYN]];
    if(_tueBtnTappedYN){
        [tueBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [tueBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)wedBtnTapped:(id)sender {
    _wedBtnTappedYN = !_wedBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(3, 1) withString:[NSString stringWithFormat:@"%d", _wedBtnTappedYN]];
    if(_wedBtnTappedYN){
        [wedBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [wedBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)thuBtnTapped:(id)sender {
    _thuBtnTappedYN = !_thuBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(4, 1) withString:[NSString stringWithFormat:@"%d", _thuBtnTappedYN]];
    if(_thuBtnTappedYN){
        [thuBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [thuBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)friBtnTapped:(id)sender {
    _friBtnTappedYN = !_friBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(5, 1) withString:[NSString stringWithFormat:@"%d", _friBtnTappedYN]];
    if(_friBtnTappedYN){
        [friBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [friBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)satBtnTapped:(id)sender {
    _satBtnTappedYN = !_satBtnTappedYN;
    rptCndt = [rptCndt stringByReplacingCharactersInRange:NSMakeRange(6, 1) withString:[NSString stringWithFormat:@"%d", _satBtnTappedYN]];
    if(_satBtnTappedYN){
        [satBtn setBackgroundColor:[UIColor darkGrayColor]];
    } else {
        [satBtn setBackgroundColor:[UIColor whiteColor]];
    }
}

- (IBAction)setCustomRptBtnTapped{
    daysBtnGrp.hidden = !daysBtnGrp.hidden;
    doublePicker.hidden = !doublePicker.hidden;
    howManyTimesGrp.hidden = !howManyTimesGrp.hidden;
    if(daysBtnGrp.hidden){
        [chgRptMthdBtn setTitle:@"Set Repeat Weekly" forState:UIControlStateNormal];
    } else {
        [chgRptMthdBtn setTitle:@"Set Custom Repeat" forState:UIControlStateNormal];
    }
}

-(IBAction)pickerButtonPressed {
    NSInteger breadRow = [doublePicker selectedRowInComponent: kBreadComponent];
    NSInteger fillingRow = [doublePicker selectedRowInComponent: kFillingComponent];
    NSString *bread = [breadTypes objectAtIndex:breadRow];
    NSString *filling = [fillingTypes objectAtIndex:fillingRow];

    NSString *message = [[NSString alloc] initWithFormat:
            @"Your %@ on %@ bread will be right up.",filling, bread];

    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Thank you for order"
                                                    message:message delegate:nil
                                          cancelButtonTitle:@"Great!!!"
                                          otherButtonTitles:nil];
    [alert show];
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 2;
}

-(NSInteger)pickerView:(UIPickerView *)pickerView
        numberOfRowsInComponent:(NSInteger)component
{
    if (component == kBreadComponent)
        return[self.breadTypes count];
    return[self.fillingTypes count];
}

-(NSString *)pickerView:(UIPickerView *)pickerView
            titleForRow:(NSInteger)row
           forComponent:(NSInteger)component
{
    if (component == kBreadComponent)
        return [self. breadTypes objectAtIndex:row];
    return [self.fillingTypes objectAtIndex:row];
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    if (component == kBreadComponent){
        NSLog(@"RightThingChanged");
    } else {
        NSLog(@"LeftThingChanged");
    }
}

- (void)setNavTitle:(NSString *)title withSubTitle:(NSString *)subtitle{
    CGRect headerTitleSubtitleFrame = CGRectMake(0, 0, 200, 35);
    UIView* _headerTitleSubtitleView = [[UILabel alloc] initWithFrame:headerTitleSubtitleFrame];
    _headerTitleSubtitleView.backgroundColor = [UIColor clearColor];
    _headerTitleSubtitleView.autoresizesSubviews = NO;

    CGRect titleFrame = CGRectMake(0, 2, 200, 24);
    UILabel *titleView = [[UILabel alloc] initWithFrame:titleFrame];
    titleView.backgroundColor = [UIColor clearColor];
    titleView.font = [UIFont boldSystemFontOfSize:16];
    titleView.textAlignment = NSTextAlignmentCenter;
    titleView.text = title;
    titleView.adjustsFontSizeToFitWidth = YES;
    [_headerTitleSubtitleView addSubview:titleView];

    CGRect subtitleFrame = CGRectMake(0, 24, 200, 35-24);
    UILabel *subtitleView = [[UILabel alloc] initWithFrame:subtitleFrame];
    subtitleView.backgroundColor = [UIColor clearColor];
    subtitleView.font = [UIFont boldSystemFontOfSize:12];
    subtitleView.textAlignment = NSTextAlignmentCenter;
    subtitleView.text = subtitle;
    subtitleView.adjustsFontSizeToFitWidth = YES;
    [_headerTitleSubtitleView addSubview:subtitleView];

    self.navigationItem.titleView = _headerTitleSubtitleView;
}

@end
