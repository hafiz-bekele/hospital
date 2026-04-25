# Role-Based Hospital Appointment Booking System

## Tech Stack
- **Frontend**: Java Swing & AWT
- **Backend**: JDBC (MySQL)

## Prerequisites
- Java JDK 8+
- MySQL Server
- MySQL Connector/J JAR (`mysql-connector-j-x.x.x.jar`)

## Setup

### 1. Database
Run the SQL script in MySQL:
```sql
source hospital_db.sql
```
Or import it via MySQL Workbench / phpMyAdmin.

### 2. Configure DB Connection
Edit `src/db/DBConnection.java`:
```java
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Compile
Place `mysql-connector-j.jar` in `HospitalSystem/lib/`.

```bash
# From HospitalSystem/
javac -cp "lib/mysql-connector-j.jar" -d out src/**/*.java src/Main.java
```

Or compile all at once:
```bash
javac -cp "lib/mysql-connector-j.jar" -d out \
  src/db/DBConnection.java \
  src/models/*.java \
  src/dao/*.java \
  src/ui/LoginFrame.java \
  src/ui/RegisterFrame.java \
  src/ui/patient/*.java \
  src/ui/doctor/*.java \
  src/ui/admin/*.java \
  src/Main.java
```

### 4. Run
```bash
java -cp "out;lib/mysql-connector-j.jar" Main
```
On Linux/Mac use `:` instead of `;`.

## Default Login
| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin123 |

## Features

### Patient
- Register a new account
- View all available doctors
- Book appointments with a doctor's schedule
- Track appointment status (Pending / Approved / Rejected)
- View medical notes added by doctor

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
