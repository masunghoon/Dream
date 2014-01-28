//
//  PlanningScreenMain.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 21..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "PlanningCell.h"
#import "PlanningScreenMain.h"
#import "SWRevealViewController.h"
#import "UIAlertView+error.h"

@interface PlanningScreenMain ()

@property (nonatomic) IBOutlet UIBarButtonItem *revealButtonItem;
@end

@implementation PlanningScreenMain

@synthesize planList;
@synthesize dailyPlans;


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

    self.planList = [NSMutableArray arrayWithCapacity:0];
    self.dailyPlans = [NSMutableDictionary dictionaryWithCapacity:0];

    [self fetchPlanList];

    // Setup refresh control for example app
    UIRefreshControl *refreshControl = [[UIRefreshControl alloc] init];
    [refreshControl addTarget:self action:@selector(refreshPlannings:) forControlEvents:UIControlEventValueChanged];

    [self.tableView addSubview:refreshControl];
    self.refreshControl = refreshControl;
    // SWReveal
    [self.revealButtonItem setTarget: self.revealViewController];
    [self.revealButtonItem setAction: @selector( revealToggle: )];

    //set the gesture
//    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
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
    return [self.planList count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    NSDictionary *tmpDic = [self.planList objectAtIndex:section];
    NSMutableArray *tmpArray = [[NSMutableArray alloc] init];
    for (NSString *key in tmpDic){
        tmpArray = [tmpDic objectForKey:key];
    }
    return [tmpArray count];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    NSDictionary *tmpDic = [self.planList objectAtIndex:section];
    NSString *sectionTitle;
    for (NSString *key in tmpDic){
        sectionTitle = key;
    }
    return sectionTitle;

}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"PlanningCell";
    PlanningCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    NSDictionary *dictionary = [self.planList objectAtIndex:indexPath.section];
    NSMutableArray *array = [[NSMutableArray alloc] init];
    for(NSString *key in dictionary){
        [array addObjectsFromArray:[dictionary objectForKey:key]];
    }
    NSDictionary *cellValue = [array objectAtIndex:indexPath.row];
    [[cell planTitleLabel] setText:[cellValue objectForKey:@"title"]];
    if ([[cellValue objectForKey:@"isDone"]intValue] == 1) {
        [[cell planTitleLabel] setTextColor:[UIColor lightGrayColor]];
        [[cell planIsDoneBtn] setTintColor:[UIColor lightGrayColor]];
        [[cell planIsDoneBtn] setImage:[UIImage imageNamed:@"checked.png"] forState:UIControlStateNormal];
    } else {
        [[cell planTitleLabel] setTextColor:[UIColor blackColor]];
        [[cell planIsDoneBtn] setImage:[UIImage imageNamed:@"unchecked.png"] forState:UIControlStateNormal];
    }
    [cell.planIsDoneBtn addTarget:self action:@selector(planIsDoneBtnTapped:event:) forControlEvents:UIControlEventTouchUpInside];

    return cell;
}

- (void) fetchPlanList{
    [[API sharedInstance] getFromURI:[NSString stringWithFormat:@"api/plans/%@",[[[API sharedInstance] user] objectForKey:@"username"]]
                          withParams:nil
                        onCompletion:^(id json){
                            if(json != nil){
                                [self.planList removeAllObjects];
                                [self.dailyPlans removeAllObjects];
                                for (int i=0;i<[json count];i++){
                                    if (![self.dailyPlans objectForKey:[[json objectAtIndex:i] objectForKey:@"date"]]){
                                        [self.dailyPlans setValue:[NSMutableArray arrayWithObject:[json objectAtIndex:i]] forKey:[[json objectAtIndex:i] objectForKey:@"date"]];
                                    } else {
                                        [[self.dailyPlans objectForKey:[[json objectAtIndex:i] objectForKey:@"date"]] addObject:[json objectAtIndex:i]];
                                    }
                                }

                                for(NSString *key in self.dailyPlans){
                                    NSMutableDictionary *tempDic = [[NSMutableDictionary alloc] initWithCapacity:1];
                                    [tempDic setObject:[self.dailyPlans objectForKey:key] forKey:key];
                                    [self.planList addObject:tempDic];
                                }
                                [self.tableView reloadData];
                            } else {
                                [UIAlertView error:@"No Plans Yet"];
                            }
                        }];
}

- (NSString *)getDate:(NSInteger)daysAgo{
    NSDateComponents *componentsToSubtract = [[NSDateComponents alloc] init];
    [componentsToSubtract setDay:daysAgo*-1];

    NSDate *calculatedDay = [[NSCalendar currentCalendar] dateByAddingComponents:componentsToSubtract toDate:[NSDate date] options:0];

    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyyMMdd"];

    return [formatter stringFromDate:calculatedDay];

}

- (void)refreshPlannings:(UIRefreshControl*)refreshControl
{
    [refreshControl beginRefreshing];
    [self fetchPlanList];
    [refreshControl endRefreshing];
}

- (IBAction) planIsDoneBtnTapped:(id)sender event:(id)event{
    NSSet *touches = [event allTouches];
    UITouch *touch = [touches anyObject];
    CGPoint currentTouchPosition = [touch locationInView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint: currentTouchPosition];
    NSDictionary *tmpDic = [self.planList objectAtIndex:indexPath.section];
    NSMutableArray *tmpArr = [[NSMutableArray alloc] init];
    for (NSString *key in tmpDic){
        [tmpArr addObjectsFromArray:[tmpDic objectForKey:key]];
    }
    NSDictionary *dictionary = [tmpArr objectAtIndex:indexPath.row];

    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[NSString stringWithFormat:@"%d", ![[dictionary objectForKey:@"isDone"] intValue]] forKey:@"isDone"];

    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/plan/%@",[dictionary objectForKey:@"id"]]
                        withParams:params
                      onCompletion:^(NSDictionary *json){
                          NSLog(@"%@",json);
                          if(![json objectForKey:@"error"]){
                              [self fetchPlanList];
                          } else {
                              [UIAlertView error:@"error"];
                          }
                      }
    ];
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
