# Contributing to Hospital Management System

Welcome! We're excited that you want to contribute. This guide will help you understand the codebase and make your first contribution.

## 📚 Code Philosophy

Our code follows these principles:
1. **Human-Readable**: Code should read like a story, not a puzzle
2. **Well-Commented**: Every class and complex method has clear explanations
3. **Consistent Style**: Same patterns throughout the codebase
4. **No Feature Breaks**: New code must not break existing features

---

## 🏗️ Project Structure

```
src/
├── Main.java                    # Application entry point
├── backend/                     # All server-side logic
│   ├── db/                      # Database connection
│   │   └── DBConnection.java    # Singleton connection manager
│   ├── models/                  # Data models (POJOs)
│   │   ├── User.java           # User entity
│   │   ├── Doctor.java         # Doctor entity
│   │   ├── Appointment.java    # Appointment entity
│   │   ├── Schedule.java       # Schedule entity
│   │   └── Notification.java   # Notification entity
│   └── dao/                     # Data Access Objects
│       ├── UserDAO.java        # User database operations
│       ├── DoctorDAO.java      # Doctor database operations
│       ├── AppointmentDAO.java # Appointment database operations
│       ├── ScheduleDAO.java    # Schedule database operations
│       └── NotificationDAO.java# Notification database operations
└── frontend/                    # All UI code
    └── ui/                      # Swing components
        ├── LoginFrame.java      # Login screen
        ├── RegisterFrame.java   # Registration screen
        ├── admin/               # Admin dashboard (8 panels)
        ├── doctor/              # Doctor dashboard (3 panels)
        ├── patient/             # Patient dashboard (4 panels)
        └── common/              # Shared components (2 panels)
```

---

## 💡 Understanding the Code

### Models (backend/models/)
**What they are**: Simple Java objects that represent database tables

**Example**:
```java
// User.java represents a row in the users table
User user = new User();
user.setFullName("John Doe");
user.setEmail("john@example.com");
```

**Key Points**:
- Each model has private fields with getters/setters
- Models are "dumb" - they just hold data, no logic
- toString() methods for easy display

### DAOs (backend/dao/)
**What they are**: Classes that handle database operations

**Example**:
```java
// UserDAO handles all user-related database queries
UserDAO userDAO = new UserDAO();
User user = userDAO.login("admin", "admin123");
```

**Key Points**:
- One DAO per model (UserDAO for User, DoctorDAO for Doctor, etc.)
- All SQL queries are here
- Uses PreparedStatement to prevent SQL injection
- Returns models or lists of models

### UI Components (frontend/ui/)
**What they are**: Swing panels and frames that users interact with

**Example**:
```java
// LoginFrame shows the login screen
LoginFrame loginWindow = new LoginFrame();
loginWindow.setVisible(true);
```

**Key Points**:
- Each screen is a separate class
- Dashboards contain multiple panels (tabs)
- UI calls DAO methods to get/save data
- Modern styling with consistent colors

---

## 🎨 Code Style Guide

### 1. Comments

**Class-Level Comments**:
```java
/**
 * Short description of what this class does
 * 
 * Longer explanation with examples if needed.
 * Explain the "why" not just the "what".
 * 
 * @author Hospital Management Team
 */
public class MyClass {
    // ...
}
```

**Method Comments**:
```java
/**
 * Short description of what this method does
 * 
 * Longer explanation if the method is complex.
 * 
 * @param paramName Description of parameter
 * @return Description of return value
 */
public ReturnType myMethod(ParamType paramName) {
    // ...
}
```

**Inline Comments**:
```java
// Explain WHY you're doing something, not WHAT you're doing
// Good: Check if user exists to prevent duplicate accounts
// Bad:  Check if user exists

if (userDAO.usernameExists(username)) {
    // Username is taken
}
```

### 2. Naming Conventions

**Classes**: PascalCase
```java
public class UserDAO { }
public class LoginFrame { }
```

**Methods**: camelCase, verb-based
```java
public void saveUser() { }
public User getUser() { }
public boolean isValid() { }
```

**Variables**: camelCase, descriptive
```java
String fullName;        // Good
String fn;              // Bad - too short
int patientCount;       // Good
int x;                  // Bad - not descriptive
```

**Constants**: UPPER_SNAKE_CASE
```java
private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/hospital_db";
private static final int MAX_PATIENTS = 10;
```

### 3. Code Organization

**Order within a class**:
1. Constants
2. Instance variables
3. Constructors
4. Public methods
5. Private helper methods

**Example**:
```java
public class MyClass {
    // 1. Constants
    private static final String DEFAULT_NAME = "Unknown";
    
    // 2. Instance variables
    private int id;
    private String name;
    
    // 3. Constructors
    public MyClass() { }
    public MyClass(int id, String name) { }
    
    // 4. Public methods
    public void doSomething() { }
    public String getName() { }
    
    // 5. Private helper methods
    private void helperMethod() { }
}
```

---

## 🔧 How to Add a New Feature

### Example: Adding a "Department" Feature

#### Step 1: Create the Model
```java
// File: src/backend/models/Department.java
package backend.models;

/**
 * Department Model - Represents a hospital department
 * 
 * Examples: Cardiology, Pediatrics, Emergency, etc.
 * 
 * @author Your Name
 */
public class Department {
    private int id;
    private String name;
    private String description;
    
    // Constructor
    public Department() { }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() { return name; }
}
```

