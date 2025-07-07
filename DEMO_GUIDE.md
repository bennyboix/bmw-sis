# 🎬 BMW-SIS Complete Demo Guide

## 🚀 Initial Setup
1. Start the application: `./mvnw spring-boot:run`
2. Open browser to: `http://localhost:5521/sis`
3. Have these test accounts ready:

| Role | Username | Password | Name |
|------|----------|----------|------|
| Admin | ADMIN-001 | admin123 | System Administrator |
| Faculty | FAC-001 | faculty123 | Dr. Maria Garcia |
| Student | 2021-0001 | password123 | John Doe |

---

## 👨‍💼 ADMIN PORTAL DEMO

### 🔐 Step 1: Admin Login
1. Navigate to: `http://localhost:5521/admin/login`
2. Enter Username: `ADMIN-001`
3. Enter Password: `admin123`
4. Click **"Login"** button
5. **Expected Result**: Redirected to Admin Dashboard

### 📊 Step 2: Explore Admin Dashboard
1. **Overview**: See system statistics (total students, faculty, subjects)
2. **Quick Actions**: Notice quick access buttons for common tasks
3. **Navigation Menu**: Familiarize with sidebar options:
   - Student Management
   - Faculty Management
   - Subject Management
   - Enrollment Periods
   - System Reports

### 👨‍🎓 Step 3: Add New Student (Complete Workflow)
1. Click **"Student Management"** in sidebar
2. Click **"Add New Student"** button
3. Fill out the form:
   ```
   Student ID: 2024-0001
   First Name: Alice
   Last Name: Johnson
   Email: alice.johnson@student.bmw.edu
   Phone: +1-555-0123
   Year Level: 1
   Program: Information Technology
   Status: Active
   Password: student123
   ```
4. Click **"Save Student"** button
5. **Expected Result**: Success message + redirect to student list
6. **Verify**: Find "Alice Johnson" in the student list

### 👨‍🏫 Step 4: Add New Faculty Member
1. Click **"Faculty Management"** in sidebar
2. Click **"Add New Faculty"** button
3. Fill out the form:
   ```
   Faculty ID: FAC-004
   First Name: Dr. James
   Last Name: Wilson
   Email: james.wilson@faculty.bmw.edu
   Phone: +1-555-0456
   Department: Computer Science
   Position: Associate Professor
   Status: Active
   Password: faculty123
   ```
4. Click **"Save Faculty"** button
5. **Expected Result**: Success message + new faculty in list

### 📚 Step 5: Create New Subject
1. Click **"Subject Management"** in sidebar
2. Click **"Add New Subject"** button
3. Fill out the form:
   ```
   Subject Code: IT-107
   Subject Name: Introduction to AI
   Units: 3
   Year Level: 1
   Description: Basic concepts of Artificial Intelligence
   Prerequisites: None
   Status: Active
   ```
4. Click **"Save Subject"** button
5. **Expected Result**: New subject appears in subject list

### 📅 Step 6: Manage Enrollment Periods
1. Click **"Enrollment Periods"** in sidebar
2. Click **"Create New Period"** button
3. Set up enrollment period:
   ```
   Period Name: Spring 2024 Enrollment
   Start Date: [Select date 1 week from now]
   End Date: [Select date 3 weeks from now]
   Academic Year: 2024
   Semester: Spring
   Status: Active
   ```
4. Click **"Create Period"** button
5. **Expected Result**: New enrollment period created and active

### 📈 Step 7: View System Reports
1. Click **"System Reports"** in sidebar
2. Explore available reports:
   - **Student Enrollment Report**: View enrollment statistics
   - **Grade Distribution**: See grade patterns across subjects
   - **Faculty Workload**: Check teaching loads
   - **Subject Capacity**: Monitor enrollment limits
3. Click on each report to see detailed data
4. **Expected Result**: Comprehensive data visualization

