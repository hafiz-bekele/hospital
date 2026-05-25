# Hospital Appointment System — Requirements Document

## 1. System Overview

The Hospital Appointment System is a desktop application built with Java Swing and MySQL. It allows patients to register and book appointments with doctors, doctors to manage their appointments and add medical notes, and administrators to manage the entire system including doctors, schedules, appointments, and reports.

---

## 2. Technology Stack

| Component       | Technology                        |
|-----------------|-----------------------------------|
| Language        | Java (JDK 8+)                     |
| UI Framework    | Java Swing                        |
| Database        | MySQL (localhost:3306)            |
| DB Connector    | MySQL Connector/J 9.7.0           |
| Database Name   | `hospital_db`                     |
| Build           | `compile.bat` / `run.bat`         |

---

## 3. User Roles

The system has three roles:

| Role    | How They Enter the System                              |
|---------|--------------------------------------------------------|
| ADMIN   | Pre-created in the database (`admin` / `admin123`)     |
| DOCTOR  | Created by the Admin only                              |
| PATIENT | Self-registers via the Register screen                 |

---

## 4. Authentication Requirements

### 4.1 Login
- All users (Admin, Doctor, Patient) log in from the same login screen
- Login requires: **username** and **password**
- On successful login, the system redirects to the appropriate dashboard based on role
- Invalid credentials show an error message
- Pressing Enter on the password field triggers login

### 4.2 Patient Registration
- Patients can self-register from the login screen
- Required fields: Full Name, Username, Password, Confirm Password
- Optional fields: Email, Phone
- Username must be unique — duplicate usernames are rejected
- Passwords must match
- After successful registration, the patient is redirected to the login screen

### 4.3 Password Change
- All users (Admin, Doctor, Patient) can change their password from their dashboard
- Requires: Current Password, New Password, Confirm New Password
- Current password must be verified before allowing the change
- New password must be at least 4 characters
- New password and confirmation must match

---

## 5. Admin Requirements

The Admin has full control over the system.

### 5.1 Dashboard / Statistics
- Displays live statistics cards:
  - Total Patients
  - Total Doctors
  - Total Appointments
  - Pending Appointments count
  - Approved Appointments count
  - Rejected Appointments count
- A "Refresh Statistics" button updates all counts

### 5.2 Manage Doctors
- View a list of all doctors with: ID, Full Name, Specialization, Qualification, Experience (years)
- **Add Doctor**: Admin fills in Full Name, Username, Password, Email, Phone, Specialization, Qualification, Experience — creates both a user account and a doctor profile in one transaction
- **Delete Doctor**: Removes the doctor and their user account from the system
- **Refresh**: Reloads the doctors list

### 5.3 Manage Schedules
- View all doctor schedules with: ID, Doctor Name, Date, Start Time, End Time, Max Patients
- **Add Schedule**: Admin selects a doctor and sets Date (YYYY-MM-DD), Start Time (HH:MM), End Time (HH:MM), Max Patients
- **Delete Schedule**: Removes a selected schedule
- **Refresh**: Reloads the schedules list

### 5.4 Manage Appointments
- View all appointments in the system with: ID, Patient, Doctor, Date, Time Slot, Reason, Status, Medical Notes
- Appointments are color-coded by status:
  - Green = APPROVED
  - Red = REJECTED
  - Yellow = PENDING
- Stats bar shows: Total, Pending, Approved, Rejected counts
- **Approve**: Sets selected appointment status to APPROVED and notifies the patient
- **Reject**: Sets selected appointment status to REJECTED and notifies the patient
- **Set Pending**: Resets selected appointment status back to PENDING
- **Refresh**: Reloads the appointments list

### 5.5 Search Appointments
- Search/filter appointments by any combination of:
  - Patient Name (partial match)
  - Doctor Name (partial match)
  - Status (ALL / PENDING / APPROVED / REJECTED / CANCELLED)
  - Date range: From date and To date (YYYY-MM-DD)
- Results displayed in a color-coded table
- Shows count of results found

### 5.6 Reports
- **Appointments per Doctor**: Table showing each doctor and their total appointment count, sorted by highest count
- **Monthly Appointment Trend**: Table showing appointment counts per month (last 12 months) plus a bar chart visualization

### 5.7 View Patients
- View all registered patients with: ID, Full Name, Username, Email, Phone
- Shows total patient count
- **View Appointments**: Shows all appointments for a selected patient in a popup table
- **Refresh**: Reloads the patient list

### 5.8 Notifications
- Admin receives in-app notifications
- Unread notifications are highlighted and marked with a dot (●)
- Notification tab badge shows unread count
- **Mark All Read**: Marks all notifications as read
- **Clear All**: Deletes all notifications for the admin
- **Refresh**: Reloads notifications

### 5.9 Change Password
- Admin can change their own password (see section 4.3)

---

## 6. Doctor Requirements

### 6.1 Appointments Panel
- View all appointments assigned to the logged-in doctor
- Filter appointments by:
  - Status (ALL / PENDING / APPROVED / REJECTED / CANCELLED)
  - Date range: From and To (YYYY-MM-DD)
- Appointments are color-coded:
  - Green = APPROVED
  - Red = REJECTED
  - Grey = CANCELLED
  - Yellow = PENDING
- Stats bar shows: Pending, Approved, Rejected counts
- **Approve**: Approves a selected appointment and sends a notification to the patient
- **Reject**: Rejects a selected appointment and sends a notification to the patient
- **Add Notes**: Doctor can write or update medical notes for a selected appointment; patient is notified
- **View Patient**: Shows full appointment details for the selected patient in a popup
- **Refresh**: Reloads the appointments list

