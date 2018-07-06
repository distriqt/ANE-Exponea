

## Support

You can check if the extension supports the current platform and device by checking the `isSupported` flag.

```as3
if (Exponea.isSupported)
{
    // Exponea extension functionality is supported on the current device
}
```

You should make sure you check this flag before attempting to use any of the extensions functionality.



## Initialise

You should perform this once in your application. This initialises the extension using your exponea application token and instance url. 

```as3
var projectToken:String = "";
var instanceUrl:String = "http://distriqt.com";

Exponea.instance.initialise( projectToken, instanceUrl );
```