### ⚙️ Step 8: System Configuration
1. Click **"System Settings"** in sidebar
2. Review/modify settings:
   - Academic year settings
   - Grade calculation rules
   - Enrollment restrictions
   - System notifications
3. Make a test change (e.g., modify max enrollment units)
4. Click **"Save Settings"**
5. **Expected Result**: Settings updated successfully

---

## 👨‍🏫 FACULTY PORTAL DEMO

### 🔐 Step 1: Faculty Login
1. Navigate to: `http://localhost:5521/faculty/login`
2. Enter Faculty ID: `FAC-001`
3. Enter Password: `faculty123`
4. Click **"Login"** button
5. **Expected Result**: Faculty Dashboard loads

### 📊 Step 2: Explore Faculty Dashboard
1. **Personal Info**: View faculty profile information
2. **Quick Stats**: See assigned classes, total students, pending grades
3. **Today's Schedule**: Check current day's classes
4. **Recent Activity**: Review recent grade entries and updates

### 📚 Step 3: View Assigned Classes
1. Click **"My Classes"** in navigation
2. Review assigned subjects:
   - Subject codes and names
   - Number of enrolled students
   - Class schedules (day, time, room)
   - Semester information
3. Click on a specific class to see details
4. **Expected Result**: Detailed class information with student roster

### 👥 Step 4: Manage Student Roster
1. From "My Classes", click on **"IT-201: Object-Oriented Programming"**
2. View complete student roster:
   - Student names and IDs
   - Contact information
   - Current grade status
3. Click **"View Student Details"** for a specific student
4. **Expected Result**: Detailed student academic profile

### ✅ Step 5: Enter Grades (Complete Process)
1. Stay in the class view for "IT-201"
2. Click **"Grade Management"** tab
3. For each student, enter grades:
   ```
   John Doe (2021-0001):
   - Midterm: 85
   - Final: 88
   - Click "Update Grade"
   
   Jane Smith (2021-0002):
   - Midterm: 92
   - Final: 90
   - Click "Update Grade"
   ```
4. Click **"Calculate Final Grades"** button
5. Review calculated final ratings and pass/fail status
6. Click **"Submit All Grades"** button
7. **Expected Result**: Grades submitted and students can view them

### 📊 Step 6: Grade Analytics
1. Click **"Grade Analytics"** in navigation
2. Review class performance metrics:
   - Grade distribution charts
   - Pass/fail rates
   - Comparison with previous semesters
   - Individual student progress tracking
3. Export grade reports if needed
4. **Expected Result**: Comprehensive grade analysis

### 📅 Step 7: Schedule Management
1. Click **"My Schedule"** in navigation
2. View weekly/monthly schedule:
   - All assigned classes
   - Room assignments
   - Time conflicts (if any)
3. Check **"Upcoming Classes"** section
4. **Expected Result**: Complete teaching schedule overview

### 💬 Step 8: Student Communication
1. Go back to a specific class roster
2. Click **"Send Announcement"** button
3. Create announcement:
   ```
   Subject: Midterm Exam Schedule
   Message: The midterm exam for IT-201 will be held on [date] 
   at 10:00 AM in Room 101. Please bring your student ID and 
   writing materials.
   ```
4. Click **"Send to All Students"**
5. **Expected Result**: Announcement sent to all enrolled students

---

## 👨‍🎓 STUDENT PORTAL DEMO

### 🔐 Step 1: Student Login
1. Navigate to: `http://localhost:5521/student/login`
2. Enter Student ID: `2021-0001`
3. Enter Password: `password123`
4. Click **"Login"** button
5. **Expected Result**: Student Dashboard loads

### 📊 Step 2: Explore Student Dashboard
1. **Personal Information**: View student profile
2. **Academic Summary**: See current GPA, total units, year level
3. **Quick Actions**: Notice enrollment, grades, schedule buttons
4. **Announcements**: Check recent announcements from faculty
5. **Academic Progress**: View progression through the program

