//
//  DTEXPExponeaEvent.h
//  Exponea
//
//  Created by Michael Archbold on 20/7/18.
//  Copyright © 2018 distriqt. All rights reserved.
//

#import <Foundation/Foundation.h>

#define DTEXP_EXPONEAEVENT_GET_CURRENT_SEGMENT_SUCCESS @"get:current:segment:success"
#define DTEXP_EXPONEAEVENT_GET_CURRENT_SEGMENT_ERROR @"get:current:segment:error"

@interface DTEXPExponeaEvent : NSObject

+(NSString*) formatSegmentForEvent: (NSString*)segment;

+(NSString*) formatErrorForEvent: (int)code message:(NSString*)message;


@end
