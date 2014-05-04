ImageDownloader
===============
Simple command-line Java app to download images from online image hosting albums. Currently supports the following sites:

* Imgur
* Flickr
* Img.ly

Please note: Some services have not been updated in a long time, so your mileage may vary.

## Setup

### Imgur
In order to use this app with Imgur, when starting the app, add the following to the `java` command line: `-Dimgur.clientid=xxxxx` where `xxxxx` is the Imgur app client ID (obtain [a client ID here](http://api.imgur.com/#register)).
### Flickr
In order to use this app with Flickr:

1. Create a new app with Flickr from the [App Garden](https://www.flickr.com/services/). Hint: When creating the app, make sure you create a "Mobile application" as the App Type with Read permissions so you can obtain an authorization URL like: `https://www.flickr.com/auth-xxxxxxxx`
2. In the file `src/name/seanpayne/utils/imgdwn/flickr/apikeys.properties`:
 * Add the application key to `flickr.apikey`
 * Add the application secret to `flickr.apisecret`
 * Add the authorization URL to the app to `flickr.appauthurl`.
3. Save and close the `apikeys.properties` file.
4. Run the app with a Flickr album or image to begin the authorization process. Follow the instructions from the app to authorize. Note the authorization token.
5. Re-run the app with the authorization token as a command-line parameter: `-Dflickr.token=xxxxx` where `xxxxx` is the authorization token.

## Running
To run the app, start it using the following:
```
java -Dflickr.token=$FLICKR_KEY -Dimgur.clientid=$IMGUR_KEY -jar imgdl.jar
```
Replacing `$FLICKR_KEY` with the Flickr authorization token, `$IMGUR_KEY` with the Imgur client ID, and `imgdl.jar` with the name of the JAR file to the application once built.