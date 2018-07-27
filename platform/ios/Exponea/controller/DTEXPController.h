//
//  DTEXPController.h
//  Exponea
//
//  Created by Michael Archbold on 22/09/2015.
//  Copyright © 2015 distriqt. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DTEXPExtensionContextDelegate.h"


@interface DTEXPController : NSObject

@property id<DTEXPExtensionContextDelegate> extContext;


-(void) initialise: (NSString*)token url:(NSString*)url;

-(void) identify: (NSString*)customerId;

-(void) update: (NSDictionary*)properties;

-(void) track: (NSString*)type properties:(NSDictionary*)properties timestamp:(double)timestamp;


-(void) trackSessionStart;

-(void) trackSessionEnd;

-(void) trackGooglePurchase;

-(void) trackVirtualPayment:(NSString *)currency withAmount:(NSNumber *)amount withItemName:(NSString *)itemName withItemType:(NSString *)itemType;

-(void) getCurrentSegment: (NSString*)segmentationId withProjectSecret: (NSString*)projectSecretToken;

-(void) disableAutomaticFlushing;

-(void) flush;







-(double) getCPUFrequency;

-(int) getNumberOfCores;

-(double) getTotalRAM;



@end
