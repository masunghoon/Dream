//
//  LifeScreenDetailNew.m
//  Dream_iOS
//
//  Created by Ma Sunghoon on 2014. 1. 24..
//  Copyright (c) 2014년 Ma Sunghoon. All rights reserved.
//

#import "API.h"
#import "LifeScreenDetailNew.h"
#import "LifeScreenDetailCell.h"
#import "LifeScreenDescAdd.h"
#import "LifeScreenAdd.h"
#import "LifeScreenRptAdd.h"

#import "UIAlertView+error.h"

@interface LifeScreenDetailNew ()

@property (nonatomic) NSInteger isPriv;
@property (nonatomic) NSMutableArray *bucketDescSize;

@end

@implementation LifeScreenDetailNew

@synthesize bucket;
@synthesize bucketDetailList;
@synthesize bucketDetailListMeta;
@synthesize bucketDescSize;

@synthesize addSubBucketBtn;

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

    bucketDetailList = [NSMutableArray arrayWithCapacity:0];
    bucketDetailListMeta = [NSMutableArray arrayWithCapacity:0];

    NSMutableDictionary *tmpDic = [[NSMutableDictionary alloc] init];
//    for(NSString *key in bucket){
//        if([key isEqualToString:@"subBuckets"]){
//            [bucketDetailList addObjectsFromArray:[bucket objectForKey:key]];
//        } else {
//            [tmpDic setValue:[bucket objectForKey:key] forKey:key];
//        }
//    }
//    [bucketDetailList addObject:tmpDic];
    for(NSString *key in bucket){
        if(![key isEqualToString:@"subBuckets"]){
            [tmpDic setValue:[bucket objectForKey:key] forKey:key];
        }
    }
    [bucketDetailList addObject:tmpDic];

    for(int i=0; i< [[bucket objectForKey:@"subBuckets"] count]; i++){
        NSMutableDictionary *tmpDic2 = [[NSMutableDictionary alloc] init];
        for(NSString *key in [[bucket objectForKey:@"subBuckets"] objectAtIndex:i])
            if(![key isEqualToString:@"subBuckets"]){
                [tmpDic2 setValue:[[[bucket objectForKey:@"subBuckets"] objectAtIndex:i] objectForKey:key] forKey:key];
            }
        [bucketDetailList addObject:tmpDic2];
    }

    [self sortBucketDetailList];

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
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return [bucketDetailList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"LifeScreenDetailCell";
    LifeScreenDetailCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];

