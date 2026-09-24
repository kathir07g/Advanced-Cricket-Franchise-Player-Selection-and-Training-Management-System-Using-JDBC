CREATE DATABASE cricket_franchise_db;

USE cricket_franchise_db;

CREATE TABLE player(
                       playerId INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       dob DATE NOT NULL,
                       role VARCHAR(100) NOT NULL,
                       strength VARCHAR(100),
                       bestFigure VARCHAR(100),
                       experience INT,
                       totalRuns INT,
                       totalWickets INT
);

CREATE TABLE franchise(
                          franchiseId INT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL UNIQUE,
                          location VARCHAR(255) NOT NULL,
                          trainingDate DATE NOT NULL,
                          availableSpots INT NOT NULL,
                          roleCount VARCHAR(100) NOT NULL
);

CREATE TABLE player_franchise(
                                 playerId INT,
                                 franchiseId INT,

                                 PRIMARY KEY(playerId, franchiseId),

                                 FOREIGN KEY(playerId)
                                     REFERENCES player(playerId)
                                     ON DELETE CASCADE,

                                 FOREIGN KEY(franchiseId)
                                     REFERENCES franchise(franchiseId)
                                     ON DELETE CASCADE
);

CREATE TABLE selection(
                          selectionId INT PRIMARY KEY AUTO_INCREMENT,
                          playerId INT NOT NULL,
                          franchiseId INT NOT NULL,
                          status VARCHAR(100) NOT NULL,

                          FOREIGN KEY(playerId)
                              REFERENCES player(playerId)
                              ON DELETE CASCADE,

                          FOREIGN KEY(franchiseId)
                              REFERENCES franchise(franchiseId)
                              ON DELETE CASCADE,

                          UNIQUE(playerId)
);

ALTER TABLE player
    ADD COLUMN username VARCHAR(100) NOT NULL UNIQUE AFTER playerId,
ADD COLUMN password VARCHAR(255) NOT NULL AFTER username;

ALTER TABLE franchise
    ADD COLUMN username VARCHAR(100) NOT NULL UNIQUE AFTER franchiseId,
ADD COLUMN password VARCHAR(255) NOT NULL AFTER username;

CREATE TABLE selection_status(
                                 franchiseId INT PRIMARY KEY,
                                 selectionDone BOOLEAN NOT NULL DEFAULT FALSE,

                                 FOREIGN KEY(franchiseId)
                                     REFERENCES franchise(franchiseId)
                                     ON DELETE CASCADE
);