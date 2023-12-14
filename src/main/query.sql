CREATE TABLE Guest (
                       ID INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       status BOOLEAN DEFAULT TRUE,
                       email VARCHAR(255) UNIQUE,
                       phone VARCHAR(255) UNIQUE
);

CREATE TABLE Room (
                      ID INT PRIMARY KEY  AUTO_INCREMENT,
                      capacity INT NOT NULL,
                      price DECIMAL(10,2) NOT NULL
);

CREATE TABLE Booking (
                         ID INT PRIMARY KEY AUTO_INCREMENT,
                         guest_ID INT NOT NULL,
                         room_ID INT NOT NULL,
                         reservation_start_date DATETIME NOT NULL,
                         reservation_end_date DATETIME NOT NULL,
                         FOREIGN KEY (guest_ID) REFERENCES Guest(ID),
                         FOREIGN KEY (room_ID) REFERENCES Room(ID)
);

CREATE TABLE Bill (
                      ID INT PRIMARY KEY AUTO_INCREMENT,
                      booking_ID INT NOT NULL,
                      amount DECIMAL(10,2) NOT NULL,
                      issued_date DATE NOT NULL,
                      FOREIGN KEY (booking_ID) REFERENCES Booking(ID)
);

CREATE TABLE currently_reserved (
                                    ID INT PRIMARY KEY AUTO_INCREMENT,
                                    room_ID INT NOT NULL,
                                    reservation_start_date DATETIME NOT NULL,
                                    reservation_end_date DATETIME NOT NULL,
                                    FOREIGN KEY (room_ID) REFERENCES Room(ID)

);