@echo off
echo Compiling Hospital System...

set JAR=mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar

if not exist out mkdir out

javac -cp "%JAR%" -d out -sourcepath src ^
  src\Main.java ^
  src\db\DBConnection.java ^
  src\models\User.java ^
  src\models\Doctor.java ^
  src\models\Appointment.java ^
  src\models\Schedule.java ^
  src\models\Notification.java ^
  src\dao\UserDAO.java ^
  src\dao\DoctorDAO.java ^
  src\dao\ScheduleDAO.java ^
  src\dao\AppointmentDAO.java ^
  src\dao\NotificationDAO.java ^
  src\ui\LoginFrame.java ^
  src\ui\RegisterFrame.java ^
  src\ui\common\NotificationsPanel.java ^
  src\ui\common\ChangePasswordPanel.java ^
  src\ui\patient\PatientDashboard.java ^
  src\ui\patient\BookAppointmentPanel.java ^
  src\ui\patient\MyAppointmentsPanel.java ^
  src\ui\patient\PatientProfilePanel.java ^
  src\ui\doctor\DoctorDashboard.java ^
  src\ui\doctor\AppointmentsPanel.java ^
  src\ui\doctor\DoctorProfilePanel.java ^
  src\ui\admin\AdminDashboard.java ^
  src\ui\admin\StatisticsPanel.java ^
  src\ui\admin\ManageDoctorsPanel.java ^
  src\ui\admin\ManageSchedulesPanel.java ^
  src\ui\admin\ManageAppointmentsPanel.java ^
  src\ui\admin\ViewPatientsPanel.java ^
  src\ui\admin\SearchAppointmentsPanel.java ^
  src\ui\admin\ReportsPanel.java

if %ERRORLEVEL% == 0 (
    echo.
    echo Compilation successful!
) else (
    echo.
    echo Compilation FAILED. Check errors above.
)
pause
