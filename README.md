# Cricket Franchise Player Selection and Training Management System

A console-based Java application for managing cricket franchises, player registrations, training eligibility, and franchise-based player selection.

The project follows an MVC-based layered architecture with separate Controller, Service, Repository, Model, and Utility layers. The application uses JDBC with MySQL for persistent data storage.

## Features

### Player Management

- Register a new player
- Player login
- View player profile
- Update player details
- Search players
- Register with available franchises
- Register with multiple active franchises
- View registered franchises
- View selection status
- Register with another franchise after the selected franchise expires

### Franchise Management

- Default franchise support
- Register new franchises
- Franchise login
- Update franchise details
- Search franchises
- View active franchises
- View registered players
- View selected players
- Configure available player spots
- Configure role requirements
- Training-date management

### Player Selection

- Franchise-based player selection
- Role-based selection
- Batsman, bowler, and all-rounder requirements
- Player eligibility validation
- Experience-based selection
- Prevent an already-selected player from participating in another active franchise's selection
- Automatically remove a selected player from other franchise registrations

### Training Date Management

- A franchise has a training date.
- New franchises cannot be registered with a past training date.
- Existing franchises can become expired when their training date passes.
- Expired franchises are not available for new player registrations.
- Expired franchises cannot execute player selection.
- Expired selections are released.
- After the selected franchise's training date expires, the player can register with another active franchise.

### Authentication & Security

- Franchise username/password authentication
- PBKDF2 password hashing
- Random salt generation
- Password verification using the stored hash
- First-login password change for the default franchise
- Normal registered franchises are not forced to change their password on first login
- Admin authentication through an environment-configured PIN

### Database

- MySQL persistent storage
- JDBC connectivity
- Primary and foreign key constraints
- Composite key for player-franchise registration
- Unique constraints to prevent duplicate registrations
- Persistent franchise and player information
- Database clearing functionality

---

## Application Architecture

The project follows a layered MVC-based architecture:

