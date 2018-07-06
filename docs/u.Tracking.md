
## Track

You may track any event by calling the `track()` method on the extension. The `track()` method takes one mandatory and two optional arguments. First argument is String type which categorizes your event. This argument is required. You may choose any string you like.

Next two arguments are `properties` and `timestamp`. `properties` is a properties Object which uses String keys and the value may be any basic type (String/Number/int etc). Properties can be used to attach any additional data to the event. 

`timestamp` is in milliseconds since 1970-01-01 00:00:00 GMT and it can be used to mark the time of the event's occurence. The default timestamp is preset to the time of the tracking of the event.


```as3
var type:String = "item_bought";
var properties:Object = {
    item_id: 45
};
var timestamp:Number = (new Date()).time;

Exponea.instance.track(
        type,
        properties,
        timestamp
);
```
