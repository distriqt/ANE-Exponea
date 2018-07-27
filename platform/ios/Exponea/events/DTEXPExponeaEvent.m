//
//  DTEXPExponeaEvent.m
//  Exponea
//
//  Created by Michael Archbold on 20/7/18.
//  Copyright © 2018 distriqt. All rights reserved.
//

#import "DTEXPExponeaEvent.h"

@implementation DTEXPExponeaEvent

+(NSString*) formatSegmentForEvent: (NSString*)segment
{
    @try
    {
        NSMutableDictionary* eventDict = [[NSMutableDictionary alloc] init];
       
        [eventDict setObject: segment forKey: @"segment"];
        
        NSData* jsonData = [NSJSONSerialization dataWithJSONObject: eventDict options: 0 error: nil];
        return [[NSString alloc] initWithData: jsonData encoding:NSUTF8StringEncoding];
    }
    @catch (NSException* e)
    {
    }
    return @"{}";
}

+(NSString*) formatErrorForEvent: (int)code message:(NSString*)message
{
    @try
    {
        NSMutableDictionary* eventDict = [[NSMutableDictionary alloc] init];
        
        [eventDict setObject: [NSNumber numberWithInt: code] forKey: @"errorCode"];
        [eventDict setObject: message forKey: @"message"];
        
        NSData* jsonData = [NSJSONSerialization dataWithJSONObject: eventDict options: 0 error: nil];
        return [[NSString alloc] initWithData: jsonData encoding:NSUTF8StringEncoding];
    }
    @catch (NSException* e)
    {
    }
    return @"{}";
}

@end
