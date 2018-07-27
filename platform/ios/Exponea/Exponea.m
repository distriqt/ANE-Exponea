/**
 *        __       __               __ 
 *   ____/ /_ ____/ /______ _ ___  / /_
 *  / __  / / ___/ __/ ___/ / __ `/ __/
 * / /_/ / (__  ) / / /  / / /_/ / / 
 * \__,_/_/____/_/ /_/  /_/\__, /_/ 
 *                           / / 
 *                           \/ 
 * http://distriqt.com
 *
 * @file   		Exponea.m
 * @brief  		ActionScript Native Extension
 * @author 		Michael Archbold
 * @created		Jan 19, 2012
 * @copyright	http://distriqt.com/copyright/license.txt
 *
 */


#import "FlashRuntimeExtensions.h"
#import "DTEXPController.h"
#import "DTEXPExtensionContext.h"

#import <CoreNativeExtension/CoreNativeExtension.h>


NSString * const Exponea_VERSION = @"1.0";
NSString * const Exponea_IMPLEMENTATION = @"iOS";

FREContext distriqt_exponea_ctx = nil;
Boolean distriqt_exponea_v = false;
DTEXPExtensionContext* distriqt_exponea_extContext = nil;
DTEXPController* distriqt_exponea_controller = nil;


////////////////////////////////////////////////////////
//	ACTIONSCRIPT INTERFACE METHODS 
//

FREObject ExponeaVersion(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
	FREObject result = NULL;
    @autoreleasepool
    {
        result = [DTFREUtils newFREObjectFromString: Exponea_VERSION];
    }
    return result;
}


FREObject ExponeaImplementation(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
	FREObject result = NULL;
    @autoreleasepool
    {
        result = [DTFREUtils newFREObjectFromString: Exponea_IMPLEMENTATION];
    }
    return result;
}


FREObject ExponeaIsSupported(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
	FREObject result = NULL;
    @autoreleasepool
    {
        result = [DTFREUtils newFREObjectFromBoolean: true ];
    }
    return result;
}


//
//
//  EXTENSION FUNCTIONALITY
//
//

FREObject Exponea_initialise(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* token = [DTFREUtils getFREObjectAsString: argv[0]];
        NSString* url   = [DTFREUtils getFREObjectAsString: argv[1]];
        
        [distriqt_exponea_controller initialise: token
                                            url: url];
    }
    return result;
}


FREObject Exponea_identify(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* customerId = [DTFREUtils getFREObjectAsString: argv[0]];
        
        [distriqt_exponea_controller identify: customerId];
    }
    return result;
}


FREObject Exponea_update(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* propertiesJSON = [DTFREUtils getFREObjectAsString: argv[0]];
        
        NSData *data = [propertiesJSON dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *properties = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
        
        [distriqt_exponea_controller update: properties];
    }
    return result;
}


FREObject Exponea_track(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* type = [DTFREUtils getFREObjectAsString: argv[0]];
        NSString* propertiesJSON = [DTFREUtils getFREObjectAsString: argv[1]];
        double timestamp = [DTFREUtils getFREObjectAsDouble: argv[2]];
        
        NSData *data = [propertiesJSON dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *properties = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];

        [distriqt_exponea_controller track: type
                                properties: properties
                                 timestamp: timestamp ];
    }
    return result;
}


FREObject Exponea_trackSessionStart(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        [distriqt_exponea_controller trackSessionStart];
    }
    return result;
}


FREObject Exponea_trackSessionEnd(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        [distriqt_exponea_controller trackSessionEnd];
    }
    return result;
}


FREObject Exponea_trackGooglePurchase(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        [distriqt_exponea_controller trackGooglePurchase];
    }
    return result;
}

FREObject Exponea_trackVirtualPayment(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* currency = [DTFREUtils getFREObjectAsString: argv[0]];
        int amount = [DTFREUtils getFREObjectAsInt: argv[1]];
        NSString* itemName = [DTFREUtils getFREObjectAsString: argv[2]];
        NSString* itemType = [DTFREUtils getFREObjectAsString: argv[3]];
        
        [distriqt_exponea_controller trackVirtualPayment: currency
                                              withAmount: [NSNumber numberWithInt: amount]
                                            withItemName: itemName
                                            withItemType: itemType ];
    }
    return result;
}

FREObject Exponea_getCurrentSegment(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        NSString* segmentationId = [DTFREUtils getFREObjectAsString: argv[0]];
        NSString* projectSecretToken = [DTFREUtils getFREObjectAsString: argv[1]];
        
        [distriqt_exponea_controller getCurrentSegment: segmentationId
                                     withProjectSecret: projectSecretToken];
    }
    return result;
}

FREObject Exponea_disableAutomaticFlushing(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        [distriqt_exponea_controller disableAutomaticFlushing];
    }
    return result;
}

FREObject Exponea_flush(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        [distriqt_exponea_controller flush];
    }
    return result;
}



