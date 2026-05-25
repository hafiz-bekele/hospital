# Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Start MySQL
```bash
# Open XAMPP Control Panel
# Click "Start" for MySQL
# Verify it's running (green indicator)
```

### Step 2: Compile
```bash
compile.bat
```

### Step 3: Run
```bash
run.bat
```

**Default Login**: `admin` / `admin123`

---

## 📁 Project Structure at a Glance

```
src/
├── Main.java              # Start here
├── backend/               # All backend logic
│   ├── db/               # Database connection
│   ├── models/           # Data models (5 files)
│   └── dao/              # Database operations (5 files)
└── frontend/             # All UI code
    └── ui/               # Swing components
        ├── admin/        # Admin screens (8 files)
        ├── doctor/       # Doctor screens (3 files)
        ├── patient/      # Patient screens (4 files)
        └── common/       # Shared components (2 files)
```

---

## 🔧 Common Tasks

### Add a New Feature

#### 1. Add Database Table
```sql
-- Edit hospital_db.sql
CREATE TABLE new_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ...
);
```

#### 2. Create Model
```java
// src/backend/models/NewModel.java
package backend.models;

public class NewModel {
    private int id;
    // getters and setters
}
```

#### 3. Create DAO
```java
// src/backend/dao/NewModelDAO.java
package backend.dao;

import backend.db.DBConnection;
import backend.models.NewModel;

public class NewModelDAO {
    public List<NewModel> getAll() {
        // SQL queries here
    }
}
```

#### 4. Create UI Panel
```java
// src/frontend/ui/admin/NewPanel.java
package frontend.ui.admin;

import backend.dao.NewModelDAO;
import backend.models.NewModel;

public class NewPanel extends JPanel {
    private NewModelDAO dao = new NewModelDAO();
    // UI code here
}
```

#### 5. Recompile
```bash
compile.bat
```

---

### Modify Existing Feature

#### 1. Find the Right File
- **UI Change**: Look in `frontend/ui/`
- **Logic Change**: Look in `backend/dao/`
- **Data Structure**: Look in `backend/models/`
- **Database**: Look in `backend/db/`

#### 2. Make Changes
```java
// Example: Add a field to User model
// File: src/backend/models/User.java

private String newField;

public String getNewField() { return newField; }
public void setNewField(String newField) { this.newField = newField; }
```

#### 3. Update Database
```sql
ALTER TABLE users ADD COLUMN new_field VARCHAR(255);
```

#### 4. Update DAO
```java
// File: src/backend/dao/UserDAO.java
// Update SQL queries to include new field
```

#### 5. Update UI
```java
// File: src/frontend/ui/patient/PatientProfilePanel.java
// Add new field to form
```

#### 6. Recompile and Test
```bash
compile.bat
run.bat
```

---

## 🐛 Debugging Tips

### Application Won't Start
```bash
# Check MySQL is running
# Open XAMPP → Start MySQL

# Check database exists
# Open phpMyAdmin → Look for hospital_db

# Check console for errors
java -cp "out;mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar" Main
```

### Compilation Errors
```bash
# Verify Java version
java -version  # Should be Java 21

# Check for typos in package names
# Backend: backend.db, backend.models, backend.dao
# Frontend: frontend.ui, frontend.ui.admin, etc.

# Verify imports
import backend.dao.*;
import backend.models.*;
import frontend.ui.*;
```

### Database Connection Failed
```java
// Check credentials in:
// src/backend/db/DBConnection.java

private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
private static final String USER = "root";
private static final String PASSWORD = ""; // Empty for XAMPP
```

### UI Not Updating
```java
// After data changes, refresh the UI:
tableModel.fireTableDataChanged();
// or
panel.revalidate();
panel.repaint();
```

---

## 📝 Code Snippets

### Query Database
```java
// In any DAO class
Connection conn = DBConnection.getConnection();
String sql = "SELECT * FROM table_name WHERE id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, id);
ResultSet rs = stmt.executeQuery();

while (rs.next()) {
    // Process results
}
```

### Create UI Button
```java
// In any UI class
JButton btn = LoginFrame.roundButton("Click Me", LoginFrame.PRIMARY, Color.WHITE);
btn.addActionListener(e -> {
    // Button action
});
```

### Show Message Dialog
```java
// Success message
JOptionPane.showMessageDialog(this, "Success!", "Info", JOptionPane.INFORMATION_MESSAGE);

// Error message
JOptionPane.showMessageDialog(this, "Error!", "Error", JOptionPane.ERROR_MESSAGE);

// Confirmation dialog
int result = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
if (result == JOptionPane.YES_OPTION) {
    // User clicked Yes
}
```