//    NSMutableDictionary *bucket = nil;
//    bucket = [bucketDetailList objectAtIndex:indexPath.row];

    // Configure the cell...
    #pragma mark - Affect to all type of Buckets
    [cell setIndex:indexPath.row];
    [[cell bucketTitle] setText:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"title"]];
    [[cell bucketDue] setText:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"deadline"]];
    [[cell bucketLevel] setText:[NSString stringWithFormat:@"LV.%d",[[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"level"] intValue]+1]];


    if([[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] == [NSNull null] || [[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] isEqualToString:@""]){
        [cell bucketDescView].hidden = YES;
    } else {
        [cell bucketDescView].hidden = NO;
        [cell bucketDescBtn].enabled = NO;

        NSString *text = [[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"];
        CGSize withinSize = CGSizeMake(225, CGFLOAT_MAX);
        CGRect textRect = [text boundingRectWithSize:withinSize
                                             options:NSStringDrawingUsesLineFragmentOrigin
                                          attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
                                             context:nil];
        CGSize size = textRect.size;

        [cell bucketDesc].frame = CGRectMake(15, 12, 240, size.height);
        [cell bucketDescView].frame = CGRectMake(20, 82, 280, size.height + 30);

        [cell bucketDescBtn].frame = CGRectOffset([cell bucketDescBtn].frame, 0, size.height + 35);
        [cell bucketPrivBtn].frame = CGRectOffset([cell bucketPrivBtn].frame, 0, size.height + 35);
        [cell bucketShareBtn].frame = CGRectOffset([cell bucketShareBtn].frame, 0, size.height + 35);
        [cell bucketPhotoBtn].frame = CGRectOffset([cell bucketPhotoBtn].frame, 0, size.height + 35);
        [cell bucketDeleteBtn].frame = CGRectOffset([cell bucketDeleteBtn].frame, 0, size.height + 35);
        [cell bucketRepeatBtn].frame = CGRectOffset([cell bucketRepeatBtn].frame, 0, size.height + 35);

        [[cell bucketDesc] setText:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"]];
    }

    if ([[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"is_private"] intValue]){
        [[cell bucketPrivBtn] setImage:[UIImage imageNamed:@"glyphicons_203_lock.png"] forState:UIControlStateNormal];
    } else {
        [[cell bucketPrivBtn] setImage:[UIImage imageNamed:@"glyphicons_204_unlock.png"] forState:UIControlStateNormal];
    }

    if([[[bucketDetailListMeta objectAtIndex:indexPath.row] objectForKey:@"expanded"] intValue]){
//        NSLog(@"expanded");
        [[cell bucketExpandBtn] setImage:[UIImage imageNamed:@"glyphicons_369_collapse_top.png"] forState:UIControlStateNormal];
    } else {
        [[cell bucketExpandBtn] setImage:[UIImage imageNamed:@"glyphicons_367_expand.png"] forState:UIControlStateNormal];
        [cell bucketDescBtn].hidden = YES;
        [cell bucketPrivBtn].hidden = YES;
        [cell bucketShareBtn].hidden = YES;
        [cell bucketPhotoBtn].hidden = YES;
        [cell bucketDeleteBtn].hidden = YES;
        [[cell bucketRepeatBtn] setHidden:YES];
        [[cell bucketDescBtn] setHidden:YES];
        [[cell bucketDescView] setHidden:YES];
    }

    [[cell bucketPrivBtn] addTarget:self action:@selector(bucketPrivBtnTapped:) forControlEvents:UIControlEventTouchUpInside];
    [[cell bucketShareBtn] addTarget:self action:@selector(bucketShareBtnTapped:) forControlEvents:UIControlEventTouchUpInside];
    [[cell bucketPhotoBtn] addTarget:self action:@selector(bucketPhotoBtnTapped:) forControlEvents:UIControlEventTouchUpInside];
    [[cell bucketDeleteBtn] addTarget:self action:@selector(bucketDeleteBtnTapped:) forControlEvents:UIControlEventTouchUpInside];
    [[cell bucketRepeatBtn] addTarget:self action:@selector(bucketRepeatBtnTapped:) forControlEvents:UIControlEventTouchUpInside];
    [[cell bucketExpandBtn] addTarget:self action:@selector(bucketExpandBtnTapped:) forControlEvents:UIControlEventTouchUpInside];

    UITapGestureRecognizer *tgrDesc = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(bucketDescViewTapped:)];
    [tgrDesc setNumberOfTapsRequired:1];
    [[cell bucketDescView] addGestureRecognizer:tgrDesc];
    [[cell bucketDescView] setUserInteractionEnabled:YES];

    UITapGestureRecognizer *tgrTitle = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(bucketTitleViewTapped:)];
    [tgrTitle setNumberOfTapsRequired:1];
    [[cell bucketTitleView] addGestureRecognizer:tgrTitle];
    [[cell bucketTitleView] setUserInteractionEnabled:YES];

    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    if ([[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"level"] isEqualToString:@"0"]){
        [bucketDetailListMeta  addObject:[NSMutableDictionary dictionaryWithObjectsAndKeys:@"1",@"expanded",nil]];
    } else {
        [bucketDetailListMeta  addObject:[NSMutableDictionary dictionaryWithObjectsAndKeys:@"0",@"expanded",nil]];
    }

    if ([[[bucketDetailListMeta objectAtIndex:indexPath.row] objectForKey:@"expanded"] intValue]){
//        if([bucket objectForKey:@"description"] == [NSNull null] || [[bucket objectForKey:@"description"] isEqualToString:@""]){
        if([[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] == [NSNull null] || [[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] isEqualToString:@""]){
            [[bucketDetailListMeta objectAtIndex:indexPath.row] setValue:[NSString stringWithFormat:@"%f",130.00] forKey:@"cellHeight"];
            return 130;
        } else {
            NSString *text = [bucket objectForKey:@"description"];
            CGSize withinSize = CGSizeMake(225, CGFLOAT_MAX);
            CGRect textRect = [text boundingRectWithSize:withinSize
                                                 options:NSStringDrawingUsesLineFragmentOrigin
                                              attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14.0f]}
                                                 context:nil];
            CGSize size = textRect.size;
            [[bucketDetailListMeta objectAtIndex:indexPath.row] setValue:[NSString stringWithFormat:@"%f",size.height] forKey:@"cellHeight"];
            return size.height + 165;
        }
    } else {
        CGSize size = CGSizeMake(0, 0);
        [[bucketDetailListMeta objectAtIndex:indexPath.row] setValue:[NSString stringWithFormat:@"%f",size.height] forKey:@"cellHeight"];
        return 90;
    }



}