FREObject Exponea_getCPUFrequency(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        double cpuFreq = [distriqt_exponea_controller getCPUFrequency];
        result = [DTFREUtils newFREObjectFromDouble: cpuFreq];
    }
    return result;
}


FREObject Exponea_getNumberOfCores(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        int cores = [distriqt_exponea_controller getNumberOfCores];
        result = [DTFREUtils newFREObjectFromInt: cores];
    }
    return result;
}


FREObject Exponea_getTotalRAM(FREContext ctx, void* funcData, uint32_t argc, FREObject argv[])
{
    FREObject result = NULL;
    @autoreleasepool
    {
        double totalRAM = [distriqt_exponea_controller getTotalRAM];
        result = [DTFREUtils newFREObjectFromDouble: totalRAM];
    }
    return result;
}


////////////////////////////////////////////////////////
// FRE CONTEXT 
//

void ExponeaContextInitializer(void* extData, const uint8_t* ctxType, FREContext ctx, uint32_t* numFunctionsToTest, const FRENamedFunction** functionsToSet)
{

    //
	//	Add the ACTIONSCRIPT interface
	
	static FRENamedFunction distriqt_exponeaFunctionMap[] =
    {
        MAP_FUNCTION( ExponeaVersion,               "version",              NULL ),
        MAP_FUNCTION( ExponeaImplementation,        "implementation",       NULL ),
        MAP_FUNCTION( ExponeaIsSupported,           "isSupported",          NULL ),
        
        MAP_FUNCTION( Exponea_initialise,           "initialise",           NULL ),
        MAP_FUNCTION( Exponea_identify,             "identify",             NULL ),
        MAP_FUNCTION( Exponea_update,               "update",               NULL ),
        MAP_FUNCTION( Exponea_track,                "track",                NULL ),
       
        MAP_FUNCTION( Exponea_trackSessionStart,    "trackSessionStart",    NULL ),
        MAP_FUNCTION( Exponea_trackSessionEnd,      "trackSessionEnd",      NULL ),

        MAP_FUNCTION( Exponea_trackGooglePurchase,      "trackGooglePurchase",      NULL ),
        MAP_FUNCTION( Exponea_trackVirtualPayment,      "trackVirtualPayment",      NULL ),
        MAP_FUNCTION( Exponea_getCurrentSegment,        "getCurrentSegment",        NULL ),
        MAP_FUNCTION( Exponea_disableAutomaticFlushing, "disableAutomaticFlushing", NULL ),
        MAP_FUNCTION( Exponea_flush,                    "flush",                    NULL ),

        MAP_FUNCTION( Exponea_getCPUFrequency,          "getCPUFrequency",          NULL ),
        MAP_FUNCTION( Exponea_getNumberOfCores,         "getNumberOfCores",         NULL ),
        MAP_FUNCTION( Exponea_getTotalRAM,              "getTotalRAM",              NULL ),
        
        
    };
    
    *numFunctionsToTest = sizeof( distriqt_exponeaFunctionMap ) / sizeof( FRENamedFunction );
    *functionsToSet = distriqt_exponeaFunctionMap;
    
	
	//
	//	Store the global states
	
    distriqt_exponea_ctx = ctx;
    distriqt_exponea_v = false;
    
    distriqt_exponea_extContext = [[DTEXPExtensionContext alloc] init];
    distriqt_exponea_extContext.context = distriqt_exponea_ctx;
    
    distriqt_exponea_controller = [[DTEXPController alloc] init];
    distriqt_exponea_controller.extContext = distriqt_exponea_extContext;

}


/**	
 *	The context finalizer is called when the extension's ActionScript code calls the ExtensionContext instance's dispose() method. 
 *	If the AIR runtime garbage collector disposes of the ExtensionContext instance, the runtime also calls ContextFinalizer().
 */
void ExponeaContextFinalizer(FREContext ctx) 
{
    if (distriqt_exponea_controller != nil)
    {
        distriqt_exponea_controller.extContext = nil;
        distriqt_exponea_controller = nil;
    }
    
    if (distriqt_exponea_extContext != nil)
    {
        distriqt_exponea_extContext.context = nil;
        distriqt_exponea_extContext = nil;
    }

	distriqt_exponea_ctx = nil;
}


/**
 *	The extension initializer is called the first time the ActionScript
 *		side of the extension calls ExtensionContext.createExtensionContext() 
 *		for any context.
 */
void ExponeaExtInitializer( void** extDataToSet, FREContextInitializer* ctxInitializerToSet, FREContextFinalizer* ctxFinalizerToSet ) 
{
	*extDataToSet = NULL;
	*ctxInitializerToSet = &ExponeaContextInitializer;
	*ctxFinalizerToSet   = &ExponeaContextFinalizer;
} 


/**
 *	The extension finalizer is called when the runtime unloads the extension. However, it is not always called.
 */
void ExponeaExtFinalizer( void* extData ) 
{
	// Nothing to clean up.	
}

