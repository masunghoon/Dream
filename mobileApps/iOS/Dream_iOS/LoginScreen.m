//
//  LoginScreen.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "LoginScreen.h"
#import "SWRevealViewController.h"
#import "InitScreen.h"

#import "LifeScreenMain.h"
#import "UIAlertView+error.h"
#import "UICKeyChainStore.h"

@interface LoginScreen ()

@property (nonatomic) IBOutlet UIButton *revealButtonItem;

@end

@implementation LoginScreen

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
}

- (IBAction)btnLoginTapped:(UIButton *)sender{
        //make the call to the web API
        [UICKeyChainStore setString:fldEmail.text forKey:@"email"];
        [UICKeyChainStore setString:fldPasswd.text forKey:@"password"];

        [[API sharedInstance] getAuthTokenWithID:fldEmail.text andPassword:fldPasswd.text onCompletion:^(NSDictionary *json){

            if(![[json objectForKey:@"status"] isEqualToString:@"error"] && [[json objectForKey:@"id"] intValue] > 0){
                //success
                [[API sharedInstance]setUser: json];

//                [self.presentingViewController dismissViewControllerAnimated:YES completion:nil];
//                [self performSegueWithIdentifier:@"loginToMain" sender:nil];
                SWRevealViewController *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjMain"];
                [self presentViewController:viewController animated:YES completion:nil];

                //show messages to the user
                [[[UIAlertView alloc] initWithTitle:@"Logged in"
                                            message:[NSString stringWithFormat:@"Welcome %@", [json objectForKey:@"username"]]
                                           delegate:nil
                                  cancelButtonTitle:@"close"
                                  otherButtonTitles:nil] show];
            } else {
                //error!
                [UIAlertView error:[json objectForKey:@"reason"]];
            }
        }];
//    }
    [self.view endEditing:YES];
}

- (IBAction)btnFBLoginTapped:(UIButton *)sender{

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    for (UIView * txt in self.view.subviews){
        if ([txt isKindOfClass:[UITextField class]] && [txt isFirstResponder]) {
            [txt resignFirstResponder];
        }
    }
}

@end
