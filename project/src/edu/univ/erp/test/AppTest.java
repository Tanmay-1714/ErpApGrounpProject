package edu.univ.erp.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.domain.User;

public class AppTest {

    private AdminService adminService;
    private StudentService studentService;
    private InstructorService instructorService;

    @BeforeEach
    public void setUp() {
        adminService = new AdminService();
        studentService = new StudentService();
        instructorService = new InstructorService();
        // Default to Admin for most tests, override inside tests where needed
        UserSession.getInstance().setCurrentUser(new User(1, "admin_test", "Admin"));
    }

    @AfterEach
    public void tearDown() {
        // Always ensure Maintenance Mode is OFF after tests to prevent locking the real DB
        if (UserSession.getInstance().getRole().equals("Admin")) {
            adminService.toggleMaintenanceMode(false);
        } else {
            // specialized hack to force reset if current user isn't admin
            UserSession.getInstance().setCurrentUser(new User(1, "admin_cleaner", "Admin"));
            adminService.toggleMaintenanceMode(false);
        }
        UserSession.getInstance().clearSession();
    }

    // ==========================================
    // GROUP 1: INPUT VALIDATION (Data Integrity)
    // ==========================================

    @Test
    public void test1_CreateSection_NegativeCapacity() {
        String result = adminService.createNewSection(1, "Mon", "10:00", "Room1", -10, "Spring", 2024);
        Assertions.assertEquals("FAILURE: Section capacity must be greater than zero.", result);
    }

    @Test
    public void test2_CreateSection_ZeroCapacity() {
        String result = adminService.createNewSection(1, "Mon", "10:00", "Room1", 0, "Spring", 2024);
        Assertions.assertEquals("FAILURE: Section capacity must be greater than zero.", result);
    }

    @Test
    public void test3_CreateSection_InvalidYear_Past() {
        // System requires year >= 2000
        String result = adminService.createNewSection(1, "Mon", "10:00", "Room1", 50, "Spring", 1999);
        Assertions.assertEquals("FAILURE: Invalid year (must be > 2000).", result);
    }

    @Test
    public void test4_CreateCourse_NegativeCredits() {
        String result = adminService.createNewCourse("TEST101", "Bad Course", -4.0);
        Assertions.assertEquals("FAILURE: Course must have positive credit hours.", result);
    }

    @Test
    public void test5_CreateCourse_ZeroCredits() {
        String result = adminService.createNewCourse("TEST102", "Zero Credit Course", 0.0);
        Assertions.assertEquals("FAILURE: Course must have positive credit hours.", result);
    }

    @Test
    public void test6_CreateUser_Student_InvalidYear() {
        // Valid args: username, password, role, rollNo, Program, Year
        // Passing "ABC" as year should fail parsing
        String result = adminService.createNewUser("badstu", "pass", "Student", "S999", "CS", "ABC");
    }

    @Test
    public void test7_CreateUser_Student_YearOutOfRange() {
        // Year 3000 is out of likely range logic if implemented, or check standard bounds
        String result = adminService.createNewUser("futurestu", "pass", "Student", "S999", "CS", "3000");
    }

    @Test
    public void test8_CreateUser_Student_MissingDetails() {
        // Student requires 3 extra details (Roll, Prog, Year). We only provide 2.
        String result = adminService.createNewUser("lazystu", "pass", "Student", "S001", "CS");
    }

    // ==========================================
    // GROUP 2: ACCESS CONTROL (Role Enforcement)
    // ==========================================

    @Test
    public void test9_Student_CannotCreateCourse() {
        // Login as Student
        UserSession.getInstance().setCurrentUser(new User(2, "student1", "Student"));
        
        String result = adminService.createNewCourse("HACK101", "Hacking", 3.0);
        Assertions.assertTrue(result.startsWith("FAILURE"), "Student should not create courses");
        Assertions.assertTrue(result.contains("Only administrators"), "Message should mention admin privilege");
    }

    @Test
    public void test10_Student_CannotCreateUser() {
        UserSession.getInstance().setCurrentUser(new User(2, "student1", "Student"));
        
        String result = adminService.createNewUser("newadmin", "pass", "Admin");
        Assertions.assertTrue(result.contains("Only administrators"), "Student should not create users");
    }

    @Test
    public void test11_Student_CannotToggleMaintenance() {
        UserSession.getInstance().setCurrentUser(new User(2, "student1", "Student"));
        
        String result = adminService.toggleMaintenanceMode(true);
        Assertions.assertTrue(result.contains("FAILURE"), "Student cannot toggle maintenance");
    }

    @Test
    public void test12_Instructor_CannotAssignInstructor() {
        // Login as Instructor
        UserSession.getInstance().setCurrentUser(new User(3, "prof1", "Instructor"));
        
        String result = adminService.assignInstructor(1, 3); // Try to assign self
        Assertions.assertTrue(result.contains("Only administrators"), "Instructor cannot assign instructors");
    }

    @Test
    public void test13_Instructor_CannotCreateSection() {
        UserSession.getInstance().setCurrentUser(new User(3, "prof1", "Instructor"));
        
        String result = adminService.createNewSection(1, "Fri", "10:00", "Lab", 20, "Fall", 2024);
        Assertions.assertTrue(result.contains("Only administrators"), "Instructor cannot create sections");
    }

    // ==========================================
    // GROUP 3: MAINTENANCE MODE ENFORCEMENT
    // ==========================================

    @Test
    public void test14_MaintenanceMode_BlocksStudentRegistration() {
        // 1. Admin turns Maintenance ON
        adminService.toggleMaintenanceMode(true);
        
        // 2. Login as Student
        UserSession.getInstance().setCurrentUser(new User(2, "student1", "Student"));
        
        // 3. Try to Register
        String result = studentService.registerForSection(1);
        
        Assertions.assertEquals("FAILURE: Maintenance Mode ON.", result);
    }

    @Test
    public void test15_MaintenanceMode_BlocksInstructorGrading() {
        // 1. Admin turns Maintenance ON
        adminService.toggleMaintenanceMode(true);
        
        // 2. Login as Instructor
        UserSession.getInstance().setCurrentUser(new User(3, "prof1", "Instructor"));
        
        // 3. Try to Update Grade
        String result = instructorService.updateStudentScore(1, 90, 90, 90);
        
        Assertions.assertEquals("FAILURE: Maintenance Mode ON.", result);
    }
}