#### Step 2: Create the DAO
```java
// File: src/backend/dao/DepartmentDAO.java
package backend.dao;

import backend.db.DBConnection;
import backend.models.Department;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Department Data Access Object
 * 
 * Handles all database operations for departments.
 * 
 * @author Your Name
 */
public class DepartmentDAO {
    
    /**
     * Get all departments from database
     * 
     * @return List of all departments
     */
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY name";
        
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            
            while (rs.next()) {
                Department dept = new Department();
                dept.setId(rs.getInt("id"));
                dept.setName(rs.getString("name"));
                dept.setDescription(rs.getString("description"));
                list.add(dept);
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch departments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }
    
    /**
     * Add a new department
     * 
     * @param department The department to add
     * @return true if successful, false otherwise
     */
    public boolean addDepartment(Department department) {
        String sql = "INSERT INTO departments (name, description) VALUES (?, ?)";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, department.getName());
            ps.setString(2, department.getDescription());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Failed to add department: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
```

#### Step 3: Create the UI Panel
```java
// File: src/frontend/ui/admin/ManageDepartmentsPanel.java
package frontend.ui.admin;

import backend.dao.DepartmentDAO;
import backend.models.Department;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Manage Departments Panel
 * 
 * Allows admin to view and manage hospital departments.
 * 
 * @author Your Name
 */
public class ManageDepartmentsPanel extends JPanel {
    
    private DepartmentDAO departmentDAO;
    private JTable table;
    
    public ManageDepartmentsPanel() {
        this.departmentDAO = new DepartmentDAO();
        setLayout(new BorderLayout());
        buildUI();
        loadDepartments();
    }
    
    private void buildUI() {
        // Create table
        String[] columns = {"ID", "Name", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        
        // Add to scroll pane
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add Department");
        addBtn.addActionListener(e -> showAddDialog());
        buttonPanel.add(addBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadDepartments() {
        // Get table model
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);  // Clear existing rows
        
        // Load from database
        List<Department> departments = departmentDAO.getAllDepartments();
        
        // Add each department to table
        for (Department dept : departments) {
            model.addRow(new Object[]{
                dept.getId(),
                dept.getName(),
                dept.getDescription()
            });
        }
    }
    
    private void showAddDialog() {
        // Create input dialog
        JTextField nameField = new JTextField(20);
        JTextField descField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Add Department", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            // Create department object
            Department dept = new Department();
            dept.setName(nameField.getText());
            dept.setDescription(descField.getText());
            
            // Save to database
            if (departmentDAO.addDepartment(dept)) {
                JOptionPane.showMessageDialog(this, "Department added!");
                loadDepartments();  // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add department", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
```

#### Step 4: Update Database
```sql
-- Add this to hospital_db.sql
CREATE TABLE departments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Step 5: Add to compile.bat
```batch
src\backend\models\Department.java ^
src\backend\dao\DepartmentDAO.java ^
src\frontend\ui\admin\ManageDepartmentsPanel.java ^
```

#### Step 6: Add to Admin Dashboard
```java
// In AdminDashboard.java, add a new tab:
tabbedPane.addTab("Departments", new ManageDepartmentsPanel());
```

---

## ✅ Testing Checklist

Before submitting your contribution:

- [ ] Code compiles without errors
- [ ] All existing features still work
- [ ] New feature works as expected
- [ ] Code follows style guide
- [ ] Added comments explaining complex logic
- [ ] No hardcoded values (use constants)
- [ ] SQL uses PreparedStatement (no string concatenation)
- [ ] Error messages are user-friendly
- [ ] Tested with different inputs (including edge cases)

---

## 🐛 Common Mistakes to Avoid

### 1. SQL Injection
**Bad**:
```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

**Good**:
```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, username);
```

### 2. Not Closing Resources
**Bad**:
```java
Connection conn = DBConnection.getConnection();
Statement st = conn.createStatement();
// Forgot to close!
```

**Good**:
```java
try (Statement st = DBConnection.getConnection().createStatement()) {
    // Automatically closes when done
}
```

### 3. Swallowing Exceptions
**Bad**:
```java
try {
    // code
} catch (Exception e) {
    // Do nothing - error is hidden!
}
```

**Good**:
```java
try {
    // code
} catch (Exception e) {
    System.err.println("❌ Operation failed: " + e.getMessage());
    e.printStackTrace();
}
```

### 4. Magic Numbers
**Bad**:
```java
if (user.getRole().equals("ADMIN")) { }  // What if we typo "ADMIN"?
```

**Good**:
```java
public static final String ROLE_ADMIN = "ADMIN";
if (user.getRole().equals(ROLE_ADMIN)) { }
```

---

## 📝 Pull Request Process

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/department-management`
3. **Make your changes** following this guide
4. **Test thoroughly**
5. **Commit with clear messages**: `git commit -m "Add department management feature"`
6. **Push to your fork**: `git push origin feature/department-management`
7. **Create a Pull Request** with:
   - Clear title
   - Description of what you changed
   - Screenshots if UI changes
   - Testing steps

---

## 💬 Getting Help

- **Questions?** Open an issue with the "question" label
- **Bug found?** Open an issue with the "bug" label
- **Feature idea?** Open an issue with the "enhancement" label

---

## 🎓 Learning Resources

### Java Basics
- [Oracle Java Tutorials](https://docs.oracle.com/javase/tutorial/)
- [Java Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)

### Swing (UI)
- [Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
- Focus on: JFrame, JPanel, JTable, JButton, Layout Managers

### JDBC (Database)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- Focus on: Connection, PreparedStatement, ResultSet

### Design Patterns
- DAO Pattern (we use this!)
- Singleton Pattern (DBConnection uses this!)
- MVC Pattern (our structure follows this!)

---

## 🌟 Code of Conduct

- Be respectful and constructive
- Help others learn
- Write code you'd be proud to show
- Test before submitting
- Document your changes

---

**Happy Coding! 🚀**

Thank you for contributing to make healthcare management better!