```text
                    +----------------+
                    |   Controller   |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    |    Service     |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    |   Repository   |
                    +-------+--------+
                            |
                            v
                    +----------------+
                    |     MySQL      |
                    +----------------+
Model

Contains the application's data objects such as:

Player
Franchise
Selection-related models
Controller

Handles user interaction and console menus.

Service

Contains application and business logic such as:

Player registration
Franchise registration
Authentication
Player eligibility
Franchise expiry
Player selection
Selection lifecycle management
Repository

Handles database operations using JDBC.

Repositories are responsible for:

Insert operations
Update operations
Delete operations
Search operations
Database retrieval
Player-franchise registration management
Selection management
Utility

Contains reusable utilities such as:

Database connection
Password hashing and verification
Technologies Used
Technology	Purpose
Java	Application development
JDBC	Database connectivity
MySQL	Data persistence
Maven	Project and dependency management
IntelliJ IDEA	Development environment
MVC	Application architecture
PBKDF2	Password hashing
Database

The application uses MySQL.

Database:

cricket_franchise_db

The database is initialized using:

database.sql

The main entities include:

player
franchise
player_franchise
selection
Player-Franchise Relationship

A player can register with multiple active franchises.

For example:

Player A
   ├── CSK
   ├── MI
   └── SRH

The same player cannot be registered twice with the same franchise.

The player-franchise relationship is protected using a composite key:

(playerId, franchiseId)
Player Selection Lifecycle

The application follows this selection lifecycle:

Player Registration
        |
        v
Multiple Active Franchise Registrations
        |
        v
Franchise Runs Selection
        |
        v
Player Selected
        |
        +----------------------------+
        |                            |
        v                            v
Remove Other Franchise        Player Cannot Be Selected
Registrations                 By Another Active Franchise
        |
        v
Selected Franchise Training Date Expires
        |
        v
Selection Released
        |
        v
Player Becomes Available Again
        |
        v
Player Can Register With Another Active Franchise
Important Selection Rules

Once a player is selected by one franchise:

The player is removed from other franchise registrations.
The player cannot participate in another active franchise's selection.
The selected player remains associated with the selected franchise until its training date expires.

When the selected franchise's training date expires:

The selection is released.
The expired franchise registration is removed.
The player becomes available again.
The player can register with another active franchise.
Franchise Training Date Rules
New Franchise

A new franchise cannot be registered with a past training date.

Past date     → Rejected
Today's date  → Accepted
Future date   → Accepted
Existing Franchise

An existing franchise can become expired naturally when its training date passes.

Expired franchises:

Are not shown as active franchises.
Cannot accept new player registrations.
Cannot execute player selection.
Are excluded from active franchise views.
Player Eligibility

Players must satisfy the application's eligibility requirements before being considered for selection.

The selection process considers player information such as:

Role
Experience
Runs
Wickets

Role requirements are configured for each franchise.

Example:

Available Spots: 5
Role Count: 2-2-1

This represents:

Batsman      : 2
Bowler       : 2
All-Rounder  : 1

The selection process respects the configured role limits.

Authentication
Default Franchise

The application supports a default franchise account for demonstration and initial setup purposes.

The default franchise has a first-login password-change flow:

Default Franchise Login
        |
        v
First Login Detected
        |
        v
Password Change Required
        |
        v
New Password Hashed
        |
        v
Normal Login

The first-login status is stored in the database.

Normal Registered Franchise

A newly registered franchise follows the normal login flow.

Normal registered franchises are not forced to change their password on first login.

Password Security

Passwords are hashed before being stored for newly created accounts.

The application uses:

PBKDF2WithHmacSHA256

with:

Random salt
65,536 iterations
256-bit derived key
Base64 encoding for storage

The stored password contains the information required to verify the password later.

Password verification uses the stored salt and iteration count to generate the password hash again and compare it with the stored value.

Admin Configuration

The admin PIN should be supplied through an environment variable rather than hard-coded into the source code.

Example environment variable:

CRICKET_ADMIN_PIN

Set the variable before running the application.

Do not commit the real admin PIN to GitHub.

Project Structure
Cricket-Franchise-Player-Selection-and-Training-Management-System/
│
├── src/
│   └── main/
│       └── java/
│           ├── controller/
│           ├── model/
│           ├── repository/
│           ├── service/
│           ├── util/
│           └── view/
│
├── database.sql
├── pom.xml
├── README.md
└── .gitignore
Setup
1. Clone the Repository
git clone <repository-url>
2. Open the Project

Open the project using IntelliJ IDEA or another Java IDE that supports Maven.

3. Configure MySQL

Create the database:

CREATE DATABASE cricket_franchise_db;

Run the SQL commands from:

database.sql

using MySQL Workbench or the MySQL command line.

4. Configure Database Connection

Configure the MySQL connection details required by the application.

Make sure the MySQL server is running before starting the application.

5. Configure Admin PIN

Set the following environment variable:

CRICKET_ADMIN_PIN
6. Build the Project

Using Maven:

mvn clean compile

Alternatively, use the Maven Lifecycle window in IntelliJ IDEA.

7. Run the Application

Run the application's Main class.

Example Application Flow
Start Application
       |
       v
Login / Register
       |
       +-------------------+
       |                   |
       v                   v
    Player             Franchise
       |                   |
       v                   v
Register to            Manage Franchise
Franchises             & Selection
       |                   |
       +---------+---------+
                 |
                 v
          Player Selection
                 |
                 v
          Selection Result
Key Business Rules
A player can register with multiple active franchises.
A player cannot register twice with the same franchise.
A selected player cannot participate in another active franchise's selection.
When a player is selected, registrations with other franchises are removed.
A franchise with an expired training date cannot run selection.
Expired franchises are not available for new player registration.
When a selected franchise expires, the player's selection is released.
After the selected franchise expires, the player can register with another active franchise.
New franchises cannot be created with a past training date.
Role requirements must match the franchise's available player spots.
The default franchise uses the first-login password-change flow.
Normal registered franchises are not forced to change their password on first login.
Passwords are hashed before being stored.
Database constraints provide additional protection against duplicate registrations.
Franchise and player data persist in the database until the database clearing functionality is used.
Future Enhancements

Possible future improvements include:

Web-based user interface
REST API using Spring Boot
JWT-based authentication
Email notifications
Player statistics dashboard
Franchise performance reports
Automated testing with JUnit
Docker-based MySQL setup
Cloud deployment

Author
Kathiravan G