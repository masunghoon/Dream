//
//  LifeScreenMain.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 8..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "UICKeyChainStore.h"
//#import "HHPanningTableViewCell.h"

#import "LifeScreenAdd.h"
#import "LifeScreenMain.h"
#import "BucketListCell.h"
#import "LifeScreenDetail.h"
#import "LifeScreenDetailNew.h"
#import "UIAlertView+error.h"
#import "SWRevealViewController.h"

@interface LifeScreenMain ()

@property (nonatomic, weak) UIRefreshControl *refreshControl;
@property (nonatomic) IBOutlet UIBarButtonItem *revealButtonItem;

@end

@implementation LifeScreenMain

@synthesize bucketListMain;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];

    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    bucketListMain = [NSMutableArray arrayWithCapacity:0];

    [self checkUserLoggedIn];

    [self setNavTitle:@"Life - 10Years"];
    handlePinch = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(handlePinch:)];
    [self.view addGestureRecognizer:handlePinch];

    // Setup refresh control for example app
    UIRefreshControl *refreshControl = [[UIRefreshControl alloc] init];
    [refreshControl addTarget:self action:@selector(refreshBuckets:) forControlEvents:UIControlEventValueChanged];

    [self.tableView addSubview:refreshControl];
    self.refreshControl = refreshControl;

    // SWReveal
    [self.revealButtonItem setTarget: self.revealViewController];
    [self.revealButtonItem setAction: @selector( revealToggle: )];

    //set the gesture
    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [self.bucketListMain count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"BucketListCell";
    BucketListCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];

    NSMutableDictionary *bucket = nil;

    bucket = [self.bucketListMain objectAtIndex:indexPath.row];

    #pragma mark TableViewCell Drawer Set

    UIView *drawerView = [[UIView alloc] initWithFrame:cell.frame];
    drawerView.backgroundColor = [UIColor colorWithWhite:0.8f alpha:1.0f];

    cell.drawerView = drawerView;

    UIImage *checkedImage;
    if ([[bucket objectForKey:@"is_live"] intValue]){
        checkedImage = [UIImage imageNamed:@"unchecked.png"];
    } else {
        checkedImage = [UIImage imageNamed:@"checked.png"];
    }
    UIButton *doneButton = [UIButton buttonWithType:UIButtonTypeCustom];
    doneButton.frame = CGRectMake(50, 35, 30, 30);
    [doneButton setImage:checkedImage forState:UIControlStateNormal];
    [cell.bucketIsDone setImage:checkedImage forState:UIControlStateNormal];
    [doneButton setTintColor:[UIColor whiteColor]];
    [cell.drawerView addSubview:doneButton];

    UIImage *shareImage = [UIImage imageNamed:@"glyphicons_308_share_alt.png"];
    UIButton *shareButton = [UIButton buttonWithType:UIButtonTypeCustom];
    shareButton.frame = CGRectMake(140, 35, 30, 30);
    [shareButton setImage:shareImage forState:UIControlStateNormal];
    [shareButton setTintColor:[UIColor whiteColor]];
    [cell.drawerView addSubview:shareButton];

    UIImage *deleteImage = [UIImage imageNamed:@"glyphicons_016_bin.png"];
    UIButton *deleteButton = [UIButton buttonWithType:UIButtonTypeCustom];
    deleteButton.frame = CGRectMake(230, 35, 30, 30);
    [deleteButton setImage:deleteImage forState:UIControlStateNormal];
    [deleteButton setTintColor:[UIColor whiteColor]];
    [cell.drawerView addSubview:deleteButton];

    [cell.bucketIsDone addTarget:self action:@selector(doneButtonTapped:) forControlEvents:UIControlEventTouchUpInside];
    [doneButton addTarget:self action:@selector(doneButtonTapped:) forControlEvents:UIControlEventTouchUpInside];

    [deleteButton addTarget:self action:@selector(deleteButtonTapped:) forControlEvents:UIControlEventTouchUpInside];
    cell.directionMask = 1;

