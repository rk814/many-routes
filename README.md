# Many Routes

## Overview
Many Routes is a **RESTful Spring** application designed for mountain and not only mountain enthusiasts. 
Main goal of application is to give inspiration for new trips by showcasing hidden or lesser-known destinations. 
It also aims to motivate users to not  stop on their adventure route through an engaging challenge-based system.

### Main functionality
The core functionality of the application follows these fife steps:

1. Choose a challenge.
2. Visit a place listed in the challenge.
3. Validate your position using the GPS in the phone app.
4. Repeat this points for all remaining places. 
5. Earn a certificate and/or medal after completing the challenge.

![main-diagram-black.png](readme-pic%2Fmain-diagram-black.png#gh-dark-mode-only)
![main-diagram-white.png](readme-pic%2Fmain-diagram-white.png#gh-light-mode-only)

### Other functionalities
- The app provides detailed information about challenges and individual peaks, such as: general information, geographic position, available
  routs (marked trails), rest points, options of communication, warnings, hiking difficulty levels and many more.
- Many details are visualized on an interactive map powered by **Mapbox**.
One of benefits of Mapbox is that it share own server for map elements. This let load complete map in single step.
Also, the integrated API offers user-friendly tools for editing and map customization.
Because of that, map is directly integrated with the frontend rather the backend.
- The app integrates current and forecasted weather data using **Free Weather API** via a WebClient.
In addition to weather, it provides astronomical details like sunrise times and moon phases.

[![mapbox-black.png](readme-pic%2Fmapbox-black.png#gh-dark-mode-only)](https://www.mapbox.com/)
[![mapbox-white.png](readme-pic%2Fmapbox-white.png#gh-light-mode-only)](https://www.mapbox.com/)
[![weatherapi_logo.webp](readme-pic%2Fweatherapi_logo.webp)](https://www.weatherapi.com/)

## Endpoints
The application provides standard CRUD endpoints, grouped into six categories:
1. Challenge - General information about challenges.
2. Summit - Additional details about specific mountain peaks or destinations.
3. User - User profile management.
4. UserChallenge - User progress in challenges.
5. Weather - Weather data from external API.
6. Authentication - Handles user sign-in and registration.

Full details of all endpoints can be explored via Swagger documentation: 

[![swagger.JPG](readme-pic%2Fswagger.JPG)](http://localhost:8080/swagger-ui/index.html#/)

## Security
The application uses Spring Security with **JWT tokens** for authentication. Tokens are encrypted using a 512-bit secret key.  
Security roles are simplified. Roles are not additive, each user is assigned a single role such as USER or ADMIN.

User passwords are hashed with bcrypt.

## Logging
Logging is implemented with **Logback**, configuration via xml for both: console output and log file storage.

## Tests
The application achieves 96% unit test coverage based on missed instructions and 94% coverage based on missed branches.  
All details about tests coverage can be found in **JaCoCo** report by running:

`mvn clean test jacoco:report`

![jacoco.JPG](readme-pic%2Fjacoco.JPG)

A few full integration tests (end-to-end) are implemented using WebClient. These tests primarily validate the complete endpoint process, including logging and database query construction, specifically addressing potential unneseccery query issues.

The tests use:
- **AssertJ** for assertions.
- **Mockito** for mocking and spying.
- **Instancio** for generating random test data.


## Database
The application is built with PostgreSQL and Hibernate JPA.

### Database Migrations
Database migrations are handled using Flyway.

### N+1 Problem
The N+1 query problem in `findAll` methods is resolved through Join Fetch queries.

## Planned Additions
- Frontend Development
Currently under development utilizing **React** and **Bootstrap**.
- Dockerization
Plans to containerize the backend, frontend, and database using Docker.
- Feature Expansion
Additional functionalities like pictures uploading, adding comments, and other user interactive features.

## Requirements
- Java 17 or higher
- Maven or IDE with integrated Maven

## Run
To run the application: 
Configure the database connection and key (secrets) using application-template-secrets.yml.  
Add sample data from one sql files:

`sql\crown_of_polish_mountains.sql`  
`sql\mounds_of_crocow.sql`

Build and run the application with the following commands:  

`mvn clean package`  

`mvn exec:java`

After starting application with fresh date base, admin user will be initialized with default credentials:  
`login: admin` `password: admin`