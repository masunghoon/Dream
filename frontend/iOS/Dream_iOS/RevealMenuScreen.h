//
//  RevealMenuScreen.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 14..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SWUITableViewCell : UITableViewCell
@property (nonatomic) IBOutlet UILabel *label;
@end

@interface RevealMenuScreen : UITableViewController
@property (nonatomic, weak) IBOutlet UITableViewCell *logoutCell;

-(IBAction)cellLogoutTapped:(id)sender;

@end
