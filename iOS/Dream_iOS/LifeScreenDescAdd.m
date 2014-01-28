//
//  LifeScreenDescAdd.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 20..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "LifeScreenDetail.h"
#import "LifeScreenDescAdd.h"

#import "UIAlertView+error.h"

@interface LifeScreenDescAdd ()

@end

@implementation LifeScreenDescAdd

@synthesize bucket;
@synthesize bucketDescText;

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


    self.navigationItem.title = [bucket objectForKey:@"title"];
	// Do any additional setup after loading the view.
    if ([bucket objectForKey:@"description"] != [NSNull null]){
        [self.bucketDescText setText:[bucket objectForKey:@"description"]];
    }
    [self.bucketDescText becomeFirstResponder];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)didSaveButtonTapped:(id)sender{
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithObjectsAndKeys:bucketDescText.text, @"description", nil];
    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@",[bucket objectForKey:@"id"]]
                        withParams:params
                      onCompletion:^(id json){
                          if(![json objectForKey:@"error"]){
//                              [self setBucketDescText:bucketDescText];
//                              [bucket setValue:bucketDescText.text forKey:@"description"];
                              [UIAlertView error:@"Succeeded"];
//                                   LifeScreenDetail *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjBucket"];
//                                   [self presentViewController:viewController animated:YES completion:nil];
                          } else {
                              [UIAlertView error:@"Failed"];

                          }
                      }
    ];
}
@end
