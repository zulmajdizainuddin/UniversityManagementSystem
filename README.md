# University Management System

A web-based University Management System built with Java Servlet/JSP, deployed on GlassFish, and backed by Apache Derby. Developed as a Semester 5 project for the **Reuse and Component-Based Development** course at UniKL.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (JDK 8+) |
| Web Framework | Java Servlet / JSP |
| Application Server | GlassFish 4/5 |
| Database | Apache Derby (JavaDB) |
| Build Tool | Apache Ant (NetBeans) |
| Password Hashing | jBCrypt 0.4 |
| IDE | NetBeans |

---

## Features

The system supports three roles — **Admin**, **Lecturer**, and **Student** — each with their own dashboard and access controls.

### Admin
- Manage users (create, edit, delete — Admin, Lecturer, Student)
- Manage courses, subjects, and classes
- Assign lecturers to subjects and classes
- Assign students to subjects and classes
- View attendance analysis reports (by subject, class, or student) with CSV export
- Manage course evaluation questions (CRUD, reorder, activate/deactivate)

### Lecturer
- Manage attendance for assigned classes (batch update)
- Manage grades for assigned subjects
- View course evaluation results submitted by students

### Student
- View enrolled subjects and classes on dashboard
- View attendance summary and per-subject detail
- View grades
- Submit course evaluations (dynamic question system with rating and text questions)

---

## Security Implementations

Eight security improvements were applied as part of the component-based reuse exercise:

1. **CSRF Protection** — UUID token generated at login, stored in session, validated on every POST via `CsrfUtil`
2. **XSS Prevention** — `HtmlUtil.escape()` wraps all user-data output in JSPs; JSTL `${...}` auto-escapes where used
3. **Role-Based Access Control** — `Roles` constants class + `AuthFilter` enforcing role-path mapping; every servlet re-checks the session role
4. **Data Ownership Checks** — Lecturers can only modify grades/attendance for subjects/classes they are assigned to
5. **DB Credential Externalization** — Connection details stored in `web/WEB-INF/db.properties`, not hardcoded
6. **BaseDAO Pattern** — All DAOs extend `BaseDAO`, centralizing connection acquisition and error logging
7. **Java Logging** — `java.util.logging.Logger` replaces all `e.printStackTrace()` calls across every servlet and DAO
8. **Try-with-Resources** — All JDBC `Connection`, `PreparedStatement`, and `ResultSet` objects use try-with-resources to prevent leaks

---

## Project Structure

```
UniversityManagementSystem/
├── src/
│   ├── conf/
│   │   └── MANIFEST.MF
│   └── java/com/project/
│       ├── dao/          # Data Access Objects (all extend BaseDAO)
│       ├── filter/       # AuthFilter (role-based route protection)
│       ├── model/        # POJO model classes
│       ├── servlet/      # Servlet controllers
│       └── util/         # CsrfUtil, HtmlUtil, Roles, DBConnection
├── web/
│   ├── WEB-INF/
│   │   ├── db.properties # DB connection config
│   │   ├── web.xml
│   │   ├── glassfish-web.xml
│   │   └── jspf/         # Shared header/footer fragments
│   ├── admin/            # Admin JSP views
│   ├── lecturer/         # Lecturer JSP views
│   ├── student/          # Student JSP views
│   ├── css/
│   ├── images/
│   └── login.jsp
├── sql/
│   └── create_course_evaluation_table.sql
├── 1) Create_Tables_Proj_Reuse.sql
├── 2) Insert_Data_Proj_Reuse.sql
├── build.xml
└── nbproject/
```

---

## Setup & Installation

### Prerequisites
- NetBeans IDE (8.x or later)
- GlassFish Server 4 or 5 (configured in NetBeans)
- Apache Derby (bundled with NetBeans via JavaDB)
- JDK 8 or later

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/zulmajdizainuddin/UniversityManagementSystem.git
```

**2. Open in NetBeans**

File → Open Project → select the cloned folder.

**3. Start the Derby database**

In NetBeans: Services tab → Databases → Java DB → right-click → **Start Server**

**4. Create the database and tables**

- Services tab → Java DB → right-click → **Create Database**
  - Database name: `UniversityManagementDB`
  - Username: `nbuser`
  - Password: `nbuser`
- Connect to the new database, open the SQL editor, and run:
  1. `1) Create_Tables_Proj_Reuse.sql`
  2. `2) Insert_Data_Proj_Reuse.sql`
  3. `sql/create_course_evaluation_table.sql`

**5. Configure GlassFish**

In NetBeans: Services tab → Servers → ensure GlassFish is registered. Add it if not (Tools → Servers → Add Server → GlassFish).

**6. Run the project**

Right-click the project → **Run** (or press F6). NetBeans will build and deploy to GlassFish automatically.

**7. Access the application**

Open your browser and go to:
```
http://localhost:8080/UniversityManagementSystem/
```

### Default Login Credentials (from seed data)

| Role | Email | Password |
|---|---|---|
| Admin | admin@university.com | admin123 |
| Lecturer | lecturer@university.com | lecturer123 |
| Student | student@university.com | student123 |

> Passwords are stored using BCrypt hashing.

---

## Database Configuration

The DB connection is configured in `web/WEB-INF/db.properties`:

```properties
db.url=jdbc:derby://localhost:1527/UniversityManagementDB;create=true
db.user=nbuser
db.password=nbuser
```

Modify this file if your Derby instance uses different credentials or port.

---

## Course Members

| Name | Role |
|---|---|
| ZULMAJDI | Developer |

> Semester 5 — Reuse and Component-Based Development — UniKL
