
## Identify

To start tracking, you need to identify the customer with their unique customerId. The unique customerId should be a String representing the customer.

```as3
var customerId:String = "test@test.com";
			
Exponea.instance.identify( customerId );
```

Any string value can be used for the customer id.

The identification is performed asynchronously and there is no need to wait for it to finish. All tracked events are stored in the internal SQL database until they are sent to the API.



## Properties

You can update properties about your user by using the `update` function. This allows you to store arbitrary data that is not event-specific (e.g. customer's age, gender, initial referrer). Such data is tied directly to the customer as their properties.

This function takes a properties Object as a parameter:

```as3
var properties:Object = {
    age: 34
};

Exponea.instance.update( properties );
```

These properties will be associated with the current identified user.




