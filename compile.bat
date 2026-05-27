@echo off
echo Compiling Hospital System...

set JAR=mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar

if not exist out mkdir out

javac -cp "%JAR%" -d out -sourcepath src ^
  src\Main.java ^
  src\backend\db\DBConnection.java ^
  src\backend\models\User.java ^
  src\backend\models\Doctor.java ^
  src\backend\models\Appointment.java ^
  src\backend\models\Schedule.java ^
  src\backend\models\Notification.java ^
  src\backend\dao\UserDAO.java ^
  src\backend\dao\DoctorDAO.java ^
  src\backend\dao\ScheduleDAO.java ^
  src\backend\dao\AppointmentDAO.java ^
  src\backend\dao\NotificationDAO.java ^
  src\frontend\ui\HomeFrame.java ^
  src\frontend\ui\LoginFrame.java ^
  src\frontend\ui\RegisterFrame.java ^
  src\frontend\ui\common\NotificationsPanel.java ^
  src\frontend\ui\common\ChangePasswordPanel.java ^
  src\frontend\ui\patient\PatientDashboard.java ^
  src\frontend\ui\patient\BookAppointmentPanel.java ^
  src\frontend\ui\patient\MyAppointmentsPanel.java ^
  src\frontend\ui\patient\PatientProfilePanel.java ^
  src\frontend\ui\doctor\DoctorDashboard.java ^
  src\frontend\ui\doctor\AppointmentsPanel.java ^
  src\frontend\ui\doctor\DoctorProfilePanel.java ^
  src\frontend\ui\admin\AdminDashboard.java ^
  src\frontend\ui\admin\StatisticsPanel.java ^
  src\frontend\ui\admin\ManageDoctorsPanel.java ^
  src\frontend\ui\admin\ManageSchedulesPanel.java ^
  src\frontend\ui\admin\ManageAppointmentsPanel.java ^
  src\frontend\ui\admin\ViewPatientsPanel.java ^
  src\frontend\ui\admin\SearchAppointmentsPanel.java ^
  src\frontend\ui\admin\ReportsPanel.java

if %ERRORLEVEL% == 0 (
    echo.
    echo Compilation successful!
) else (
    echo.
    echo Compilation FAILED. Check errors above.
)
pause
