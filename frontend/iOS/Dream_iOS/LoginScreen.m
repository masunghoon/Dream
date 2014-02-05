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
    // If the session state is any of the two "open" states when the button is clicked
    if (FBSession.activeSession.state == FBSessionStateOpen
            || FBSession.activeSession.state == FBSessionStateOpenTokenExtended) {

        // Close the session and remove the access token from the cache
        // The session state handler (in the app delegate) will be called automatically
        [FBSession.activeSession closeAndClearTokenInformation];

        // If the session state is not any of the two "open" states when the button is clicked
    } else {
        // Open a session showing the user the login UI
        // You must ALWAYS ask for basic_info permissions when opening a session
        [FBSession openActiveSessionWithReadPermissions:@[@"basic_info",@"email",@"user_birthday"]
                                           allowLoginUI:YES
                                      completionHandler:
                                              ^(FBSession *session, FBSessionState state, NSError *error) {

                                                  // Call the app delegate's sessionStateChanged:state:error method to handle session state changes
                                                  [self sessionStateChanged:session state:state error:error];
                                              }];
    }
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


// This method will handle ALL the session state changes in the app
- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error
{
    // If the session was opened successfully
    if (!error && state == FBSessionStateOpen){
        NSLog(@"Session opened");
        // Show the user the logged-in UI
        [self userLoggedIn];
        return;
    }
    if (state == FBSessionStateClosed || state == FBSessionStateClosedLoginFailed){
        // If the session is closed
        NSLog(@"Session closed");
        // Show the user the logged-out UI
        [self userLoggedOut];
    }

    // Handle errors
    if (error){
        NSLog(@"Error");
        NSString *alertText;
        NSString *alertTitle;
        // If the error requires people using an app to make an action outside of the app in order to recover
        if ([FBErrorUtility shouldNotifyUserForError:error] == YES){
            alertTitle = @"Something went wrong";
            alertText = [FBErrorUtility userMessageForError:error];
            [self showMessage:alertText withTitle:alertTitle];
        } else {

            // If the user cancelled login, do nothing
            if ([FBErrorUtility errorCategoryForError:error] == FBErrorCategoryUserCancelled) {
                NSLog(@"User cancelled login");

                // Handle session closures that happen outside of the app
            } else if ([FBErrorUtility errorCategoryForError:error] == FBErrorCategoryAuthenticationReopenSession){
                alertTitle = @"Session Error";
                alertText = @"Your current session is no longer valid. Please log in again.";
                [self showMessage:alertText withTitle:alertTitle];

                // For simplicity, here we just show a generic message for all other errors
                // You can learn how to handle other errors using our guide: https://developers.facebook.com/docs/ios/errors
            } else {
                //Get more error information from the error
                NSDictionary *errorInformation = [[[error.userInfo objectForKey:@"com.facebook.sdk:ParsedJSONResponseKey"] objectForKey:@"body"] objectForKey:@"error"];

                // Show the user an error message
                alertTitle = @"Something went wrong";
                alertText = [NSString stringWithFormat:@"Please retry. \n\n If the problem persists contact us and mention this error code: %@", [errorInformation objectForKey:@"message"]];
                [self showMessage:alertText withTitle:alertTitle];
            }
        }
        // Clear this token
        [FBSession.activeSession closeAndClearTokenInformation];
        // Show the user the logged-out UI
        [self userLoggedOut];
    }
}

// Show the user the logged-out UI
- (void)userLoggedOut
{
    // Set the button title as "Log in with Facebook"
    UIButton *loginButton = [self FBLoginBtn];
    [loginButton setTitle:@"Log in with Facebook" forState:UIControlStateNormal];

    // Confirm logout message
//    [self showMessage:@"You're now logged out" withTitle:@""];
}

// Show the user the logged-in UI
- (void)userLoggedIn
{
    [[API sharedInstance] setFacebookAccessToken:[[FBSession activeSession] accessTokenData]
                                    onCompletion:^(NSDictionary *json){
                                        if(![[json objectForKey:@"status"] isEqualToString:@"error"] && [[json objectForKey:@"id"] intValue] > 0){
                                            //success
                                            [[API sharedInstance]setUser: json];

                                            // Set the button title as "Log out"
                                            UIButton *loginButton = self.FBLoginBtn;
                                            [loginButton setTitle:@"Log out" forState:UIControlStateNormal];

                                            // Welcome message
                                            [self showMessage:@"You're now logged in" withTitle:@"Welcome!"];

                                            SWRevealViewController *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjMain"];
                                            [self presentViewController:viewController animated:YES completion:nil];

                                        } else {
                                            //error!
                                            [UIAlertView error:[json objectForKey:@"reason"]];
                                        }
                                    }
    ];
}

// Show an alert message
- (void)showMessage:(NSString *)text withTitle:(NSString *)title
{
    [[[UIAlertView alloc] initWithTitle:title
                                message:text
                               delegate:self
                      cancelButtonTitle:@"OK!"
                      otherButtonTitles:nil] show];
}

@end
