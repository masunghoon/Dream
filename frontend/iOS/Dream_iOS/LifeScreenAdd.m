//
//  LifeScreenAdd.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 9..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "LifeScreenAdd.h"
#import "LifeScreenDetail.h"
#import "API.h"
#import "UIAlertView+error.h"

@interface LifeScreenAdd ()

@end

@implementation LifeScreenAdd

@synthesize bucket;
@synthesize params;
@synthesize saveType;
@synthesize saveBucket;
@synthesize inputBucketTitle;
@synthesize saveBtn, setDueBtn;
@synthesize dueRange;
@synthesize dueBtnGroup;
@synthesize dueBtn1, dueBtn2, dueBtn3, dueBtn4, dueBtn5, dueBtn6;
@synthesize dueBtnLifetime, dueBtnCustom;

@synthesize datePicker;

//@property (nonatomic, weak) IBOutlet UIDatePicker *datePicker;

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
//    params = [[NSMutableDictionary alloc] init];

    NSLog(@"%@",params);
    NSLog(@"%@",bucket);

    if([saveType isEqualToString:@"Modify"]){
        [inputBucketTitle setText:[bucket objectForKey:@"title"]];
        if([[bucket objectForKey:@"scope"] isEqualToString:@"DECADE"]){
            if(![[bucket objectForKey:@"range"] isEqualToString:@"00"]){
                [dueRange setText:[NSString stringWithFormat:@"in my %@'s", [bucket objectForKey:@"range"]]];
            } else {
                [dueRange setText:@"in my Lifetime"];
            }
        } else if ([[bucket objectForKey:@"scope"] isEqualToString:@"YEARLY"]){
            [dueRange setText:[NSString stringWithFormat:@"in %@'s", [bucket objectForKey:@"range"]]];
        } else if ([[bucket objectForKey:@"scope"] isEqualToString:@"MONTHLY"]){
            [dueRange setText:[NSString stringWithFormat:@"in %@", [bucket objectForKey:@"range"]]];
        } else {
            [dueRange setText:[NSString stringWithFormat:@"until %@", [bucket objectForKey:@"deadline"]]];
        }
    }
    [inputBucketTitle becomeFirstResponder];
    [self setDueBtns:[[[API sharedInstance] user] objectForKey:@"birthday"]];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setParamsForKey:(NSString *)key withValue:(NSString *)value{

    [params setValue:value forKey:key];
}

- (void)setDueBtns:(NSString *)userBday {
    NSInteger bYear = [[userBday substringWithRange:NSMakeRange(0, 4)] intValue];
//    NSInteger bMonth = [[userBday substringWithRange:NSMakeRange(4, 2)] intValue];
//    NSInteger bDay = [[userBday substringWithRange:NSMakeRange(6, 2)] intValue];

    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy"];
    NSInteger thisYear = [[formatter stringFromDate:[NSDate date]] intValue];

    for (NSInteger i = 1; i < 10; i++){
        if(thisYear > bYear+(i*10) && thisYear <= bYear+((i+1)*10)){
            [dueBtn1 setTitle:[NSString stringWithFormat:@"%d's", i*10] forState:UIControlStateNormal];
            [dueBtn2 setTitle:[NSString stringWithFormat:@"%d's", (i+1)*10] forState:UIControlStateNormal];
            [dueBtn3 setTitle:[NSString stringWithFormat:@"%d's", (i+2)*10] forState:UIControlStateNormal];
            [dueBtn4 setTitle:[NSString stringWithFormat:@"%d's", (i+3)*10] forState:UIControlStateNormal];
            [dueBtn5 setTitle:[NSString stringWithFormat:@"%d's", (i+4)*10] forState:UIControlStateNormal];
            [dueBtn6 setTitle:[NSString stringWithFormat:@"%d's", (i+5)*10] forState:UIControlStateNormal];
            [dueBtn1 tag];
        }
    }

}

