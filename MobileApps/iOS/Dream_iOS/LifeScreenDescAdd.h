//
//  LifeScreenDescAdd.h
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 20..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface LifeScreenDescAdd : UIViewController{
    NSMutableDictionary *bucket;
}

@property (nonatomic, strong) NSMutableDictionary *bucket;

@property (nonatomic, weak) IBOutlet UITextView *bucketDescText;

@end
