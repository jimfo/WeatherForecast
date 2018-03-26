# WeatherForecast
## A Weather App

On my LG-G4 I have a Weather Widget from which I modelled this app. 

The background changes to a darker shade based Sunrise/Sunset of the first
city in the list.

### Issues : 
1. I would like the Loading Indicator to overlay the refresh icon until it's done.
2. I made my own Menu Bar instead of using built-in functionality. I didn't know that functionality existed
   so I'll change that later.

![screenshot1](https://user-images.githubusercontent.com/5784029/37881891-dbcd4554-306b-11e8-9376-4c0d84478b10.png)

__Current Weather View with Today Selected__

**-------------------------------------------------------------------------------------------------------------------**

### Issues :
None. I'm happy with how the Fragment is setup.

![screenshot_2](https://user-images.githubusercontent.com/5784029/37882038-86a328f8-306d-11e8-8a8c-11d800e6afbe.png)

__Current Weather View with Five-Day selected.__

**-------------------------------------------------------------------------------------------------------------------**

Cities have long press functionality for delete.
The Floating Action button shrinks and disappears when clicked. It reappears and grows to original size when return
from Add City Activity only if you are returning to Weather List View from the Floating Action Button intent. This
matches the functionality of the Android Weather Widget

### Issues :
1. I used an image from the internet for the Floating Action Button. I would like to switch to the standard Android image.
2. I need to limit the number of cities that can be put in the list to 10.
3. I am using a List View with Adapter, I need to use a RecyclerView here.

![screenshot_3](https://user-images.githubusercontent.com/5784029/37882104-14e09db2-306e-11e8-99c5-da8cd09beca7.png)

__Weather List View__

**-------------------------------------------------------------------------------------------------------------------**

All functionality works as it should. Selected increments/decrements based on number selected,
select all selects all/deselects all, delete button is greyed out when 0 items are selected and
highlighted when at least one city is selected. Matches functionality of the Android Weather Widget.

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


I took the Udacity Sunshine app course and was selected to take part in their Grow with Google course which, I believe, was
a more in depth course than the Sunshine app. ( I was offered the Grow with Google whilst in the middle of the Sunshine app )
I kind of got ahead of myself with this project and need to do a lot of refactoring to incorporate more of what I learned in the 
Grow with Google course. To finish this project as it is I need to add more images to handle all weather codes and complete the
Update Interval Functionality. 

