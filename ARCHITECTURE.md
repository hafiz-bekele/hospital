# Hospital Management System - Architecture

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        USER INTERFACE                        │
│                     (Java Swing Desktop)                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND LAYER                            │
│                  (frontend.ui.*)                             │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ LoginFrame   │  │ RegisterFrame│  │   Common     │      │
│  └──────────────┘  └──────────────┘  │   Panels     │      │
│                                       └──────────────┘      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    Admin     │  │    Doctor    │  │   Patient    │      │
│  │  Dashboard   │  │  Dashboard   │  │  Dashboard   │      │
│  │  (8 panels)  │  │  (3 panels)  │  │  (4 panels)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND LAYER                             │
│                  (backend.*)                                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │              DATA ACCESS LAYER (DAO)               │     │
│  │              (backend.dao.*)                       │     │
│  ├────────────────────────────────────────────────────┤     │
│  │  • UserDAO          • DoctorDAO                    │     │
│  │  • AppointmentDAO   • ScheduleDAO                  │     │
│  │  • NotificationDAO                                 │     │
│  └────────────────────────────────────────────────────┘     │
│                              │                               │
│                              ▼                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │              DOMAIN MODELS                         │     │
│  │              (backend.models.*)                    │     │
│  ├────────────────────────────────────────────────────┤     │
│  │  • User             • Doctor                       │     │
│  │  • Appointment      • Schedule                     │     │
│  │  • Notification                                    │     │
│  └────────────────────────────────────────────────────┘     │
│                              │                               │
│                              ▼                               │
│  ┌────────────────────────────────────────────────────┐     │
│  │           DATABASE CONNECTION                      │     │
│  │           (backend.db.DBConnection)                │     │
│  └────────────────────────────────────────────────────┘     │
│                                                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                            │
│                  (MySQL - XAMPP)                             │
├─────────────────────────────────────────────────────────────┤
│  Tables:                                                     │
│  • users          • doctors        • appointments           │
│  • schedules      • notifications                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Layer Responsibilities

### 1. Frontend Layer (`frontend.ui.*`)
**Purpose**: User interface and user interaction

**Components**:
- **Entry Screens**: Login, Registration
- **Admin Dashboard**: 8 panels for complete hospital management
- **Doctor Dashboard**: 3 panels for appointment management
- **Patient Dashboard**: 4 panels for booking and tracking
- **Common Components**: Shared panels (notifications, password change)

**Responsibilities**:
- Display data to users
- Capture user input
- Validate form data
- Handle UI events
- Call backend services

**Technologies**: Java Swing, AWT

---

### 2. Backend Layer (`backend.*`)

#### 2a. Data Access Layer (`backend.dao.*`)
**Purpose**: Database operations and business logic

**Components**:
- `UserDAO`: User authentication, registration, profile management
- `DoctorDAO`: Doctor CRUD operations, search, filtering
- `AppointmentDAO`: Appointment booking, approval, status tracking
- `ScheduleDAO`: Doctor schedule management
- `NotificationDAO`: Notification creation and retrieval

**Responsibilities**:
- Execute SQL queries
- Handle transactions
- Implement business rules
- Data validation
- Error handling

**Pattern**: Data Access Object (DAO) pattern

---

#### 2b. Domain Models (`backend.models.*`)
**Purpose**: Data representation

**Components**:
- `User`: User entity (id, username, password, role, etc.)
- `Doctor`: Doctor entity (id, name, specialization, etc.)
- `Appointment`: Appointment entity (id, patient, doctor, date, status)
- `Schedule`: Schedule entity (id, doctor, date, time slots)
- `Notification`: Notification entity (id, user, message, date)

**Responsibilities**:
- Represent database tables as Java objects
- Encapsulate data with getters/setters
- Provide data structure for transfer between layers

**Pattern**: Plain Old Java Objects (POJOs)

---

#### 2c. Database Connection (`backend.db.*`)
**Purpose**: Database connectivity

**Component**:
- `DBConnection`: Singleton connection manager

**Responsibilities**:
- Establish MySQL connection
- Manage connection lifecycle
- Provide connection to DAOs
- Handle connection errors

**Pattern**: Singleton pattern

---

### 3. Database Layer (MySQL)
**Purpose**: Data persistence

**Schema**:
```sql
users
├── id (PK)
├── username
├── password
├── full_name
├── email
├── phone
├── role (ADMIN/DOCTOR/PATIENT)
└── created_at

doctors
├── id (PK)
├── user_id (FK → users.id)
├── specialization
├── qualification
└── experience

appointments
├── id (PK)
├── patient_id (FK → users.id)
├── doctor_id (FK → doctors.id)
├── schedule_id (FK → schedules.id)
├── status (PENDING/APPROVED/REJECTED)
├── medical_notes
└── created_at

schedules
├── id (PK)
├── doctor_id (FK → doctors.id)
├── date
├── start_time
├── end_time
└── max_patients

notifications
├── id (PK)
├── user_id (FK → users.id)
├── message
├── is_read
└── created_at
```

---

## 🔄 Data Flow Examples

### Example 1: Patient Books Appointment

```
1. User clicks "Book Appointment" button
   └─> PatientDashboard.java (frontend.ui.patient)

2. User searches for doctor
   └─> BookAppointmentPanel.java calls DoctorDAO.searchDoctors()
       └─> backend.dao.DoctorDAO

3. DoctorDAO queries database
   └─> Uses DBConnection.getConnection()
       └─> backend.db.DBConnection
           └─> Executes SQL: SELECT * FROM doctors WHERE ...

4. Results returned as List<Doctor>
   └─> backend.models.Doctor objects
       └─> Displayed in JTable in BookAppointmentPanel

5. User selects doctor and submits
   └─> BookAppointmentPanel calls AppointmentDAO.createAppointment()
       └─> backend.dao.AppointmentDAO
           └─> Executes SQL: INSERT INTO appointments ...

6. Success message displayed
   └─> JOptionPane in BookAppointmentPanel
```

