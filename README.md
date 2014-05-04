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
2. Note the application keys and add the following to the `java` command line when starting:
 * `-Dflickr.apikey=xxxx` where `xxxx` is the API key from the API console in Flickr
 * `-Dflickr.apisecret=xxxx` where `xxxx` is the API secret from the API console in Flickr
 * `-Dflickr.appauthurl=http://www.flickr.com/auth-xxxx` where the URL is the authorization URL set up in Flickr.
4. Run the app with a Flickr album or image to begin the authorization process. Follow the instructions from the app to authorize. Note the authorization token.
5. Re-run the app with the authorization token as a command-line parameter: `-Dflickr.token=xxxxx` where `xxxxx` is the authorization token.

## Running
To run the app, start it use the following as a guide:
```
java -Dflickr.apikey=$FLICKR_KEY -Dflickr.apisecret=$FLICKR_SECRET -Dflickr.appauthurl=$FLICKR_AUTHURL -Dflickr.token=$FLICKR_TOKEN -Dimgur.clientid=$IMGUR_KEY -jar imgdl.jar <Flickr/Imgur/Imgly URL> <destination directory>
```