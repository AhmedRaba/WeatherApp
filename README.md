# Weather App

A simple Weather App built with Kotlin and Jetpack Compose, implementing **MVVM** architecture, **Clean Architecture**, and the **Repository Pattern**. The app fetches weather data from an API and displays it in a user-friendly interface. It uses **SharedPreferences** for caching and employs threading for background tasks.

## Features
- Display current weather data
- 5-day weather forecast
- No third-party libraries
- Caching weather data using **SharedPreferences**
- Implements **MVVM** (Model-View-ViewModel) architecture
- Follows **Clean Architecture** principles
- Uses **Repository Pattern** for data management
- Threads for background tasks to ensure smooth UI performance

## Architecture

### MVVM (Model-View-ViewModel)
- **Model**: Represents the data layer (WeatherData, WeatherDto, etc.).
- **View**: UI components (Jetpack Compose).
- **ViewModel**: Holds the UI logic and interacts with UseCases and the Repository.

### Clean Architecture
- **Presentation Layer**: Contains UI logic, implemented with Jetpack Compose and ViewModel.
- **Domain Layer**: Contains business logic and use cases (e.g., fetching weather).
- **Data Layer**: Handles data fetching from an API and caching using SharedPreferences.

### Repository Pattern
- Manages data from multiple sources (API and cache).
- Provides a clean interface for data access.

## Libraries and Tools Used
- **Jetpack Compose**: UI framework for building the interface.
- **Kotlin**: Programming language.
- **SharedPreferences**: Used for caching weather data.
- **HttpURLConnection**: Used for making HTTP requests and fetching weather data from the API (no Retrofit).
- **Threading**: Handles background tasks to keep the UI responsive.
