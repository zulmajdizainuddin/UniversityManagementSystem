package com.project.servlet;

import com.project.dao.AttendanceDAO;
import com.project.dao.ClassesDAO;
import com.project.dao.LecturerClassDAO;
import com.project.dao.SubjectDAO;
import com.project.dao.StudentDAO;
import com.project.model.Attendance;
import com.project.model.Class;
import com.project.model.Subject;
import com.project.model.Student;
import com.project.model.User;
import com.project.util.CsrfUtil;
import com.project.util.Roles;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/AttendanceServlet")
public class AttendanceServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AttendanceServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !Roles.LECTURER.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "viewAttendance";

        try {
            SubjectDAO subjectDAO = new SubjectDAO();
            ClassesDAO classDAO = new ClassesDAO();
            StudentDAO studentDAO = new StudentDAO();
            AttendanceDAO attendanceDAO = new AttendanceDAO();

            switch (action) {
                case "viewAttendance": {
                    List<Subject> subjects = subjectDAO.getSubjectsByLecturer(user.getUserId());
                    request.setAttribute("subjects", subjects);
                    String subjectIdParam = request.getParameter("subjectId");
                    String classIdParam = request.getParameter("classId");
                    Integer selectedSubjectId = (subjectIdParam != null && !subjectIdParam.isEmpty()) ? Integer.parseInt(subjectIdParam) : null;
                    Integer selectedClassId = (classIdParam != null && !classIdParam.isEmpty()) ? Integer.parseInt(classIdParam) : null;
                    request.setAttribute("selectedSubjectId", selectedSubjectId);
                    request.setAttribute("selectedClassId", selectedClassId);
                    List<Class> classes = null;
                    if (selectedSubjectId != null) {
                        classes = classDAO.getClassesByLecturerAndSubject(user.getUserId(), selectedSubjectId);
                    }
                    request.setAttribute("classes", classes);
                    if (selectedClassId != null) {
                        List<Attendance> attendanceList = attendanceDAO.getAttendanceByClass(selectedClassId);
                        List<Student> students = studentDAO.getStudentsByClass(selectedClassId);
                        request.setAttribute("attendanceList", attendanceList);
                        request.setAttribute("students", students);
                    }
                    request.setAttribute("action", "viewAttendance");
                    request.getRequestDispatcher("/lecturer/ManageAttendance.jsp").forward(request, response);
                    break;
                }
                case "takeAttendance": {
                    List<Subject> subjectsTA = subjectDAO.getSubjectsByLecturer(user.getUserId());
                    request.setAttribute("subjects", subjectsTA);
                    String subjectIdTA = request.getParameter("subjectId");
                    String classIdTA = request.getParameter("classId");
                    String dateTA = request.getParameter("date");
                    Integer selectedSubjectIdTA = (subjectIdTA != null && !subjectIdTA.isEmpty()) ? Integer.parseInt(subjectIdTA) : null;
                    Integer selectedClassIdTA = (classIdTA != null && !classIdTA.isEmpty()) ? Integer.parseInt(classIdTA) : null;
                    request.setAttribute("selectedSubjectId", selectedSubjectIdTA);
                    request.setAttribute("selectedClassId", selectedClassIdTA);
                    request.setAttribute("selectedDate", dateTA);
                    List<Class> classesTA = null;
                    if (selectedSubjectIdTA != null) {
                        classesTA = classDAO.getClassesByLecturerAndSubject(user.getUserId(), selectedSubjectIdTA);
                    }
                    request.setAttribute("classes", classesTA);
                    List<Student> studentsTA = null;
                    List<Attendance> attendanceListTA = null;
                    if (selectedClassIdTA != null && dateTA != null && !dateTA.isEmpty()) {
                        studentsTA = studentDAO.getStudentsByClass(selectedClassIdTA);
                        attendanceListTA = attendanceDAO.getAttendanceByClassDateLecturer(selectedClassIdTA, dateTA, user.getUserId());
                    }
                    request.setAttribute("students", studentsTA);
                    request.setAttribute("attendanceList", attendanceListTA);
                    request.setAttribute("action", "takeAttendance");
                    request.getRequestDispatcher("/lecturer/ManageAttendance.jsp").forward(request, response);
                    break;
                }
                default:
                    response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=viewAttendance");
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AttendanceServlet GET error", e);
            session.setAttribute("errorMessage", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=viewAttendance");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !Roles.LECTURER.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // CSRF check
        if (!CsrfUtil.isValidToken(request)) {
            session.setAttribute("errorMessage", "Invalid request. Please try again.");
            response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=viewAttendance");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "saveAttendance";

        try {
            AttendanceDAO attendanceDAO = new AttendanceDAO();
            ClassesDAO classDAO = new ClassesDAO();
            LecturerClassDAO lcDao = new LecturerClassDAO();

            switch (action) {
                case "saveAttendance": {
                    String classIdParam = request.getParameter("classId");
                    String date = request.getParameter("date");
                    String[] studentIds = request.getParameterValues("studentIds");
                    if (classIdParam == null || classIdParam.isEmpty() || date == null || date.isEmpty() || studentIds == null) {
                        session.setAttribute("errorMessage", "Please select class, date and mark attendance.");
                        response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=takeAttendance");
                        return;
                    }
                    int classId = Integer.parseInt(classIdParam);

                    // Ownership check
                    if (!lcDao.lecturerTeachesClass(user.getUserId(), classId)) {
                        response.sendRedirect(request.getContextPath() + "/accessDenied.jsp");
                        return;
                    }

                    int subjectIdInt = classDAO.getSubjectIdByClassId(classId);
                    int lecturerId = user.getUserId();
                    for (String sid : studentIds) {
                        int studentId = Integer.parseInt(sid);
                        String status = request.getParameter("status_" + studentId);
                        Attendance attendance = new Attendance();
                        attendance.setStudentId(studentId);
                        attendance.setClassId(classId);
                        attendance.setSubjectId(subjectIdInt);
                        attendance.setDate(date);
                        attendance.setStatus(status);
                        attendance.setLecturerId(lecturerId);
                        attendanceDAO.addOrUpdateAttendance(attendance);
                    }
                    session.setAttribute("successMessage", "Attendance recorded successfully.");
                    response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=takeAttendance&subjectId=" + classDAO.getSubjectIdByClassId(classId) + "&classId=" + classId + "&date=" + date);
                    break;
                }
                case "updateAttendanceBatch": {
                    String[] attendanceIds = request.getParameterValues("attendanceIds");
                    if (attendanceIds != null) {
                        for (String attIdStr : attendanceIds) {
                            int attId = Integer.parseInt(attIdStr);
                            String status = request.getParameter("status_" + attId);
                            Attendance att = attendanceDAO.getAttendanceById(attId);
                            if (att != null && status != null) {
                                att.setStatus(status);
                                attendanceDAO.updateAttendance(att);
                            }
                        }
                        session.setAttribute("successMessage", "Attendance records updated successfully.");
                    } else {
                        session.setAttribute("errorMessage", "No attendance records selected for update.");
                    }
                    String subjectIdParam = request.getParameter("subjectId");
                    String classIdParam2 = request.getParameter("classId");
                    String redirectUrl = request.getContextPath() + "/AttendanceServlet?action=viewAttendance";
                    if (subjectIdParam != null && !subjectIdParam.isEmpty()) redirectUrl += "&subjectId=" + subjectIdParam;
                    if (classIdParam2 != null && !classIdParam2.isEmpty()) redirectUrl += "&classId=" + classIdParam2;
                    response.sendRedirect(redirectUrl);
                    break;
                }
                default:
                    response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=viewAttendance");
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AttendanceServlet POST error", e);
            session.setAttribute("errorMessage", "An error occurred. Please try again.");
            response.sendRedirect(request.getContextPath() + "/AttendanceServlet?action=viewAttendance");
        }
    }
}
