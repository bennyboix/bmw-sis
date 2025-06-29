# 🎓 BMW Student Information System (BMW-SIS)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-24-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)]()

A comprehensive **Student Information System** built with Spring Boot, featuring student enrollment, grade management, faculty portal, and administrative dashboard for IT education management.

## 📋 Table of Contents
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [System Architecture](#-system-architecture)
- [Usage Guide](#-usage-guide)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Contributing](#-contributing)
- [License](#-license)

## ✨ Features

### 👨‍🎓 Student Portal
- **🔐 Secure Authentication**: Students log in using Student ID and password
- **📊 Interactive Dashboard**: Comprehensive overview with quick navigation
- **📈 Grade Management**: View midterm/final grades with pass/fail indicators
- **📅 Schedule Viewer**: Real-time schedule with faculty and room information
- **📝 Smart Enrollment**: AI-powered subject enrollment with slot management

### 👨‍🏫 Faculty Portal
- **📚 Class Management**: Manage assigned classes and student rosters
- **✅ Grade Entry**: Streamlined grade input and management system
- **📋 Student Tracking**: Monitor student progress and attendance
- **🗓️ Schedule Overview**: View teaching schedule and room assignments

### 👨‍💼 Admin Dashboard
- **🏫 System Management**: Complete control over enrollment periods
- **👥 User Management**: Student, faculty, and admin account management
- **📊 Analytics & Reports**: Comprehensive reporting and data visualization
- **⚙️ Configuration**: System settings and curriculum management

### 🎯 Core System Features
- **💻 IT-Focused Curriculum**: Specialized Information Technology course catalog
- **⏰ Enrollment Period Control**: Flexible enrollment window management
- **🎯 Smart Slot Management**: Prevents over-enrollment with real-time capacity tracking
- **📚 Year Level Restrictions**: Automated prerequisite and level checking
- **👨‍🏫 Faculty Assignment**: Intelligent faculty-subject scheduling system

## 🛠️ Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Backend** | Spring Boot | 3.5.0 |
| **Database** | MySQL | 8.0+ |
| **ORM** | Spring Data JPA | Latest |
| **Template Engine** | Thymeleaf | Latest |
| **Frontend** | HTML5, CSS3, JavaScript | Latest |
| **Build Tool** | Maven | 3.6+ |
| **Java Version** | OpenJDK | 24+ |

## 🚀 Getting Started

### Prerequisites
```bash
# Check Java version (24+ required)
java --version

# Check Maven version (3.6+ required)
mvn --version

# Check MySQL version (8.0+ required)
mysql --version
```

**Required Software:**
- ☕ **Java 24+** - OpenJDK or Oracle JDK
- 🔧 **Maven 3.6+** - Build automation tool
- 🗄️ **MySQL 8.0+** - Database server
- 🖥️ **MySQL Workbench** (Optional) - Database management GUI

### 📦 Installation

1. **Clone the repository**:
```bash
git clone https://github.com/bennyboix/bmw-sis.git
cd bmw-sis
```

2. **Set up MySQL Database**:
```sql
-- Create database (optional - app will create if not exists)
CREATE DATABASE bmwsis;

-- Create MySQL user (optional)
CREATE USER 'bmwsis_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON bmwsis.* TO 'bmwsis_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configure Database Connection**:
   Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bmwsis?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

4. **Build the project**:
```bash
./mvnw clean install
```

5. **Run the application**:
```bash
./mvnw spring-boot:run
```

6. **Access the application**:
   - 🏠 **Main Portal**: http://localhost:5521/sis
   - 👨‍🎓 **Student Login**: http://localhost:5521/student/login
   - 👨‍🏫 **Faculty Login**: http://localhost:5521/faculty/login
   - 👨‍💼 **Admin Login**: http://localhost:5521/admin/login
   - 📊 **System Monitoring**: http://localhost:5522/actuator (Admin only)

### 🔑 Default Test Accounts

#### 👨‍🎓 Students
| Student ID | Password | Name | Year Level | Status |
|------------|----------|------|------------|--------|
| 2021-0001 | password123 | John Doe | 3 | Active |
| 2021-0002 | password123 | Jane Smith | 2 | Active |
| 2022-0001 | password123 | Mike Johnson | 2 | Active |

#### 👨‍🏫 Faculty
| Faculty ID | Password | Name | Department | Status |
|------------|----------|------|------------|--------|
| FAC-001 | faculty123 | Dr. Maria Garcia | Computer Science | Active |
| FAC-002 | faculty123 | Prof. Robert Chen | Information Technology | Active |
| FAC-003 | faculty123 | Dr. Sarah Williams | Software Engineering | Active |

#### 👨‍💼 Admin
| Admin ID | Password | Name | Role |
|----------|----------|------|------|
| ADMIN-001 | admin123 | System Administrator | Super Admin |

## 🏗️ System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│                 │    │                 │    │                 │
│ • Thymeleaf     │◄──►│ • Spring Boot   │◄──►│ • MySQL 8.0+    │
│ • HTML/CSS/JS   │    │ • Spring MVC    │    │ • JPA/Hibernate │
│ • Bootstrap     │    │ • Spring Data   │    │ • Connection Pool│
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 📚 Available IT Subjects

<details>
<summary><strong>📖 First Year (Year 1) - Foundation Courses</strong></summary>

| Code | Subject Name | Units | Prerequisites |
|------|--------------|-------|---------------|
| IT-101 | Introduction to Programming | 3 | None |
| IT-102 | Web Development Fundamentals | 3 | None |
| IT-103 | Computer Fundamentals | 3 | None |
| IT-104 | Mathematics for IT | 3 | None |

</details>

<details>
<summary><strong>📘 Second Year (Year 2) - Core Courses</strong></summary>

| Code | Subject Name | Units | Prerequisites |
|------|--------------|-------|---------------|
| IT-201 | Object-Oriented Programming | 3 | IT-101 |
| IT-202 | Database Management Systems | 3 | IT-103 |
| IT-203 | Advanced Web Development | 3 | IT-102 |
| IT-204 | Computer Networks | 3 | IT-103 |
| IT-205 | Data Structures and Algorithms | 3 | IT-101, IT-104 |

</details>

<details>
<summary><strong>📗 Third Year (Year 3) - Advanced Courses</strong></summary>

| Code | Subject Name | Units | Prerequisites |
|------|--------------|-------|---------------|
| IT-301 | Software Engineering | 3 | IT-201, IT-205 |
| IT-302 | Advanced Database Systems | 3 | IT-202 |
| IT-303 | Web Application Frameworks | 3 | IT-203 |
| IT-304 | Mobile Application Development | 3 | IT-201 |
| IT-305 | Cybersecurity Fundamentals | 3 | IT-204 |
| IT-306 | System Analysis and Design | 3 | IT-301 |

</details>

## 📖 Usage Guide

### 👨‍🎓 For Students

1. **🔐 Login Process**
   - Navigate to the student portal
   - Enter your Student ID and password
   - Access your personalized dashboard

2. **📊 Dashboard Features**
   - View personal information and academic status
   - Quick navigation to all available features
   - Real-time notifications and announcements

3. **📈 Grade Management**
   - Comprehensive grade viewing with:
     - Subject details and faculty information
     - Midterm and final examination scores
     - Calculated final ratings and pass/fail status
     - Historical academic performance

4. **📅 Schedule Management**
   - Current enrolled subjects with complete details
   - Class schedules including day, time, and room
   - Faculty contact information
   - Total enrolled units tracking

5. **📝 Smart Enrollment**
   - Browse subjects appropriate for your year level
   - Real-time slot availability checking
   - Intelligent prerequisite validation
   - One-click enrollment process

### 👨‍🏫 For Faculty

1. **📚 Class Management**
   - View assigned classes and sections
   - Access student rosters and information
   - Monitor class capacity and enrollment

2. **✅ Grade Entry System**
   - Streamlined grade input interface
   - Bulk grade import/export functionality
   - Automatic GPA calculations

### 👨‍💼 For Administrators

1. **🏫 System Configuration**
   - Enrollment period management
   - Academic calendar setup
   - System-wide settings control

2. **👥 User Management**
   - Student, faculty, and admin account creation
   - Role-based access control
   - Bulk user import/export

## 🗄️ Database Schema

### Core Entities

```sql
-- Key database tables and relationships
Student ──┐
          ├── Enrollment ──── Subject
          └── Grade ────────── SubjectSchedule ──── Faculty

EnrollmentPeriod ──── System Configuration
Program ──── CurriculumAssignment ──── Curriculum
```

### Entity Relationships
- **Student** ↔ **Enrollment** (One-to-Many)
- **Subject** ↔ **SubjectSchedule** (One-to-Many)
- **Faculty** ↔ **SubjectSchedule** (One-to-Many)
- **Enrollment** ↔ **Grade** (One-to-One)

## ⚙️ Configuration

### Database Configuration
```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bmwsis?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Server Configuration
```properties
# Server Configuration
server.port=5521
spring.application.name=bmwsis
server.servlet.context-path=/
```

## 📁 Project Structure

```
src/main/java/com/sisregistration/bmwsis/
├── 📂 config/          # Configuration classes
├── 📂 controller/      # Web controllers (MVC)
├── 📂 entity/          # JPA entities
├── 📂 repository/      # Data access layer
├── 📂 service/         # Business logic layer
└── 📄 BmwsisApplication.java

src/main/resources/
├── 📂 static/          # CSS, JS, Images
├── 📂 templates/       # Thymeleaf templates
└── 📄 application.properties
```

## 🚀 Future Enhancements

- [ ] 📱 **Mobile Application** - React Native/Flutter app
- [ ] 🔔 **Real-time Notifications** - WebSocket integration
- [ ] 📊 **Advanced Analytics** - Machine learning insights
- [ ] 🌐 **Multi-language Support** - Internationalization
- [ ] 🔗 **API Integration** - RESTful API for third-party systems
- [ ] 📧 **Email System** - Automated notifications
- [ ] 🎨 **Theme Customization** - Dark/light mode support
- [ ] 📈 **Performance Optimization** - Caching and optimization


---

<div align="center">
  <strong>Made with ❤️ for BMW Student Information System</strong>
  <br>
  <sub>Built with Spring Boot • Powered by Java • Designed for Education</sub>
</div> 