### 📈 Step 3: View Current Grades
1. Click **"View Grades"** from dashboard or navigation
2. Review enrolled subjects with grades:
   - Subject codes and names
   - Faculty information
   - Midterm scores
   - Final exam scores
   - Final ratings
   - Pass/Fail status
3. Click on a specific subject for detailed breakdown
4. **Expected Result**: Comprehensive grade history

### 📅 Step 4: Check Class Schedule
1. Click **"My Schedule"** in navigation
2. View current semester schedule:
   - Subject names and codes
   - Days and times
   - Room locations
   - Faculty names
   - Total enrolled units
3. Switch between weekly and daily views
4. **Expected Result**: Complete class schedule layout

### 📝 Step 5: Enroll in New Subjects (Complete Process)
1. Click **"Enrollment"** in navigation
2. **Check Prerequisites**: System shows available subjects for your year level
3. Browse available subjects:
   ```
   IT-104: Mathematics for IT
   - Units: 3
   - Prerequisites: None
   - Available Slots: 25/30
   - Faculty: Prof. Robert Chen
   - Schedule: MWF 2:00-3:00 PM
   ```
4. Click **"Enroll"** button for IT-104
5. **Confirmation Dialog**: Review enrollment details
6. Click **"Confirm Enrollment"**
7. **Expected Result**: Successfully enrolled, slots updated

### 🚫 Step 6: Test Enrollment Restrictions
1. Try to enroll in a subject above your year level (e.g., IT-301)
2. **Expected Result**: Error message about year level restrictions
3. Try to enroll in a subject with missing prerequisites
4. **Expected Result**: Error message about missing prerequisites
5. Try to enroll when slots are full
6. **Expected Result**: Error message about full capacity

### 📊 Step 7: Academic Progress Tracking
1. Click **"Academic Progress"** in navigation
2. View degree completion progress:
   - Completed subjects by year level
   - Remaining requirements
   - GPA trends over time
   - Projected graduation date
3. Review transcript summary
4. **Expected Result**: Complete academic tracking

### 🔍 Step 8: Search and Filter Features
1. In **"Available Subjects"** section:
   - Use search bar to find specific subjects
   - Filter by year level
   - Filter by department
   - Sort by units or availability
2. Test various combinations:
   ```
   Search: "Programming"
   Filter: Year 2
   Sort: By Units (ascending)
   ```
3. **Expected Result**: Refined subject listings

### 📱 Step 9: Profile Management
1. Click **"Profile"** in navigation
2. Update personal information:
   - Contact details
   - Emergency contacts
   - Academic preferences
3. Change password:
   - Enter current password
   - Enter new password
   - Confirm new password
4. Click **"Update Profile"**
5. **Expected Result**: Profile updated successfully

---

## 🎯 Key Demo Highlights

### 🔄 **Cross-Role Integration**
1. **Admin adds student** → **Student can login** → **Faculty sees student in roster**
2. **Faculty enters grades** → **Student sees updated grades** → **Admin sees in reports**
3. **Admin sets enrollment period** → **Student can enroll during period** → **Faculty sees new enrollments**

### 🛡️ **Security Features**
- Role-based access (try accessing wrong portals)
- Session management (logout/login)
- Password security (encrypted storage)

### 📊 **Real-time Updates**
- Enrollment slot counts update immediately
- Grade changes reflect instantly
- System reports update in real-time

### 🎨 **User Experience**
- Responsive design (test on different screen sizes)
- Intuitive navigation
- Clear error messages and confirmations

## 🎪 Demo Tips

1. **Start Fresh**: Clear browser cache between different user logins
2. **Show Errors**: Demonstrate validation by entering invalid data
3. **Highlight Features**: Point out unique features like smart enrollment
4. **Data Flow**: Explain how actions in one portal affect others
5. **Mobile Friendly**: Show responsive design on mobile devices

---

**🎬 Total Demo Time: ~30-45 minutes for complete walkthrough**