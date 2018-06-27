# WeatherForecast
## A Weather App

On my LG-G4 I have a Weather Widget from which I modelled this app. 
I download the list of cities that OpenWeatherMap.org uses, I believe it was just over 70,000 cities.
I don't have the full list but I think I got about 20,000 cities represented worldwide.

Some of the functionality:
1. Sunrise and Sunset times per location.
2. The background changes to a darker shade based on Sunrise/Sunset time of the first city in the list.
3. Set Celsius/Fahrenheit
4. Set Mile Per Hour or Meters Per Second.
5. Reorder list.
6. Add citites.
7. Delete cities, long press or Delete City Activity
8. Touch city in city list and move to home page with that city displayed.
9. Five-day forecast.

![screenshot1](https://user-images.githubusercontent.com/5784029/37881891-dbcd4554-306b-11e8-9376-4c0d84478b10.png)

__Current Weather View with Today Selected__

**-------------------------------------------------------------------------------------------------------------------**

![screenshot_2](https://user-images.githubusercontent.com/5784029/37882038-86a328f8-306d-11e8-8a8c-11d800e6afbe.png)

__Current Weather View with Five-Day selected.__

**-------------------------------------------------------------------------------------------------------------------**

Cities have long press functionality for delete.
The Floating Action button shrinks and disappears when clicked. It reappears and grows to original size when return
from Add City Activity only if you are returning to Weather List View from the Floating Action Button intent. This
matches the functionality of the Android Weather Widget

![screenshot_3](https://user-images.githubusercontent.com/5784029/37882104-14e09db2-306e-11e8-99c5-da8cd09beca7.png)

__Weather List View__

**-------------------------------------------------------------------------------------------------------------------**

![screenshot_4](https://user-images.githubusercontent.com/5784029/37882298-281e5002-3070-11e8-855e-8129d8c48f5a.png)

__Delete City Activity__

**-------------------------------------------------------------------------------------------------------------------**

All functionality works except Update Interval, I have not yet implemented that.

![screenshot_5](https://user-images.githubusercontent.com/5784029/37882424-39b9a52c-3071-11e8-9f5d-bb6d67f84d70.png)

__Settings Activity__

**-------------------------------------------------------------------------------------------------------------------**

The grips to the right allow the user to reorder the city list.

![screenshot_6](https://user-images.githubusercontent.com/5784029/37882624-e8f912f6-3072-11e8-8f4e-d08b0a0d7104.png)

__Edit City Activity__

This was my first attempt at making a weather app. I did this before taking Udacity's Nanodegree course and I will come back to this at a future date to update this project with what I've learned.

