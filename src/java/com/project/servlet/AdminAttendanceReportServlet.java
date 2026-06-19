package com.project.servlet;

import com.project.dao.AttendanceReportDAO;
import com.project.dao.ClassesDAO;
import com.project.dao.StudentDAO;
import com.project.dao.SubjectDAO;
import com.project.model.Class;
import com.project.model.Student;
import com.project.model.Subject;
import com.project.model.User;
import com.project.util.Roles;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/AdminAttendanceReportServlet")
public class AdminAttendanceReportServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(AdminAttendanceReportServlet.class.getName());
    private static final String SUBJECT_VIEW = "/admin/admin_attendance_report_subject.jsp";
    private static final String CLASS_VIEW = "/admin/admin_attendance_report_class.jsp";
    private static final String STUDENT_VIEW = "/admin/admin_attendance_report_student.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !Roles.ADMIN.equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) action = "subjectView";

        try {
            switch (action) {
                case "subjectView": handleSubjectView(request, response); break;
                case "classView":   handleClassView(request, response); break;
                case "studentView": handleStudentView(request, response); break;
                case "exportCsv":   handleExportCsv(request, response); break;
                default:            handleSubjectView(request, response); break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "AdminAttendanceReportServlet error, action=" + action, e);
            request.setAttribute("errorMessage", "Error generating report: " + e.getMessage());
            request.getRequestDispatcher(SUBJECT_VIEW).forward(request, response);
        }
    }

    private void handleSubjectView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SubjectDAO subjectDAO = new SubjectDAO();
        AttendanceReportDAO reportDAO = new AttendanceReportDAO();

        List<Subject> subjects = subjectDAO.getAllSubjects();
        request.setAttribute("subjects", subjects);

        String subjectIdParam = request.getParameter("subjectId");
        String thresholdParam = request.getParameter("threshold");
        Integer subjectId = (subjectIdParam != null && !subjectIdParam.isEmpty()) ? Integer.parseInt(subjectIdParam) : null;
        double threshold = (thresholdParam != null && !thresholdParam.isEmpty()) ? Double.parseDouble(thresholdParam) : 80.0;

        request.setAttribute("selectedSubjectId", subjectId);
        request.setAttribute("threshold", threshold);
        if (subjectId != null) {
            request.setAttribute("summary", reportDAO.getSubjectReport(subjectId));
        }
        request.getRequestDispatcher(SUBJECT_VIEW).forward(request, response);
    }

    private void handleClassView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SubjectDAO subjectDAO = new SubjectDAO();
        ClassesDAO classesDAO = new ClassesDAO();
        AttendanceReportDAO reportDAO = new AttendanceReportDAO();

        List<Subject> subjects = subjectDAO.getAllSubjects();
        request.setAttribute("subjects", subjects);

        String subjectIdParam = request.getParameter("subjectId");
        String classIdParam = request.getParameter("classId");
        String thresholdParam = request.getParameter("threshold");
        Integer subjectId = (subjectIdParam != null && !subjectIdParam.isEmpty()) ? Integer.parseInt(subjectIdParam) : null;
        Integer classId = (classIdParam != null && !classIdParam.isEmpty()) ? Integer.parseInt(classIdParam) : null;
        double threshold = (thresholdParam != null && !thresholdParam.isEmpty()) ? Double.parseDouble(thresholdParam) : 80.0;

        request.setAttribute("selectedSubjectId", subjectId);
        request.setAttribute("selectedClassId", classId);
        request.setAttribute("threshold", threshold);
        if (subjectId != null) {
            List<Class> classes = classesDAO.getClassesBySubject(subjectId);
            request.setAttribute("classes", classes);
        }
        if (classId != null) {
            request.setAttribute("summary", reportDAO.getClassReport(classId));
        }
        request.getRequestDispatcher(CLASS_VIEW).forward(request, response);
    }

    private void handleStudentView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        StudentDAO studentDAO = new StudentDAO();
        AttendanceReportDAO reportDAO = new AttendanceReportDAO();

        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);

        String studentIdParam = request.getParameter("studentId");
        String thresholdParam = request.getParameter("threshold");
        Integer studentId = (studentIdParam != null && !studentIdParam.isEmpty()) ? Integer.parseInt(studentIdParam) : null;
        double threshold = (thresholdParam != null && !thresholdParam.isEmpty()) ? Double.parseDouble(thresholdParam) : 80.0;

        request.setAttribute("selectedStudentId", studentId);
        request.setAttribute("threshold", threshold);
        if (studentId != null) {
            request.setAttribute("summary", reportDAO.getStudentReport(studentId));
        }
        request.getRequestDispatcher(STUDENT_VIEW).forward(request, response);
    }

    private void handleExportCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = request.getParameter("type");
        if (type == null) type = "subject";

        AttendanceReportDAO reportDAO = new AttendanceReportDAO();
        String filename = "attendance_report_" + type + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8.name()) + "\"");

        try (PrintWriter writer = response.getWriter()) {
            if ("subject".equalsIgnoreCase(type)) {
                int subjectId = Integer.parseInt(request.getParameter("subjectId"));
                AttendanceReportDAO.SubjectReportSummary summary = reportDAO.getSubjectReport(subjectId);
                writer.println("Student Number,Student Name,Present,Absent,Attendance %");
                for (AttendanceReportDAO.SubjectReportRow row : summary.studentRows) {
                    writer.printf("\"%s\",\"%s\",%d,%d,%.2f%n",
                            safe(row.studentNumber), safe(row.studentName),
                            row.presentCount, row.absentCount, row.attendancePercent);
                }
            } else if ("class".equalsIgnoreCase(type)) {
                int classId = Integer.parseInt(request.getParameter("classId"));
                AttendanceReportDAO.ClassReportSummary summary = reportDAO.getClassReport(classId);
                writer.println("Student Number,Student Name,Present,Absent,Attendance %");
                for (AttendanceReportDAO.ClassReportRow row : summary.studentRows) {
                    writer.printf("\"%s\",\"%s\",%d,%d,%.2f%n",
                            safe(row.studentNumber), safe(row.studentName),
                            row.presentCount, row.absentCount, row.attendancePercent);
                }
            } else if ("student".equalsIgnoreCase(type)) {
                int studentId = Integer.parseInt(request.getParameter("studentId"));
                AttendanceReportDAO.StudentReportSummary summary = reportDAO.getStudentReport(studentId);
                writer.println("Subject Code,Subject Name,Present,Absent,Attendance %");
                for (AttendanceReportDAO.StudentSubjectRow row : summary.subjectRows) {
                    writer.printf("\"%s\",\"%s\",%d,%d,%.2f%n",
                            safe(row.subjectCode), safe(row.subjectName),
                            row.presentCount, row.absentCount, row.attendancePercent);
                }
            }
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("\"", "\"\"");
    }
}
