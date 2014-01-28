//
//  LoginScreen.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LoginScreen : UIViewController {
    //Login form fields
    IBOutlet UITextField *fldEmail;
    IBOutlet UITextField *fldPasswd;
}

@property (nonatomic, strong) IBOutlet UILabel* label;
@property (nonatomic, strong) UIColor* color;
@property (nonatomic, strong) NSString* text;



@end
