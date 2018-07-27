//
//  DTEXPController.m
//  Exponea
//
//  Created by Michael Archbold on 22/09/2015.
//  Copyright © 2015 distriqt. All rights reserved.
//

#import "DTEXPController.h"
#import "DTEXPExponeaEvent.h"


#import <ExponeaSDK/ExponeaSDK.h>

#import <stdlib.h>
#import <stdio.h>
#import <sys/types.h>
#import <sys/sysctl.h>
#import <sys/utsname.h>



@implementation DTEXPController

@synthesize extContext;

-(id) init
{
    self = [super init];
    if (self)
    {
        
    }
    return self;
}


-(void) initialise: (NSString*)token url:(NSString*)url
{
    [extContext log: DTEXP_TAG message: @"initialise: %@ url: %@", token, url ];
    @try
    {
        [Exponea getInstance: token andWithTarget: url];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"initialise: ERROR: %@", e.description];
    }
}


-(void) identify: (NSString*)customerId
{
    [extContext log: DTEXP_TAG message: @"identify: %@", customerId ];
    @try
    {
        [Exponea identify: customerId];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"identify: ERROR: %@", e.description];
    }
}


-(void) update: (NSDictionary*)properties
{
    [extContext log: DTEXP_TAG message: @"update: %@", (properties == nil ? @"nil" : properties) ];
    @try
    {
        [Exponea update: properties];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"update: ERROR: %@", e.description];
    }
}


-(void) track: (NSString*)type properties:(NSDictionary*)properties timestamp:(double)timestamp
{
    [extContext log: DTEXP_TAG message: @"track: %@ properties: %@ timestamp: %f",
     type,
     (properties == nil ? @"nil" : properties),
     timestamp ];
    @try
    {
        if (timestamp > 0)
        {
            [Exponea track: type
            withProperties: properties
             withTimestamp: [NSNumber numberWithDouble: timestamp / 1000]];
        }
        else
        {
            [Exponea track: type
            withProperties: properties
             withTimestamp: nil];
        }
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"track: ERROR: %@", e.description];
    }
}


-(void) trackSessionStart
{
    [extContext log: DTEXP_TAG message: @"trackSessionStart" ];
    @try
    {
        [Exponea trackSessionStart];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"trackSessionStart: ERROR: %@", e.description];
    }
}


-(void) trackSessionEnd
{
    [extContext log: DTEXP_TAG message: @"trackSessionEnd" ];
    @try
    {
        [Exponea trackSessionEnd];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"trackSessionEnd: ERROR: %@", e.description];
    }
}


-(void) trackGooglePurchase
{
    [extContext log: DTEXP_TAG message: @"trackGooglePurchase::NOT SUPPORTED" ];
}


-(void) trackVirtualPayment:(NSString *)currency withAmount:(NSNumber *)amount withItemName:(NSString *)itemName withItemType:(NSString *)itemType
{
    [extContext log: DTEXP_TAG message: @"trackVirtualPayment: %@ withAmount: %@ itemName: %@ itemType: %@", currency, amount, itemName, itemType ];
    @try
    {
        [Exponea trackVirtualPayment: currency
                          withAmount: amount
                        withItemName: itemName
                        withItemType: itemType];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"trackVirtualPayment: ERROR: %@", e.description];
    }
}



-(void) getCurrentSegment: (NSString*)segmentationId withProjectSecret: (NSString*)projectSecretToken
{
    [extContext log: DTEXP_TAG message: @"getCurrentSegment: %@ withProjectSecret: %@", segmentationId, projectSecretToken ];
    @try
    {
        [Exponea getCurrentSegment: segmentationId
                 withProjectSecret: projectSecretToken
                      withCallBack:^(BOOL wasSuccessful, ExponeaSegment *segment, NSString *error)
        {
            if (wasSuccessful)
            {
                [self.extContext dispatch: DTEXP_EXPONEAEVENT_GET_CURRENT_SEGMENT_SUCCESS
                                     data: [DTEXPExponeaEvent formatSegmentForEvent: [segment getName]]
                 ];
            }
            else
            {
                [self.extContext dispatch: DTEXP_EXPONEAEVENT_GET_CURRENT_SEGMENT_ERROR
                                     data: [DTEXPExponeaEvent formatErrorForEvent: -1 message: error ]
                 ];
            }
        }];
    }
    @catch(NSException* e)
    {
         [extContext log: DTEXP_TAG message: @"getCurrentSegment: ERROR: %@", e.description];
    }
}


-(void) disableAutomaticFlushing
{
    [extContext log: DTEXP_TAG message: @"disableAutomaticFlushing:" ];
    @try
    {
        [Exponea disableAutomaticFlushing];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"disableAutomaticFlushing: ERROR: %@", e.description];
    }
}


-(void) flush
{
    [extContext log: DTEXP_TAG message: @"flush:" ];
    @try
    {
        [Exponea flush];
    }
    @catch (NSException* e)
    {
        [extContext log: DTEXP_TAG message: @"flush: ERROR: %@", e.description];
    }
}
















//
//
//    ADDITIONAL FUNCTIONALITY
//
//


-(double) getCPUFrequency
{
    [extContext log: DTEXP_TAG message: @"getCPUFrequency" ];
    @try
    {
        return [DTEXPController _sysctlCGFloatForKey: @"hw.cpufrequency"];
    }
    @catch (NSException* e)
    {
    }
    return 0;
}


-(int) getNumberOfCores
{
    [extContext log: DTEXP_TAG message: @"getNumberOfCores" ];
    @try
    {
        return (int)[DTEXPController _sysctlCGFloatForKey: @"hw.ncpu"];
    }
    @catch (NSException* e)
    {
    }
    return 0;
}


-(double) getTotalRAM
{
    [extContext log: DTEXP_TAG message: @"getTotalRAM" ];
    @try
    {
        return (double)[[NSProcessInfo processInfo] physicalMemory];
    }
    @catch (NSException* e)
    {
    }
    return 0;
}






+ (CGFloat)_sysctlCGFloatForKey:(NSString *)key
{
    const char *keyCString = [key UTF8String];
    CGFloat answerFloat = 0;
    
    size_t length = 0;
    sysctlbyname(keyCString, NULL, &length, NULL, 0);
    if (length)
    {
        char *answerRaw = malloc(length * sizeof(char));
        sysctlbyname(keyCString, answerRaw, &length, NULL, 0);
        switch (length)
        {
            case 8:
            {
                answerFloat = (CGFloat)*(int64_t *)answerRaw;
            }
                break;
                
            case 4:
            {
                answerFloat = (CGFloat)*(int32_t *)answerRaw;
            }
                break;
                
            default:
            {
                answerFloat = 0.;
            }
                break;
        }
        free(answerRaw);
    }
    
    return answerFloat;
}



@end
