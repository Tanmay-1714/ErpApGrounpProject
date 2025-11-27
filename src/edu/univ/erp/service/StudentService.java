package edu.univ.erp.service;
import edu.univ.erp.auth.UserSession;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import java.util.List;

public class StudentService {
    private EnrollmentDAO eDao = new EnrollmentDAO();
    private SectionDAO sDao = new SectionDAO();
    private AdminService adminService = new AdminService();

    public List<Section> getCourseCatalog() { return sDao.getAllSections(); }
    public List<Enrollment> getMyRegistrations() { 
        return eDao.getMyRegistrations(UserSession.getInstance().getProfileId()); 
    }
    
    public String registerForSection(int sectionId) {
        if (adminService.isMaintenanceModeEnabled()) return "FAILURE: Maintenance Mode ON.";
        int sid = UserSession.getInstance().getProfileId();
        if (eDao.isStudentEnrolled(sid, sectionId)) return "FAILURE: Already registered.";
        return eDao.registerStudent(sid, sectionId) ? "SUCCESS" : "FAILURE";
    }

    public String dropSection(int sectionId) {
        if (adminService.isMaintenanceModeEnabled()) return "FAILURE: Maintenance Mode ON.";
        return eDao.deleteEnrollment(UserSession.getInstance().getProfileId(), sectionId) ? "SUCCESS" : "FAILURE";
    }
}