#pragma mark TableViewCell Drawer Set END

    [cell setIndex:indexPath.row];
    [[cell titleLabel] setText:[bucket objectForKey:@"title"]];
    //warning - have to change Source to Scope & Range Data
    [[cell rangeLabel] setText:[bucket objectForKey:@"deadline"]];
    [[cell remainDaysLabel] setText:[NSString stringWithFormat:@"%ld days", (long)[self getRemainDays:[bucket objectForKey:@"deadline"]]]];

    return cell;
}

#pragma mark -
#pragma mark Table view delegate

- (NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];

    if ([cell isKindOfClass:[BucketListCell class]]) {
        BucketListCell *panningTableViewCell = (BucketListCell *)cell;

        if ([panningTableViewCell isDrawerRevealed]) {
            return nil;
        }
    }

    return indexPath;
}



- (NSInteger)getRemainDays:(NSString *)dueDate {
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"yyyy-LL-d"];

    NSDate *date = [dateFormat dateFromString:dueDate];
    NSDate *today = [NSDate date];

    NSDateComponents *dateComp;

    dateComp = [[NSCalendar currentCalendar] components:NSDayCalendarUnit
                                               fromDate:today
                                                 toDate:date
                                                options:0];

    return [dateComp day];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 100;
}

- (void)setNavTitle:(NSString *)navTitle {
    self.navigationItem.title = navTitle;
}

- (void)checkUserLoggedIn {
    [[API sharedInstance] getFromURI:@"api/resource"
                          withParams:nil
                        onCompletion:^(id json){
                            if(![[json objectForKey:@"status"] isEqualToString:@"error"] && [[json objectForKey:@"id"] intValue] > 0){
                                [[API sharedInstance]setUser: json];
                                [self fetchBucketList];
                            } else {
                                [[API sharedInstance]setUser: json];
                                [self performSegueWithIdentifier:@"LifeToSignIn" sender:nil];
                            }
                        }
    ];
}

- (void)fetchBucketList {
    [[API sharedInstance] getFromURI:[NSString stringWithFormat:@"api/test/%@", [[[API sharedInstance] user] objectForKey:@"username"]]
                          withParams:nil
                        onCompletion:^(id json){
                            if(json != nil){
                                [[API sharedInstance] setMyBucketList:json];
//                                [[[API sharedInstance] myBucketList] = [json mutableCopy]];
                                [bucketListMain removeAllObjects];
                                for (int i=0; i<[json count]; i++){
                                    if([[[[[API sharedInstance] myBucketList] objectAtIndex:i] objectForKey:@"scope"] isEqualToString:@"DECADE"]) {
                                        [bucketListMain addObject:[[[API sharedInstance] myBucketList] objectAtIndex:i]];
                                    }
                                }
//                                [self.bucketListMain addObjectsFromArray:[[API sharedInstance] myBucketList]];
                                [self.tableView reloadData];
                            } else {
                                [UIAlertView error:@"No Buckets"];
                            }
                        }
    ];
}

#pragma mark - UIRefreshControl Selector
- (void)refreshBuckets:(UIRefreshControl*)refreshControl
{
    [refreshControl beginRefreshing];
    [self fetchBucketList];
    [refreshControl endRefreshing];
}