### 6.2 Notifications
- Doctor receives in-app notifications
- Same behavior as Admin notifications (unread badge, mark read, clear all, refresh)

### 6.3 My Profile
- Displays the doctor's profile card with:
  - Avatar (initials "Dr")
  - Full Name, Username, Email, Phone
  - Specialization, Qualification, Experience (years)

### 6.4 Change Password
- Doctor can change their own password (see section 4.3)

---

## 7. Patient Requirements

### 7.1 Book Appointment
- View a table of all available doctors with: ID, Name, Specialization, Qualification, Experience
- Click a doctor row to auto-select them in the booking form
- Select a doctor from a dropdown
- Doctor info strip shows: Specialization, Qualification, Experience
- Select an available schedule (date and time slot) from a dropdown
- Enter a reason for the visit (optional text area)
- **Book Appointment**: Submits the booking — appointment is created with status PENDING
- Confirmation message shows: Doctor, Date, Time, and status note

### 7.2 My Appointments
- View all the patient's own appointments with: ID, Doctor, Date, Time Slot, Reason, Status, Medical Notes
- Appointments are color-coded by status (same as above)
- Stats bar shows: Total, Pending, Approved, Rejected, Cancelled counts
- **View Details**: Opens a popup showing full appointment details including status (color-coded) and medical notes
- **Cancel Appointment**: Cancels a selected appointment — only PENDING appointments can be cancelled; sends a notification to the patient
- **Refresh**: Reloads the appointments list

### 7.3 Notifications
- Patient receives in-app notifications for:
  - Appointment approved/rejected by doctor or admin
  - Medical notes added by doctor
  - Appointment cancellation confirmation
- Same behavior as other roles (unread badge, mark read, clear all, refresh)

### 7.4 My Profile
- Displays the patient's profile card with:
  - Avatar (initials from full name)
  - Full Name, Username, Email, Phone, Role

### 7.5 Change Password
- Patient can change their own password (see section 4.3)

---

## 8. Appointment Lifecycle

```
Patient books → PENDING
                   │
          ┌────────┴────────┐
          ▼                 ▼
       APPROVED          REJECTED
          │
          └── (Admin can reset to PENDING)

Patient can CANCEL only when status = PENDING → CANCELLED
```

---

## 9. Notification System

- Notifications are sent automatically when:
  - An appointment is approved (by doctor or admin) → patient notified
  - An appointment is rejected (by doctor or admin) → patient notified
  - Medical notes are added by a doctor → patient notified
  - A patient cancels their own appointment → patient notified
- Notifications are stored in the database with a read/unread flag
- Unread count is shown as a badge on the Notifications tab title
- Users can mark all as read or delete all notifications

---

## 10. Database Tables

| Table           | Description                                      |
|-----------------|--------------------------------------------------|
| `users`         | All users (Admin, Doctor, Patient) with role     |
| `doctors`       | Doctor profiles linked to users                  |
| `schedules`     | Doctor availability slots                        |
| `appointments`  | Appointment bookings with status and notes       |
| `notifications` | In-app messages per user with read/unread flag   |

---

## 11. Data Fields

### users
| Field       | Type    | Notes                          |
|-------------|---------|--------------------------------|
| id          | INT PK  | Auto increment                 |
| username    | VARCHAR | Unique                         |
| password    | VARCHAR | Plain text                     |
| role        | VARCHAR | ADMIN / DOCTOR / PATIENT       |
| full_name   | VARCHAR |                                |
| email       | VARCHAR | Optional                       |
| phone       | VARCHAR | Optional                       |

### doctors
| Field             | Type    | Notes                    |
|-------------------|---------|--------------------------|
| id                | INT PK  |                          |
| user_id           | INT FK  | References users.id      |
| specialization    | VARCHAR | Required                 |
| qualification     | VARCHAR | Optional                 |
| experience_years  | INT     |                          |

### schedules
| Field          | Type    | Notes                    |
|----------------|---------|--------------------------|
| id             | INT PK  |                          |
| doctor_id      | INT FK  | References doctors.id    |
| available_date | DATE    | Format: YYYY-MM-DD       |
| start_time     | TIME    | Format: HH:MM            |
| end_time       | TIME    | Format: HH:MM            |
| max_patients   | INT     |                          |

### appointments
| Field            | Type    | Notes                              |
|------------------|---------|------------------------------------|
| id               | INT PK  |                                    |
| patient_id       | INT FK  | References users.id                |
| doctor_id        | INT FK  | References doctors.id              |
| schedule_id      | INT FK  | References schedules.id            |
| appointment_date | DATE    |                                    |
| reason           | TEXT    | Optional                           |
| status           | VARCHAR | PENDING / APPROVED / REJECTED / CANCELLED |
| medical_notes    | TEXT    | Added by doctor                    |

### notifications
| Field      | Type    | Notes                    |
|------------|---------|--------------------------|
| id         | INT PK  |                          |
| user_id    | INT FK  | References users.id      |
| message    | TEXT    |                          |
| is_read    | TINYINT | 0 = unread, 1 = read     |
| created_at | DATETIME|                          |

---

## 12. Non-Functional Requirements

- **Platform**: Windows desktop application (Java Swing)
- **Database**: MySQL running on localhost port 3306
- **Concurrency**: Single-user desktop app, no concurrent session handling required
- **UI**: System look-and-feel applied on startup; all buttons have solid colors, bold text, and hand cursor for clear visibility
- **Validation**: All required fields validated before database operations; duplicate usernames rejected
- **Error Handling**: Database errors printed to console; user-facing error dialogs shown on failures
- **Transactions**: Doctor creation uses a database transaction (user + doctor records created atomically)
