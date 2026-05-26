# Hospital Management System

A comprehensive desktop application for managing hospital operations including patient registration, doctor appointments, and administrative tasks.

## Features

### Patient
- Register a new account with validation
- View all available doctors with live search
- Book appointments with doctor schedules
- Track appointment status (Pending / Approved / Rejected)
- View medical notes added by doctors

### Doctor
- Login with credentials created by admin
- View all assigned appointments
- Approve or reject appointments
- Add/edit medical notes per appointment

### Admin
- Dashboard with live statistics (patients, doctors, appointments by status)
- Add / delete doctors (creates login credentials)
- Manage doctor schedules (date, time slots, max patients)
- Approve / reject / manage all appointments
- View all patients

## Technology Stack

- **Frontend**: Java Swing with modern UI design
- **Backend**: Java JDBC for database operations
- **Database**: MySQL (XAMPP)
- **Java Version**: Java 21

## Project Structure

```
HospitalSystem/
├── src/
│   ├── Main.java                    # Application entry point
│   ├── backend/                     # Backend logic
│   │   ├── db/                      # Database connection
│   │   │   └── DBConnection.java
│   │   ├── models/                  # Data models
│   │   │   ├── User.java
│   │   │   ├── Doctor.java
│   │   │   ├── Appointment.java
│   │   │   ├── Schedule.java
│   │   │   └── Notification.java
│   │   └── dao/                     # Data Access Objects
│   │       ├── UserDAO.java
│   │       ├── DoctorDAO.java
│   │       ├── AppointmentDAO.java
│   │       ├── ScheduleDAO.java
│   │       └── NotificationDAO.java
│   └── frontend/                    # Frontend UI
│       └── ui/
│           ├── LoginFrame.java
│           ├── RegisterFrame.java
│           ├── admin/               # Admin dashboard panels (8 files)
│           │   ├── AdminDashboard.java
│           │   ├── StatisticsPanel.java
│           │   ├── ManageDoctorsPanel.java
│           │   ├── ManageSchedulesPanel.java
│           │   ├── ManageAppointmentsPanel.java
│           │   ├── ViewPatientsPanel.java
│           │   ├── SearchAppointmentsPanel.java
│           │   └── ReportsPanel.java
│           ├── doctor/              # Doctor dashboard panels (3 files)
│           │   ├── DoctorDashboard.java
│           │   ├── AppointmentsPanel.java
│           │   └── DoctorProfilePanel.java
│           ├── patient/             # Patient dashboard panels (4 files)
│           │   ├── PatientDashboard.java
│           │   ├── BookAppointmentPanel.java
│           │   ├── MyAppointmentsPanel.java
│           │   └── PatientProfilePanel.java
│           └── common/              # Shared panels (2 files)
│               ├── NotificationsPanel.java
│               └── ChangePasswordPanel.java
├── out/                             # Compiled classes
├── mysql-connector-j-9.7.0/         # MySQL JDBC driver
├── hospital_db.sql                  # Database schema
├── compile.bat                      # Compilation script
└── run.bat                          # Run script
```

## Setup Instructions

### Prerequisites
1. **Java 21** - Ensure Java is installed and `JAVA_HOME` is set
2. **XAMPP** - For MySQL database server
3. **MySQL Connector** - Already included in the project

### Database Setup
1. Start XAMPP and ensure MySQL is running
2. Open phpMyAdmin (http://localhost/phpmyadmin)
3. Create a new database named `hospital_db`
4. Import the `hospital_db.sql` file

### Running the Application

#### Option 1: Using the run script
```bash
run.bat
```

#### Option 2: Manual compilation and run
```bash
# Compile
compile.bat

# Run
java -cp "out;mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" Main
```

## Default Login Credentials

- **Admin**: `admin` / `admin123`
- **Patients**: Register through the application
- **Doctors**: Created by admin through the dashboard

## Database Configuration

The database connection is configured in `src/backend/db/DBConnection.java`:

```java
URL:      jdbc:mysql://localhost:3306/hospital_db
USER:     root
PASSWORD: (empty - XAMPP default)
```

## UI Features

- Modern, clean interface with Segoe UI fonts
- Color-coded dashboards:
  - **Admin**: Purple accent
  - **Doctor**: Teal accent
  - **Patient**: Blue accent
- Live search and filtering for doctors
- Real-time form validation
- Password strength indicator
- Responsive table layouts

## Development Notes

- All UI components use Java Swing
- Database operations use JDBC with prepared statements
- Clean separation between frontend and backend
- Role-based access control (ADMIN, DOCTOR, PATIENT)
- Input validation on both client and server side

