//
//  LifeScreenDetail.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "LifeScreenDetail.h"
#import "LifeScreenDescAdd.h"
#import "LifeScreenRptAdd.h"
#import "SWRevealViewController.h"
#import "UIAlertView+error.h"

@interface LifeScreenDetail ()

@property (nonatomic) NSInteger isPriv;

@end

@implementation LifeScreenDetail

@synthesize bucket;

@synthesize bucketTitle;
@synthesize bucketTitleView;
@synthesize bucketDue;

@synthesize bucketDesc;
@synthesize bucketDescView;
@synthesize bucketDescBtn;

@synthesize bucketPriv;
@synthesize bucketPrivBtn;
@synthesize bucketShareBtn;
@synthesize bucketPhotoBtn;
@synthesize bucketDeleteBtn;
@synthesize bucketPlanBtn;




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

//    bucket = [NSMutableDictionary dictionaryWithCapacity:1];
    NSLog(@"%@",bucket);

    NSLog(@"%@", [bucket objectForKey:@"title"]);
    NSLog(@"%@", [bucket objectForKey:@"deadline"]);

    [self.bucketTitle setText:[bucket objectForKey:@"title"]];
    [self.bucketDue setText:[bucket objectForKey:@"deadline"]];
    if([bucket objectForKey:@"description"] == [NSNull null]){
        bucketDescView.hidden = 1;
    } else {
        bucketDescView.hidden = 0;
        bucketDescBtn.enabled = NO;

        NSString *text = [bucket objectForKey:@"description"];
        CGSize withinSize = CGSizeMake(225, CGFLOAT_MAX);
        CGRect textRect = [text boundingRectWithSize:withinSize
                                             options:NSStringDrawingUsesLineFragmentOrigin
                                          attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
                                             context:nil];
        CGSize size = textRect.size;

        bucketDesc.frame = CGRectMake(20, 40, 240, size.height);
        bucketDescView.frame = CGRectMake(20, 160, 280, size.height + 60);

        bucketDescBtn.frame = CGRectOffset(bucketDescBtn.frame, 0, size.height + 60);
        bucketPrivBtn.frame = CGRectOffset(bucketPrivBtn.frame, 0, size.height + 60);
        bucketShareBtn.frame = CGRectOffset(bucketShareBtn.frame, 0, size.height + 60);
        bucketPhotoBtn.frame = CGRectOffset(bucketPhotoBtn.frame, 0, size.height + 60);
        bucketDeleteBtn.frame = CGRectOffset(bucketDeleteBtn.frame, 0, size.height + 60);
        bucketPlanBtn.frame = CGRectOffset(bucketPlanBtn.frame, 0, size.height + 60);

        [self.bucketDesc setText:[bucket objectForKey:@"description"]];

        UITapGestureRecognizer *tgr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(bucketTitleTapped)];
        tgr.numberOfTapsRequired = 1;
        [bucketTitleView addGestureRecognizer:tgr];
        bucketTitleView.userInteractionEnabled = YES;

        UITapGestureRecognizer *tgr2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(bucketDescTapped)];
        tgr2.numberOfTapsRequired = 1;
        [bucketDescView addGestureRecognizer:tgr2];
        bucketDescView.userInteractionEnabled = YES;


    }
    if ([[bucket objectForKey:@"is_private"] intValue]){
        [bucketPriv setImage:[UIImage imageNamed:@"glyphicons_203_lock.png"] forState:UIControlStateNormal];
    } else {
        [bucketPriv setImage:[UIImage imageNamed:@"glyphicons_204_unlock.png"] forState:UIControlStateNormal];
    }
    _isPriv = [[bucket objectForKey:@"is_private"] intValue];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)bucketTitleTapped {
//    [self performSegueWithIdentifier:@"goToEditDesc" sender:nil];
    NSLog(@"!@#$!@#$");
}

- (void)bucketDescTapped {
    [self performSegueWithIdentifier:@"goToEditDesc" sender:nil];
}

- (IBAction)bucketDescBtnTapped:(id)sender {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                    message:@"ShareButtonTapped"
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (IBAction)bucketPrivBtnTapped:(id)sender
{
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithObjectsAndKeys:
        [NSString stringWithFormat:@"%d", ![[bucket objectForKey:@"is_private"] intValue]], @"is_private", nil];
    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@",[bucket objectForKey:@"id"]]
                             withParams:params
                           onCompletion:^(id json){
                               if(![json objectForKey:@"error"]){
                                   [UIAlertView error:@"Succeeded"];
                                   if (_isPriv){
                                       [bucketPriv setImage:[UIImage imageNamed:@"glyphicons_204_unlock.png"] forState:UIControlStateNormal];
                                   } else {
                                       [bucketPriv setImage:[UIImage imageNamed:@"glyphicons_203_lock.png"] forState:UIControlStateNormal];
                                   }
                                   _isPriv = !_isPriv;

                               } else {
                                   [UIAlertView error:@"Failed"];

                               }
                           }];
}


- (IBAction)bucketDeleteBtnTapped:(id)sender
{
    [[API sharedInstance] deleteFromURI:[NSString stringWithFormat:@"api/bucket/%@",[bucket objectForKey:@"id"]]
                             withParams:nil
                           onCompletion:^(id json){
                               if(![json objectForKey:@"error"]){
                                   [UIAlertView error:@"Delete Succeeded"];
                                   SWRevealViewController *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjMain"];
                                   [self presentViewController:viewController animated:YES completion:nil];
                               } else {
                                   [UIAlertView error:@"Deletion Failed"];

                               }
                           }];

}

- (IBAction)bucketShareBtnTapped:(id)sender {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                    message:@"ShareButtonTapped"
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (IBAction)bucketPhotoBtnTapped:(id)sender {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Alert"
                                                    message:@"You Tapped Photo Button."
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (IBAction)backButtonPressed:(id)sender{
    UIStoryboard *storyboard = self.storyboard;
    SWRevealViewController *swrvc = [storyboard instantiateViewControllerWithIdentifier:@"DreamProjMain"];
    swrvc.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;
    [self presentViewController:swrvc animated:YES completion:nil];
}


- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if([segue.identifier isEqualToString:@"goToEditDesc"]){
        LifeScreenDescAdd *lifeScreenDescAdd = segue.destinationViewController;
        [lifeScreenDescAdd setBucket:bucket];
    }

    if([segue.identifier isEqualToString:@"goToEditRepeat"]){
        LifeScreenRptAdd *lifeScreenRptAdd = segue.destinationViewController;
        [lifeScreenRptAdd setBucket:bucket];
    }
}

@end
