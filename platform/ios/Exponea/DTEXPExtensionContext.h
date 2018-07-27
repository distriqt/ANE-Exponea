//
//  DTEXPEventDispatcher.h
//  Exponea
//
//  Created by Michael Archbold on 22/09/2015.
//  Copyright © 2015 distriqt. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DTEXPExtensionContextDelegate.h"
#import "FlashRuntimeExtensions.h"


@interface DTEXPExtensionContext : NSObject<DTEXPExtensionContextDelegate>

@property FREContext context;

@end
