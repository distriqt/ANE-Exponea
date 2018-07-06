
## Add the Extension

First step is always to add the extension to your development environment. 
To do this use the tutorial located [here](http://airnativeextensions.com/knowledgebase/tutorial/1).



## Required ANEs

### Core ANE

The Core ANE is required by this ANE. You must include and package this extension in your application.

The Core ANE doesn't provide any functionality in itself but provides support libraries and frameworks used by our extensions. It also includes some centralised code for some common actions that can cause issues if they are implemented in each individual extension.

You can access this extension here: [https://github.com/distriqt/ANE-Core](https://github.com/distriqt/ANE-Core).



### Google Play Services 

This ANE requires usage of certain aspects of the Google Play Services client library. 
The client library is available as a series of ANEs that you add into your applications packaging options. 
Each separate ANE provides a component from the Play Services client library and are used by different ANEs. 
These client libraries aren't packaged with this ANE as they are used by multiple ANEs and separating them 
will avoid conflicts, allowing you to use multiple ANEs in the one application.

This ANE requires the following Google Play Services:

- [com.distriqt.playservices.Base.ane](https://github.com/distriqt/ANE-GooglePlayServices/raw/master/lib/com.distriqt.playservices.Base.ane)
- [com.distriqt.playservices.Ads.ane](https://github.com/distriqt/ANE-GooglePlayServices/raw/master/lib/com.distriqt.playservices.Ads.ane)
- [com.distriqt.playservices.GCM.ane](https://github.com/distriqt/ANE-GooglePlayServices/raw/master/lib/com.distriqt.playservices.GCM.ane)

You must include the above native extensions in your application along with this extension, 
and you need to ensure they are packaged with your application.

You can access the Google Play Services client library extensions here: 
[https://github.com/distriqt/ANE-GooglePlayServices](https://github.com/distriqt/ANE-GooglePlayServices).



## Extension IDs

The following should be added to your `extensions` node in your application descriptor to identify all the required ANEs in your application:

```xml
<extensions>
	<extensionID>com.distriqt.Exponea</extensionID>
	<extensionID>com.distriqt.Core</extensionID>
	<extensionID>com.distriqt.playservices.Base</extensionID>
	<extensionID>com.distriqt.playservices.Ads</extensionID>
	<extensionID>com.distriqt.playservices.GCM</extensionID>
</extensions>
```




## Android Manifest Additions

The Exponea ANE requires a few additions to the manifest. You should add the listing below to your manifest.


```xml
<manifest android:installLocation="auto">
	
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.GET_ACCOUNTS" />

	<application android:allowBackup="true">

		<receiver
			android:name="com.infinario.android.infinariosdk.GcmBroadcastReceiver"
			android:permission="com.google.android.c2dm.permission.SEND" >
			<intent-filter>
				<action android:name="com.google.android.c2dm.intent.RECEIVE" />
				<category android:name="com.example.gcm" />
			</intent-filter>
		</receiver>
		<service android:name="com.infinario.android.infinariosdk.GcmIntentService" />

		<receiver android:name="com.infinario.android.infinariosdk.ReferrerReceiver"
			android:exported="true" >
			<intent-filter>
				<action android:name="com.android.vending.INSTALL_REFERRER" />
			</intent-filter>
		</receiver>

	</application>

</manifest>
```


## iOS Additions

App Transport Security (ATS) is a privacy feature introduced in iOS 9. It's enabled 
by default for new applications and enforces secure connections.

> If you are using `https` for your instance you shouldn't need to add this addition.

All iOS 9 or higher devices running apps that don't disable ATS will be affected by 
this change. This may affect your app's integration with the Google Mobile Ads SDK.

The following log message appears when a non-ATS compliant app attempts to serve an 
ad via HTTP on iOS 9+:

> App Transport Security has blocked a cleartext HTTP (`http://`) resource load since it is insecure. Temporary exceptions can be configured via your app's Info.plist file.

To ensure you are not impacted by ATS, add the following to the `InfoAdditions`
node in your `iPhone` settings of your application descriptor:

```xml
<key>NSAppTransportSecurity</key>
<dict>
	<key>NSAllowsArbitraryLoads</key>
	<true/>
</dict>
```


### Dynamic Framework

Exponea utilises a dynamic framework which means some additional files need to be packaged with your application.
The dynamic framework must be packaged with your application in a particular location as outlined below. Additionally we highly recommend using AIR SDK version 30 or higher due to some additions making working with dynamic frameworks easier in that release.

Firstly you need to create a `Frameworks` directory at the root level of your application. This is the directory that must contain all the dynamic frameworks and libraries required by your application. In this case we just need to add the `ExponeaSDK.framework`. You can find this in the repository alongside the `ane` file.

>
> If your application is crashing on launch most likely it will be due to incorrectly adding the framework or naming/location of the `Frameworks` directory. You can check the logs for error messages relating the the dynamic framework.
>





