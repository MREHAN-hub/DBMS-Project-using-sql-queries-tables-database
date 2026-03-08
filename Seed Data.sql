-- Seed Data
-- Blood Groups
INSERT INTO BloodGroup (BloodGroup) VALUES ('A+'), ('A-'), ('B+'), ('B-'), ('AB+'), ('AB-'), ('O+'), ('O-');

-- Donors
INSERT INTO Donor (FullName, Age, Gender, BloodGroup, Contact, LastDonationDate) VALUES
('Alice Johnson', 30, 'Female', 'A+', '1234567890', '2024-12-01'),
('Bob Smith', 45, 'Male', 'O-', '0987654321', '2025-02-15'),
('Carla Reyes', 28, 'Female', 'B+', '1122334455', NULL);

-- Blood Inventory
INSERT INTO BloodInventory (BloodGroup, Quantity) VALUES
('A+', 10),
('O-', 5),
('B+', 7);

-- Recipients
INSERT INTO Recipient (FullName, Age, Gender, BloodGroup, Hospital, RequestDate) VALUES
('Daniel Kim', 60, 'Male', 'A+', 'City Hospital', '2025-05-01'),
('Ella Rivera', 35, 'Female', 'O-', 'County Medical', '2025-05-02');

-- Staff
INSERT INTO Staff (Username, Password, Role) VALUES
('admin1', 'hashedpassword1', 'Admin'),
('nurse2', 'hashedpassword2', 'Nurse');

-- Blood Requests
INSERT INTO BloodRequest (RecipientID, BloodGroup, QuantityRequested, Status, RequestDate) VALUES
(1, 'A+', 2, 'Pending', '2025-05-01'),
(2, 'O-', 1, 'Fulfilled', '2025-05-02');
