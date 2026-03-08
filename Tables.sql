CREATE TABLE BloodGroup (
    BloodGroup NVARCHAR(5) PRIMARY KEY
);

-- Donors Table
CREATE TABLE Donor (
    DonorID INT PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Age INT NOT NULL CHECK (Age >= 18 AND Age <= 65),
    Gender NVARCHAR(10) NOT NULL CHECK (Gender IN ('Male', 'Female', 'Other')),
    BloodGroup NVARCHAR(5) NOT NULL FOREIGN KEY REFERENCES BloodGroup(BloodGroup),
    Contact NVARCHAR(50) NOT NULL,
    LastDonationDate DATE
);

-- Blood Inventory
CREATE TABLE BloodInventory (
    BloodGroup NVARCHAR(5) PRIMARY KEY FOREIGN KEY REFERENCES BloodGroup(BloodGroup),
    Quantity INT NOT NULL CHECK (Quantity >= 0)
);

-- Recipients Table
CREATE TABLE Recipient (
    RecipientID INT PRIMARY KEY IDENTITY(1,1),
    FullName NVARCHAR(100) NOT NULL,
    Age INT NOT NULL CHECK (Age > 0),
    Gender NVARCHAR(10) NOT NULL CHECK (Gender IN ('Male', 'Female', 'Other')),
    BloodGroup NVARCHAR(5) NOT NULL FOREIGN KEY REFERENCES BloodGroup(BloodGroup),
    Hospital NVARCHAR(100) NOT NULL,
    RequestDate DATE NOT NULL
);

-- Admin/Staff Table
CREATE TABLE Staff (
    StaffID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(50) NOT NULL UNIQUE,
    Password NVARCHAR(100) NOT NULL, -- Store hashed passwords only
    Role NVARCHAR(20) NOT NULL
);

-- Blood Request Table
CREATE TABLE BloodRequest (
    RequestID INT PRIMARY KEY IDENTITY(1,1),
    RecipientID INT NOT NULL FOREIGN KEY REFERENCES Recipient(RecipientID),
    BloodGroup NVARCHAR(5) NOT NULL FOREIGN KEY REFERENCES BloodGroup(BloodGroup),
    QuantityRequested INT NOT NULL CHECK (QuantityRequested > 0),
    Status NVARCHAR(20) NOT NULL CHECK (Status IN ('Pending', 'Fulfilled', 'Rejected')),
    RequestDate DATE NOT NULL
);
