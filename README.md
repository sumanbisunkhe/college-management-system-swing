# 🎓 College Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Swing-4A90E2?style=for-the-badge&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

A modern, feature-rich college management system built with Java Swing and PostgreSQL.

</div>

## 🌟 Features

### 📊 Dashboard
- Modern, fullscreen interface
- Intuitive navigation with tabbed panels
- Real-time data updates
- Clean and professional UI design

### 👥 User Management
- **Student Management**
  - Complete student profile management
  - Email verification for unique records
  - Date of birth tracking
  - Flexible course enrollment system

- **Teacher Management**
  - Teacher profile management
  - Email-based unique identification
  - Course assignment system
  - Schedule management

### 📚 Academic Management
- **Course Management**
  - Department-wise course organization
  - Credit system implementation
  - Course capacity management
  - Prerequisites tracking

- **Department Management**
  - Department creation and management
  - Course-department associations
  - Faculty assignments

- **Enrollment System**
  - Student course registration
  - Grade management
  - Semester-wise enrollment tracking
  - Duplicate enrollment prevention

### 📅 Schedule Management
- **Class Scheduling**
  - Automated conflict detection
  - Room availability checking
  - Teacher schedule management
  - Time slot management

- **Room Management**
  - Room capacity tracking
  - Room number assignment
  - Occupancy management

- **Time Slot Management**
  - Flexible time slot creation
  - Conflict prevention
  - Schedule optimization

## 🏗️ Technical Architecture

### 💾 Database Structure
```sql
-- Core Tables
departments (id, name)
students (id, name, dob, email)
teachers (id, name, email)
courses (id, name, credit, dept_id)

-- Relationship Tables
enrollments (id, student_id, course_id, semester, grade)
class_schedules (id, course_id, teacher_id, timeslot_id, room_id)

-- Supporting Tables
rooms (id, room_number, capacity)
timeslots (id, start_time, end_time)
```

### 🔧 Technical Features
- **Data Validation**
  - Email format verification
  - Date format validation
  - Required field checking
  - Duplicate entry prevention

- **Error Handling**
  - Comprehensive error messages
  - User-friendly error displays
  - Database error management
  - Input validation feedback

- **Security Features**
  - SQL injection prevention
  - Data integrity checks
  - Unique constraint enforcement
  - Referential integrity

## 🚀 Recent Updates

### Version 2.0
- Implemented fullscreen application window
- Added Room and TimeSlot management
- Enhanced class scheduling with conflict detection
- Improved data refresh mechanism
- Added comprehensive validation system

### Version 1.0
- Initial release with core features
- Basic student and teacher management
- Course and enrollment system
- Department organization

## 💻 Installation

1. **Prerequisites**
   ```bash
   - Java JDK 17 or higher
   - PostgreSQL 12 or higher
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE college_management;
   ```

3. **Configuration**
   - Update database credentials in `DatabaseConnection.java`
   - Run `DatabaseInitializer.java` to create tables

4. **Run Application**
   ```bash
   java -jar college-management-system.jar
   ```

## 🛠️ Technical Requirements

- **Java Version**: JDK 17+
- **Database**: PostgreSQL 12+
- **Screen Resolution**: 1920x1080 (Recommended)
- **Memory**: 4GB RAM (Minimum)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
<div align="center">
Made with ❤️ by Your Team Name
</div> 