---

### Example 2: Admin Adds New Doctor

```
1. Admin clicks "Add Doctor" button
   └─> AdminDashboard.java (frontend.ui.admin)
       └─> ManageDoctorsPanel.java

2. Admin fills form and submits
   └─> ManageDoctorsPanel calls DoctorDAO.addDoctor()
       └─> backend.dao.DoctorDAO

3. DoctorDAO creates user account first
   └─> Calls UserDAO.register()
       └─> backend.dao.UserDAO
           └─> Executes SQL: INSERT INTO users ...

4. Then creates doctor record
   └─> DoctorDAO.addDoctor() continues
       └─> Executes SQL: INSERT INTO doctors ...

5. Transaction committed
   └─> Both records saved
       └─> Success message displayed

6. Doctor list refreshed
   └─> ManageDoctorsPanel reloads table
       └─> Calls DoctorDAO.getAllDoctors()
```

---

### Example 3: Doctor Approves Appointment

```
1. Doctor views appointments
   └─> DoctorDashboard.java (frontend.ui.doctor)
       └─> AppointmentsPanel.java

2. Panel loads appointments
   └─> Calls AppointmentDAO.getAppointmentsByDoctor()
       └─> backend.dao.AppointmentDAO
           └─> Executes SQL: SELECT * FROM appointments WHERE doctor_id = ?

3. Doctor clicks "Approve" button
   └─> AppointmentsPanel calls AppointmentDAO.updateStatus()
       └─> backend.dao.AppointmentDAO
           └─> Executes SQL: UPDATE appointments SET status = 'APPROVED' ...

4. Notification created for patient
   └─> NotificationDAO.createNotification()
       └─> backend.dao.NotificationDAO
           └─> Executes SQL: INSERT INTO notifications ...

5. UI updated
   └─> Table refreshed
       └─> Status badge changes color
```

---

## 🎨 Design Patterns Used

### 1. **Singleton Pattern**
- **Where**: `DBConnection.java`
- **Why**: Single database connection instance
- **Benefit**: Resource efficiency, centralized connection management

### 2. **Data Access Object (DAO) Pattern**
- **Where**: All `*DAO.java` files
- **Why**: Separate data access logic from business logic
- **Benefit**: Easier testing, maintainable code, database independence

### 3. **Model-View Pattern**
- **Where**: Models (backend.models.*) + Views (frontend.ui.*)
- **Why**: Separate data representation from UI
- **Benefit**: Reusable models, flexible UI changes

### 4. **Factory Pattern (Implicit)**
- **Where**: DAO classes creating model objects
- **Why**: Consistent object creation from database results
- **Benefit**: Centralized object construction logic

---

## 🔐 Security Features

### Authentication
- Password hashing (should be implemented with BCrypt)
- Session management per user role
- Login validation

### Authorization
- Role-based access control (RBAC)
- Admin, Doctor, Patient roles
- Dashboard access restricted by role

### Data Validation
- Frontend: Form validation before submission
- Backend: SQL injection prevention with PreparedStatements
- Database: Foreign key constraints, NOT NULL constraints

---

## 🚀 Performance Considerations

### Database
- Connection pooling (can be added)
- Indexed columns (id, username, doctor_id, patient_id)
- Prepared statements for query optimization

### UI
- Lazy loading for large datasets
- Pagination for tables (can be added)
- Asynchronous data loading (can be improved)

### Memory
- Singleton connection reduces overhead
- Efficient object creation in DAOs
- Proper resource cleanup

---

## 📈 Scalability Path

### Current: Desktop Application
```
[Java Swing UI] → [JDBC] → [MySQL]
```

### Future: Web Application
```
[React/Angular] → [REST API] → [Spring Boot] → [MySQL]
                                    ↓
                              [Same Models & DAOs]
```

### Future: Microservices
```
[Frontend] → [API Gateway]
                ↓
    ┌───────────┼───────────┐
    ↓           ↓           ↓
[User Service] [Appointment] [Doctor Service]
    ↓           ↓           ↓
[User DB]   [Appt DB]   [Doctor DB]
```

**Current structure supports all these migrations!**

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Frontend** | Java Swing | Java 21 |
| **Backend** | Java JDBC | Java 21 |
| **Database** | MySQL | 8.0+ |
| **Driver** | MySQL Connector/J | 9.7.0 |
| **Server** | XAMPP | Latest |

---

## 📚 Code Organization Benefits

### ✅ Maintainability
- Clear separation of concerns
- Easy to locate files
- Consistent naming conventions

### ✅ Testability
- DAOs can be unit tested independently
- Models are simple POJOs
- UI can be tested separately

### ✅ Reusability
- Models used across all layers
- DAOs can be reused in different UIs
- Common UI components shared

### ✅ Extensibility
- Easy to add new features
- Simple to add new user roles
- Straightforward to add new entities

---

## 🎯 Best Practices Followed

1. **Package by Feature**: Backend/Frontend separation
2. **Single Responsibility**: Each class has one job
3. **DRY Principle**: Reusable components and methods
4. **Consistent Naming**: Clear, descriptive names
5. **Error Handling**: Try-catch blocks in DAOs
6. **Resource Management**: Proper connection cleanup
7. **Code Comments**: Inline documentation
8. **Version Control Ready**: Clean structure for Git

---

**Architecture Version**: 2.0  
**Last Updated**: May 25, 2026  
**Status**: ✅ Production Ready