- (void)sortBucketDetailList{
    NSMutableDictionary *tmpDic;
    int indexMin;
    for (int i=0; i<[bucketDetailList count]-1; i++){
        indexMin = i;
        for (int j=i+1;j<[bucketDetailList count]; j++){
            if ([[bucketDetailList[j] objectForKey:@"level"] intValue] < [[bucketDetailList[indexMin] objectForKey:@"level"] intValue]){
                indexMin = j;
            }
            tmpDic = bucketDetailList[indexMin];
            bucketDetailList[indexMin] = bucketDetailList[i];
            bucketDetailList[i] = tmpDic;
        }
    }
}

- (void)bucketPrivBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    LifeScreenDetailCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    NSMutableDictionary *dictionary = [bucketDetailList objectAtIndex:indexPath.row];

    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [params setValue:[NSString stringWithFormat:@"%d", ![[dictionary objectForKey:@"is_private"] intValue]] forKey:@"is_private"];
    [[API sharedInstance] putToURI:[NSString stringWithFormat:@"api/bucket/%@",[dictionary objectForKey:@"id"]]
                        withParams:params
                      onCompletion:^(NSDictionary *json){
                          if(![json objectForKey:@"error"]){
                              [[bucketDetailList objectAtIndex:indexPath.row] setValue:[[json objectForKey:@"bucket"] objectForKey:@"is_private"]  forKey:@"is_private"];
                              if([[[json objectForKey:@"bucket"] objectForKey:@"is_private"] intValue]){
                                  NSLog(@"1");
                                  [[cell bucketPrivBtn]setImage:[UIImage imageNamed:@"glyphicons_203_lock.png"] forState:UIControlStateNormal];
                              } else {
                                  NSLog(@"2");
                                  [[cell bucketPrivBtn]setImage:[UIImage imageNamed:@"glyphicons_204_unlock.png"] forState:UIControlStateNormal];
                              }
                          } else {
                              [UIAlertView error:@"error"];
                          }
                      }
    ];
}

- (void)bucketShareBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    NSMutableDictionary *dictionary = [bucketDetailList objectAtIndex:indexPath.row];

    LifeScreenAdd *lifeScreenAdd = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjAddBucket"];
    [lifeScreenAdd setSaveType:@"SubBucket"];
    [lifeScreenAdd setBucket:nil];
    [lifeScreenAdd setNavTitle:@"Add SubBucket" withSubTitle:[dictionary objectForKey:@"title"]];
    [lifeScreenAdd setParams:[NSMutableDictionary dictionaryWithObjectsAndKeys:[dictionary objectForKey:@"id"],@"parentID",
                             [NSString stringWithFormat:@"%d",[[dictionary objectForKey:@"level"] intValue]+1],@"level",
                             @"1",@"is_live",
                             @"",@"scope",
                             @"",@"rnage",nil]];
    [self.navigationController pushViewController:lifeScreenAdd animated:YES];
}

- (void)bucketDeleteBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];

    [[API sharedInstance] deleteFromURI:[NSString stringWithFormat:@"api/bucket/%@",[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"id"]]
                             withParams:nil
                           onCompletion:^(id json){
                               if(![json objectForKey:@"error"]){
                                   [UIAlertView error:@"Delete Succeeded"];
                                   NSLog(@"%@",[bucketDetailList objectAtIndex:indexPath.row]);
                                   [bucketDetailList removeObjectAtIndex:indexPath.row];
                                   [self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
//                                   SWRevealViewController *viewController = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjMain"];
//                                   [self presentViewController:viewController animated:YES completion:nil];
                               } else {
                                   [UIAlertView error:@"Deletion Failed"];

                               }
                           }];
}

- (void)bucketRepeatBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    NSMutableDictionary *dictionary = [bucketDetailList objectAtIndex:indexPath.row];
    LifeScreenRptAdd *lifeScreenRptAdd = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjAddRepeat"];

    [lifeScreenRptAdd setBucket:dictionary];
    [lifeScreenRptAdd setNavTitle:@"Set Repeat" withSubTitle:[dictionary objectForKey:@"title"]];
    [self.navigationController pushViewController:lifeScreenRptAdd animated:YES];
}

