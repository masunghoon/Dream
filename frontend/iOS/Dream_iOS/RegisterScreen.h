//
//  RegisterScreen.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 16..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FacebookSDK/FacebookSDK.h>

@interface RegisterScreen : UIViewController <FBLoginViewDelegate>{
    IBOutlet UITextField *fldEmail;
    IBOutlet UITextField *fldPassword;
    IBOutlet UITextField *fldPasswordCfm;
}

@property  (weak, nonatomic) IBOutlet UIButton *FBLoginBtn;

- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error;
- (void)userLoggedIn;
- (void)userLoggedOut;

@end