- (void)handlePinch:(UIPinchGestureRecognizer *)recognizer{
    NSLog(@"Pinch Scale: %f", recognizer.scale);

    if (recognizer.scale > 2.0f){
        recognizer.scale = 1.0f;
        if([self.navigationItem.title isEqualToString:@"Life - 10Years"]){
            [bucketListMain removeAllObjects];
            for (int i=0; i< [[[API sharedInstance] myBucketList] count]; i++){
                if([[[[[API sharedInstance] myBucketList] objectAtIndex:i] objectForKey:@"scope"] isEqualToString:@"YEARLY"]) {
                    [bucketListMain addObject:[[[API sharedInstance] myBucketList] objectAtIndex:i]];
                }
                [[self tableView] reloadData];
                [self setNavTitle:@"1Y"];
            }
        } else if ([self.navigationItem.title isEqualToString:@"1Y"]) {
            [bucketListMain removeAllObjects];
            for (int i=0; i< [[[API sharedInstance] myBucketList] count]; i++){
                if([[[[[API sharedInstance] myBucketList] objectAtIndex:i] objectForKey:@"scope"] isEqualToString:@"MONTHLY"]) {
                    [bucketListMain addObject:[[[API sharedInstance] myBucketList] objectAtIndex:i]];
                }
                [[self tableView] reloadData];
                [self setNavTitle:@"1M"];
            }
        }
    } else if (recognizer.scale < 0.75f){
        recognizer.scale = 2.0f;
        if([self.navigationItem.title isEqualToString:@"1M"]){
            [bucketListMain removeAllObjects];
            for (int i=0; i< [[[API sharedInstance] myBucketList] count]; i++){
                if([[[[[API sharedInstance] myBucketList] objectAtIndex:i] objectForKey:@"scope"] isEqualToString:@"YEARLY"]) {
                    [bucketListMain addObject:[[[API sharedInstance] myBucketList] objectAtIndex:i]];
                }
                [[self tableView] reloadData];
                [self setNavTitle:@"1Y"];
            }
        } else if ([self.navigationItem.title isEqualToString:@"1Y"]) {
            [bucketListMain removeAllObjects];
            for (int i=0; i< [[[API sharedInstance] myBucketList] count]; i++){
                if([[[[[API sharedInstance] myBucketList] objectAtIndex:i] objectForKey:@"scope"] isEqualToString:@"DECADE"]) {
                    [bucketListMain addObject:[[[API sharedInstance] myBucketList] objectAtIndex:i]];
                }
                [[self tableView] reloadData];
                [self setNavTitle:@"Life - 10Years"];
            }
        }
    }
}

- (void)doneButtonTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params = [NSMutableDictionary dictionaryWithObjectsAndKeys:
            [NSString stringWithFormat:@"%d", ![[[bucketListMain objectAtIndex:indexPath.row] objectForKey:@"is_live"] intValue]],@"is_live",nil];
    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@", [[bucketListMain objectAtIndex:indexPath.row] objectForKey:@"id"]]
                        withParams:params
                      onCompletion:^(id json){
                          if(![json objectForKey:@"error"]){
                                [self fetchBucketList];
                        } else {
                                [UIAlertView error:@"Deletion Failed"];

                        }
}];
}

- (void)deleteButtonTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    [[API sharedInstance] deleteFromURI:[NSString stringWithFormat:@"api/bucket/%@",[[bucketListMain objectAtIndex:indexPath.row] objectForKey:@"id"]]
                             withParams:nil
                           onCompletion:^(id json){
                               if(![json objectForKey:@"error"]){
                                   [UIAlertView error:@"Delete Succeeded"];
                                   [self fetchBucketList];
                               } else {
                                   [UIAlertView error:@"Deletion Failed"];

                               }
                           }];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if([segue.identifier isEqualToString:@"goToLifeDetail"]){
        LifeScreenDetail *lifeScreenDetail = segue.destinationViewController;
        BucketListCell *bucketListCell = (BucketListCell *)sender;

        [lifeScreenDetail setBucket:[self.bucketListMain objectAtIndex:[bucketListCell index]]];
    } else if ([segue.identifier isEqualToString:@"lifeToDetailNew"]){
        LifeScreenDetailNew *lifeScreenDetailNew = segue.destinationViewController;
        BucketListCell *bucketListCell = (BucketListCell *)sender;
        [lifeScreenDetailNew setBucket:[self.bucketListMain objectAtIndex:[bucketListCell index]]];
    } else if ([segue.identifier isEqualToString:@"mainToAddBucket"]){
        LifeScreenAdd *lifeScreenAdd = segue.destinationViewController;
        [lifeScreenAdd setSaveType:@"Add"];
        [lifeScreenAdd setParams:[NSMutableDictionary dictionaryWithObjectsAndKeys:@"DECACE",@"scope",@"0",@"level",@"1",@"is_live",nil]];
    }
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a story board-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}

 */

@end
