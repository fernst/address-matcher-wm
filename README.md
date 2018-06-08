# Back End Coding 

<h2>Usage</h2>

<h3>Run tests</h3>

On the root of the project, run the following command:

```mvn clean test```

<h3>Build executable jar file</h3>

On the root of the project, run the following command:

```mvn clean package```

<h3>Execute the program</h3>

After execute the previous step, you can execute:

```java -jar target/geolocation-finder.jar ```

You'll see the output in STDOUT, and any errors/exceptions that are handled in STDERR

<h2>Original Instructions</h2>

<h3>Please fork this repository and push when you are completed.</h3>

Write a program that reads the included file of addresses and finds the geolocation of each address.  

Use https://maps.googleapis.com/maps/api/geocode/json?address=<address string> 

*Note that this free api is subject to rate limiting. You will need to handle this with a maximum number of retries being 5. If an address cannot be geolocated or maximum number of retries has exceeded, set the status to "NOT_FOUND"

The application should output a JSONArray of JSONObjects. 

Example output
```
[{
  "address": "address",
  "status": "FOUND"
  "location": {
    "lat": 1234.56,
    "lng": 1234.56
  }
}]
```
**You may use a JSON library but otherwise stick to what comes with Java JDK

We leave the implementation open ended so you can work comfortably using tech you are used to.