- (IBAction)saveBtnTapped:(id)sender {
    NSLog(@"%@",saveType);

    if(!datePicker.hidden){
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"yyyy-MM-dd"];

        NSInteger dDate = [[[formatter stringFromDate:[self.datePicker date]] substringToIndex:4] intValue];
        NSInteger bDate = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue];
        [self setParamsForKey:@"range" withValue:[NSString stringWithFormat:@"%d",(dDate - bDate) - (dDate - bDate)%10 - 10]];
        [self setParamsForKey:@"deadline" withValue:[formatter stringFromDate:[self.datePicker date]]];

    }
    [self setParamsForKey:@"title" withValue:inputBucketTitle.text];
    [self setParamsForKey:@"scope" withValue:@"DECADE"];
    if ([saveType isEqualToString:@"Modify"]) {
        [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@", [bucket objectForKey:@"id"]]
                            withParams:params
                          onCompletion:^(NSDictionary *json){
                              if(![json objectForKey:@"error"]){
                                  bucket = [[json objectForKey:@"bucket"] mutableCopy];
                                  [self performSegueWithIdentifier:@"addToDetail" sender:nil];
                              } else {
                                  [UIAlertView error:@"Error"];
                              }
                          }
        ];
    } else {
        [[API sharedInstance] postToURI:[NSString stringWithFormat:@"api/buckets/%@", [[[API sharedInstance] user] objectForKey:@"username"]]
                             withParams:params
                           onCompletion:^(NSDictionary *json){
                               if(![json objectForKey:@"error"]){
                                   [UIAlertView error:@"Success"];
                                   bucket = [[json objectForKey:@"bucket"] mutableCopy];
                                   [self performSegueWithIdentifier:@"addToDetail" sender:nil];
                               } else {
                                   [UIAlertView error:@"Error"];
                               }
                           }
        ];
    }

}

- (IBAction)setDueBtnTapped:(id)sender {
    dueBtnGroup.hidden = !dueBtnGroup.hidden;
    datePicker.hidden = !dueBtnGroup.hidden;
    [self.view endEditing:YES];
}

- (IBAction)dueBtn1Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn1.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor grayColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn1.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn1.currentTitle substringToIndex:2]];
    NSLog(@"%@",params);
}
- (IBAction)dueBtn2Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn2.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor grayColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn2.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn2.currentTitle substringToIndex:2]];
}
- (IBAction)dueBtn3Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn3.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor grayColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn3.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn3.currentTitle substringToIndex:2]];
}
- (IBAction)dueBtn4Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn4.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor grayColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn4.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn4.currentTitle substringToIndex:2]];
}
- (IBAction)dueBtn5Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn5.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor grayColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn5.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn5.currentTitle substringToIndex:2]];
}
- (IBAction)dueBtn6Tapped:(id)sender {
    [dueRange setText:[NSString stringWithFormat:@"in my %@", dueBtn6.currentTitle]];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor grayColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    NSInteger dYear = [[dueBtn6.currentTitle substringToIndex:2] intValue]; //due Year
    NSInteger bYear = [[[[[API sharedInstance] user] objectForKey:@"birthday"] substringToIndex:4] intValue]; //birth Year
    [self setParamsForKey:@"deadline" withValue:[NSString stringWithFormat:@"%d-12-31",bYear + dYear + 9]];
    [self setParamsForKey:@"range" withValue:[dueBtn6.currentTitle substringToIndex:2]];
}
- (IBAction)dueBtnLifetimeTapped:(id)sender {
    [dueRange setText:@"in my Lifetime"];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor grayColor]];
    [dueBtnCustom setBackgroundColor:[UIColor whiteColor]];
    [self.view endEditing:YES];
    [self setParamsForKey:@"deadline" withValue:@"2999-12-31"];
    [self setParamsForKey:@"range" withValue:@"00"];
}
- (IBAction)dueBtnCustomTapped:(id)sender {
    [dueRange setText:@"Custom"];
    [dueBtn1 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn2 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn3 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn4 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn5 setBackgroundColor:[UIColor whiteColor]];
    [dueBtn6 setBackgroundColor:[UIColor whiteColor]];
    [dueBtnLifetime setBackgroundColor:[UIColor whiteColor]];
    [dueBtnCustom setBackgroundColor:[UIColor grayColor]];
    [self.view endEditing:YES];
    dueBtnGroup.hidden = 1;
    datePicker.hidden = 0;
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

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    UITouch *touch = [[event allTouches] anyObject];
    if ([inputBucketTitle isFirstResponder] && [touch view] != inputBucketTitle) {
        [inputBucketTitle resignFirstResponder];
    }
    [super touchesBegan:touches withEvent:event];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if([segue.identifier isEqualToString:@"addToDetail"]){
        LifeScreenDetail *lifeScreenDetail = segue.destinationViewController;
//        BucketListCell *bucketListCell = (BucketListCell *)sender;

        [lifeScreenDetail setBucket:bucket];
    }
}


@end
