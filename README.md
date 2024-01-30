# HighSpots - App for sharing chill spots

Key technologies: Java, Android Studio, Firebase, GMail API, Google Maps API for Android

## Short Introduction
HighSpots is an Android application that allows users to "pin" their location on a public map and share some information and images to other users about their location or just visit already existing locations. The application allows filtering on locations based on several attributes in radius of 30km. Furthermore, for each user statics such as how many spots the user has added to the map, the number of spots the user has visitted, the number of other people who have visitted any of the user's spots and the average raiting of the user's spots are reported on the homepage. 

## Login, Registration and Authentication
Registration form, as seen in the picture below, requires only email, password and nickname. The email does not need to be real, yet it needs to follow the general email pattern. However, the application allows changing password via email, so it is advisable to be real xD. 

![Registration Form](https://github.com/Djimi02/HighSpots-Project/blob/main/images/Registration.jpg)

Login page, as seen in the picture below, is very simple as it requires only the email and password. In case of forgotten password, the user can click on "Forgot password" then enter their email and then they will receive Firebase-generated link to change their password.

![Login Form](https://github.com/Djimi02/HighSpots-Project/blob/main/images/Login.jpg)

Note: For user authentication I have used Firebase's API

## Homepage
Homepage reports all the user information such as: current email and nickname, the number of spots visitted and rated by the user, the number of visitors to any of the user's spots, and a list of the user's spots as well as per spot statics.

![Homepage](https://github.com/Djimi02/HighSpots-Project/blob/main/images/Homepage.jpg)

The button "UPDATE MY DATA" fetches the most recent data from the database.

At the bottom the navigation bar can be seen.

## Settings page
On the settings page, one can do several things: change nickname, email and password, logout from the account and delete the account from the database, as well as send email to the developer via the contact form.

![Settings page](https://github.com/Djimi02/HighSpots-Project/blob/main/images/Settings.jpg)

## Map
The map is standard Android Google map. It visializes the user's current location, and the spots existing within 30km away from the user. Green pin means that the spot is created by the currently logged in user, while red pin means that the spot is created by other user.

![Map](https://github.com/Djimi02/HighSpots-Project/blob/main/images/OpenMap.jpg)

The application allows filtering spots based on features and distance:

![Menu](https://github.com/Djimi02/HighSpots-Project/blob/main/images/Menu.jpg)

## Spot creation
There is a "+" button in the bottom right corner of the map page, which allows users to add their current location as a spot. Spot creation requires the user to give it initial rating, mark features existing on the real world location such as lake or gazebo, and upload a photo, as can be seen in the 2 pictures below. Clicking on "ADD IMAGE" opens the user's phone camera and wait for them to take a photo.

![Spot creation form](https://github.com/Djimi02/HighSpots-Project/blob/main/images/CreateSpot1.jpg)

![Spot creation form](https://github.com/Djimi02/HighSpots-Project/blob/main/images/CreateSpot2.jpg)

## Spot viewing
While searching for cool spots nearby, the user can click on any of the pins on the map and a spot overview pop-up window will appear, as can be seen in the picture below. It contains information such as image of the location, average raiting + the number of raitings, as well as a list of features. Once the user's location is less than 150m away from the spot location, the buttons "VISIT" and "RATE" become accessible. After "visiting" and raiting the spot, the user info as well as the spot info are updated in the database. Last but not least, a report form is accessible for each spot in the top right corner, which can be used to notify the developer of inappropriate photos or not existing places.

![Spot view](https://github.com/Djimi02/HighSpots-Project/blob/main/images/OpenSpot.jpg)

## Database
The NoSQL database Firebase is used. Firebase Authentication with email and password is used for user registration and authentication. Firebase Storage is used for storing the images from the spots uploaded by the users. Firebase Realtime Database is used for storing User and Spot objects. 

A photo from the database showing how User objects are stored is shown below. Each user has several attributes and is identified by unique ID. User object is usually associated with multiple Spot objects. The "data fan-out" technique is used to link User objects to Spot objects and the other way around. So for example, foundSpots, ratedSpots and visittedSpots are lists containing the unique IDs of the Spot objects.

![User object in the database](https://github.com/Djimi02/HighSpots-Project/blob/main/images/UserDatabase.png)

Follows a photo from the database showing how Spot objects are stored:

![Spot object in the database](https://github.com/Djimi02/HighSpots-Project/blob/main/images/SpotDatabase.png)

## Gmail service
javax.mail library is used to allow users to "Contact" the developer and "Report" spots. I created 2 gmails: sender account and receiver account. The required information + text are added as the email body and are sent by the sender to the receiver account.