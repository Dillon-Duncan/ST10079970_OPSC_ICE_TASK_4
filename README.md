# ST10079970

## Overview
This Android application allows users to search for nearby places such as restaurants, hospitals, gas stations, or any other types of locations based on their current location. The app leverages the **Google Places API** to fetch nearby places and displays the results in a user-friendly RecyclerView list.

## Features
- Fetch nearby places based on the user's current GPS location.
- Display a list of places, including name, address, rating, and distance from the current location.
- Allows users to search for different types of places using a search bar (e.g., restaurants, cafes, hospitals).
- View distances in kilometers from the current location to each place.

## How It Works
1. When the app is launched, it checks for location permissions.
2. The app fetches the user's last known location using the **FusedLocationProviderClient** from Google Play services.
3. Upon entering a search query and pressing the "Search" button, the app sends a request to the **Google Places API** to retrieve a list of nearby places.
4. The results are displayed in a scrollable RecyclerView with details such as:
   - **Place Name**
   - **Address**
   - **User Rating**
   - **Distance from the current location**

## Installation

### Prerequisites
- Android Studio installed.
- Google API Key for Places API (instructions below).

### Steps to Run the App

1. **Clone the Repository**:
bash git clone https://github.com/Dillon-Duncan/ST10079970_OPSC_ICE_TASK_4.git cd ST10079970_OPSC_ICE_TASK_4


2. Set Up Google Places API Key:
   Visit the Google Cloud Console.
   Create a new project.
   Enable the Google Places API and Google Maps SDK.
   Generate an API Key under APIs & Services > Credentials.
   Add API Key to the App:
   Open the file MainActivity.kt.
   Replace the placeholder API key with your actual API Key.

3. Run the App:
   Open Android Studio and import the project.
   Connect an Android device or use an emulator.
   Build and run the app from Android Studio.

## API Used
Google Places API
The app uses the Google Places API to fetch nearby places of interest based on the user's current location. The following API call is used:
https://maps.googleapis.com/maps/api/place/nearbysearch/json?location={latitude},{longitude}&radius=1500&type={place_type}&key={API_KEY}

Where:
- `location`: The latitude and longitude of the current location.
- `radius`: The search radius in meters.
- `type`: The type of place to search for (e.g., restaurant, hospital).
- `key`: Your Google Places API key.

For more information, visit the Google Places API documentation.

## Required API Key Permissions
Ensure that the following APIs are enabled for your project in Google Cloud Console:
- Places API
- Maps SDK for Android

## Permissions Required
The app requires the following Android permissions to function properly:
xml <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

This permission is used to access the user's precise location, allowing the app to fetch nearby places. It is critical for providing location-based search results.
Without this permission, the app will not be able to determine the user's current location and won't fetch any nearby places.

## Libraries Used
- Google Play Services: To fetch the user's current location.
- Volley: For network requests to the Google Places API.
- RecyclerView: To display the list of nearby places in a scrollable list.
- ConstraintLayout: For a responsive UI layout.

## Code Structure
- MainActivity.kt: Contains the main activity logic, including location fetching, API calls, and handling RecyclerView data.
- activity_main.xml: Defines the UI layout for the main screen, including the search bar, button, and RecyclerView.
- item_place.xml: Defines the layout for each item in the RecyclerView 

## Place Data Class
Place: A data class that represents a place object with properties like name, address, rating, and distance.