### Create Table
```java
String[] columns = {"ID", "Name", "Email"};
DefaultTableModel model = new DefaultTableModel(columns, 0);
JTable table = new JTable(model);

// Add row
model.addRow(new Object[]{1, "John", "john@email.com"});

// Clear table
model.setRowCount(0);
```

---

## 🎨 UI Color Scheme

```java
// Use these colors from LoginFrame for consistency:
LoginFrame.PRIMARY      // Blue - Primary buttons
LoginFrame.SUCCESS      // Green - Success states
LoginFrame.BG           // Light gray - Background
LoginFrame.CARD         // White - Cards/panels
LoginFrame.TEXT         // Dark - Text color
LoginFrame.MUTED        // Gray - Secondary text
LoginFrame.BORDER_CLR   // Light gray - Borders
```

---

## 📦 File Naming Conventions

### Models
- Singular noun: `User.java`, `Doctor.java`, `Appointment.java`
- Location: `backend/models/`

### DAOs
- Model name + DAO: `UserDAO.java`, `DoctorDAO.java`
- Location: `backend/dao/`

### UI Panels
- Descriptive name + Panel: `BookAppointmentPanel.java`
- Location: `frontend/ui/{role}/`

### Dashboards
- Role + Dashboard: `AdminDashboard.java`, `DoctorDashboard.java`
- Location: `frontend/ui/{role}/`

---

## 🔑 Key Files Reference

| File | Purpose | When to Edit |
|------|---------|--------------|
| `Main.java` | Application entry point | Rarely |
| `DBConnection.java` | Database config | Change DB credentials |
| `LoginFrame.java` | Login UI + shared styles | Modify login or add UI helpers |
| `*DAO.java` | Database operations | Add/modify queries |
| `*Model.java` | Data structure | Add/modify fields |
| `*Dashboard.java` | Main screens | Add new tabs/panels |
| `*Panel.java` | Feature screens | Modify specific features |
| `compile.bat` | Build script | Add new files |
| `hospital_db.sql` | Database schema | Modify database structure |

---

## 🧪 Testing Checklist

### Before Committing Code
- [ ] Code compiles without errors
- [ ] Application launches successfully
- [ ] Login works (admin/admin123)
- [ ] Test the feature you modified
- [ ] Check console for errors
- [ ] Database connection works
- [ ] UI displays correctly
- [ ] No null pointer exceptions

### Full Feature Test
- [ ] Admin can add doctors
- [ ] Admin can manage schedules
- [ ] Patients can register
- [ ] Patients can search doctors
- [ ] Patients can book appointments
- [ ] Doctors can view appointments
- [ ] Doctors can approve/reject
- [ ] Notifications work
- [ ] Password change works

---

## 📚 Learning Resources

### Java Swing
- [Oracle Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- Focus on: JFrame, JPanel, JTable, JButton, Layout Managers

### JDBC
- [Oracle JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- Focus on: Connection, PreparedStatement, ResultSet

### MySQL
- [MySQL Documentation](https://dev.mysql.com/doc/)
- Focus on: SELECT, INSERT, UPDATE, DELETE, JOIN

### Design Patterns
- DAO Pattern
- Singleton Pattern
- MVC Pattern

---

## 💡 Pro Tips

1. **Always use PreparedStatement** - Prevents SQL injection
2. **Close resources** - Use try-with-resources or finally blocks
3. **Validate input** - Both frontend and backend
4. **Use meaningful names** - Code should be self-documenting
5. **Keep methods small** - One method, one responsibility
6. **Comment complex logic** - Help future you
7. **Test incrementally** - Don't write too much before testing
8. **Use version control** - Commit often with clear messages

---

## 🆘 Getting Help

### Error Messages
1. Read the full error message
2. Note the file name and line number
3. Check that line in the code
4. Google the error if unclear

### Common Issues
- **ClassNotFoundException**: Missing MySQL connector JAR
- **SQLException**: Check database connection and SQL syntax
- **NullPointerException**: Check if object is null before using
- **NumberFormatException**: Validate numeric input

### Debug Process
1. Add `System.out.println()` statements
2. Check variable values
3. Verify method is being called
4. Check database has correct data
5. Use IDE debugger if available

---

**Happy Coding! 🚀**

For detailed architecture, see `ARCHITECTURE.md`  
For full documentation, see `README.md`  
For restructuring details, see `RESTRUCTURING_SUMMARY.md`
