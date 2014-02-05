//
//  LoginScreen.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FacebookSDK/FacebookSDK.h>

@interface LoginScreen : UIViewController <FBLoginViewDelegate>{
    //Login form fields
    IBOutlet UITextField *fldEmail;
    IBOutlet UITextField *fldPasswd;
}

@property (nonatomic, strong) IBOutlet UILabel* label;
@property (nonatomic, weak) IBOutlet UIButton *FBLoginBtn;
@property (nonatomic, strong) UIColor* color;
@property (nonatomic, strong) NSString* text;

- (void)sessionStateChanged:(FBSession *)session state:(FBSessionState) state error:(NSError *)error;
- (void)userLoggedIn;
- (void)userLoggedOut;

@end
