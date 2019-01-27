# Earthquake
 App developed for the Mobile Programming Laboratory course
 
## Requirements
* List of all events and/or events in your proximity (distance in km)
* Show on map the same events
* Events’ details screen
* Trigger a notification when occurred a new event using a background service

## Resources
Get an APY Key from https://developers.google.com/maps/documentation/android-sdk/signup  and add on `res/values/google_maps_api.xml`

Http request: http://webservices.rm.ingv.it/fdsnws/event/1/query

GET Parameters:
* starttime=[date, in format “yyyy-mm-dd HH:ii:ss”]
* endtime=[date, in format “yyyy-mm-dd HH:ii:ss”] format=[string, text|xml|kml]
* orderby=[string, time|time-asc|magnitude|magnitude-asc] minmag=[number]
* maxmag=[number] lat=[double]&lon=[double]&maxradiuskm=[number]

 
## Screenshot
![screenshot](https://user-images.githubusercontent.com/34028703/50698113-63d16a80-1044-11e9-924a-81bf31ac2bf6.jpg)

## Authors

* **Andreoli Lorenzo**
* **Cantagallo Andrea**
* **Tramontozzi Paolo**