- (void)bucketExpandBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];
    LifeScreenDetailCell *cell = [self.tableView cellForRowAtIndexPath:indexPath];
    [[bucketDetailListMeta objectAtIndex:indexPath.row] setValue:[NSString stringWithFormat:@"%d", ![[[bucketDetailListMeta objectAtIndex:indexPath.row] objectForKey:@"expanded"] intValue]] forKey:@"expanded"];

    if([[[bucketDetailListMeta objectAtIndex:indexPath.row] objectForKey:@"expanded"] intValue]){
        [[cell bucketExpandBtn] setImage:[UIImage imageNamed:@"glyphicons_369_collapse_top.png"] forState:UIControlStateNormal];
        [[cell bucketDescBtn] setHidden:NO];
        [[cell bucketPrivBtn] setHidden:NO];
        [[cell bucketShareBtn] setHidden:NO];
        [[cell bucketPhotoBtn] setHidden:NO];
        [[cell bucketDeleteBtn] setHidden:NO];
        [[cell bucketRepeatBtn] setHidden:NO];
        if([[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] == [NSNull null] || [[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"] isEqualToString:@""]){
            [[cell bucketDescView] setHidden:YES];
        } else {
            [[cell bucketDescView] setHidden:NO];
            [[cell bucketDesc] setText:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"description"]];
        }
    } else {
        [[cell bucketExpandBtn] setImage:[UIImage imageNamed:@"glyphicons_367_expand.png"] forState:UIControlStateNormal];
        [[cell bucketDescBtn] setHidden:YES];
        [[cell bucketPrivBtn] setHidden:YES];
        [[cell bucketShareBtn] setHidden:YES];
        [[cell bucketPhotoBtn] setHidden:YES];
        [[cell bucketDeleteBtn] setHidden:YES];
        [[cell bucketRepeatBtn] setHidden:YES];
        [[cell bucketDescView] setHidden:YES];
    }
    [self.tableView beginUpdates];
    [self.tableView endUpdates];
}

- (void)bucketTitleViewTapped:(UIGestureRecognizer *)recognizer{
    CGPoint tapPoint = [recognizer locationInView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:tapPoint];

    LifeScreenAdd *lifeScreenAdd = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjAddBucket"];
    [lifeScreenAdd setSaveType:@"Modify"];
    [lifeScreenAdd setBucket:[bucketDetailList objectAtIndex:indexPath.row]];
    [lifeScreenAdd setParams:[NSMutableDictionary dictionaryWithObjectsAndKeys:nil]];
    [lifeScreenAdd setNavTitle:@"Modify Bucket" withSubTitle:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@"title"]];
    [self.navigationController pushViewController:lifeScreenAdd animated:YES];
}

- (void)bucketDescViewTapped:(UIGestureRecognizer *)recognizer {
    CGPoint tapPoint = [recognizer locationInView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:tapPoint];

    LifeScreenDescAdd *lifeScreenDescAdd = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjAddDesc"];
    [lifeScreenDescAdd setBucket:[bucketDetailList objectAtIndex:indexPath.row]];

    [self.navigationController pushViewController:lifeScreenDescAdd animated:YES];

//    [self performSegueWithIdentifier:@"detailToEditDesc" sender:nil];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if([segue.identifier isEqualToString:@"detailToEditDesc"]){
        CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
        NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];

        LifeScreenDescAdd *lifeScreenDescAdd = segue.destinationViewController;

        [lifeScreenDescAdd setBucket:[bucketDetailList objectAtIndex:indexPath.row]];
    }
}

- (IBAction)addSubBucketBtnTapped{
    NSMutableDictionary *dictionary = [bucketDetailList objectAtIndex:0];

    LifeScreenAdd *lifeScreenAdd = [self.storyboard instantiateViewControllerWithIdentifier:@"DreamProjAddBucket"];
    [lifeScreenAdd setSaveType:@"SubBucket"];
    [lifeScreenAdd setBucket:nil];
    [lifeScreenAdd setNavTitle:@"Add SubBucket" withSubTitle:[dictionary objectForKey:@"title"]];
    [lifeScreenAdd setParams:[NSMutableDictionary dictionaryWithObjectsAndKeys:[dictionary objectForKey:@"id"],@"parentID",
                                                                               [NSString stringWithFormat:@"%d",[[dictionary objectForKey:@"level"] intValue]+1],@"level",
                                                                               @"1",@"is_live",
                                                                               @"",@"scope",
                                                                               @"",@"rnage",nil]];
    [self.navigationController pushViewController:lifeScreenAdd animated:YES];
}

- (void)bucketPhotoBtnTapped:(id)sender{
    CGPoint buttonPosition = [sender convertPoint:CGPointZero toView:self.tableView];
    NSIndexPath *indexPath = [self.tableView indexPathForRowAtPoint:buttonPosition];

//    [bucket objectForKey:[[bucketDetailList objectAtIndex:indexPath.row] objectForKey:@""]
//    bucket = [bucket objectAtIndex:indexPath.row];
//
//    NSLog(@"%@",bucket);
//    [self.tableView reloadData];

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
