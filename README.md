# Advanced Cricket Franchise Player Selection and Training Management System Using JDBC

## Project Description

A console-based Cricket Franchise Player Selection and Training Management System developed using Java, JDBC and MySQL.

The project follows MVC architecture with separate Controller, Service, Repository, Model and View layers.

## Technologies Used

- Java
- JDBC
- MySQL
- Maven
- MVC Architecture
- IntelliJ IDEA

## Features

### Player

- Player Registration
- Player Login
- View Player Profile
- Update Player Profile
- Register to Another Franchise
- View Registered Franchises
- View Selection Status
- View Training Details

### Franchise

- Franchise Registration
- Franchise Login
- View Franchise Profile
- View Registered Players
- Run Player Selection
- View Selected Players
- Training Management
- Update Franchise Details

### Admin

- Admin PIN protected database management
- Clear Player Data
- Clear Franchise Data
- Clear All Data
- Add Default Franchise
- View Registered Players Franchise-wise
- View Registered Franchises
- View Selected Players Franchise-wise

## Database

The project uses MySQL to permanently store:

- Player details
- Franchise details
- Player-Franchise registrations
- Selection details
- Selection status

## Project Architecture

The project follows a layered MVC architecture:

Controller → Service → Repository → MySQL Database

### Controller

Handles user input and application flow.

The project contains:

- LoginController
- PlayerController
- FranchiseController

### Service

Contains the application and business logic.

The project contains:

- PlayerService
- FranchiseService
- SelectionService

### Repository

Handles database operations using JDBC.

The repository layer contains the repository interfaces and their JDBC implementations.

### Model

Contains the main application objects:

- Player
- Franchise

### View

Handles displaying information to the user.

The project contains:

- LoginView
- PlayerView
- FranchiseView

## Project Structure

```text
src
└── main
    └── java
        ├── controller
        │   ├── LoginController.java
        │   ├── PlayerController.java
        │   └── FranchiseController.java
        │
        ├── main
        │   └── Main.java
        │
        ├── model
        │   ├── Player.java
        │   └── Franchise.java
        │
        ├── repository
        │   ├── DBConnection.java
        │   ├── PlayerRepository.java
        │   ├── PlayerRepositoryImpl.java
        │   ├── FranchiseRepository.java
        │   ├── FranchiseRepositoryImpl.java
        │   ├── SelectionRepository.java
        │   └── SelectionRepositoryImpl.java
        │
        ├── service
        │   ├── PlayerService.java
        │   ├── FranchiseService.java
        │   └── SelectionService.java
        │
        └── view
            ├── LoginView.java
            ├── PlayerView.java
            └── FranchiseView.java