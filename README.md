# MotorPH Employee App – Group 18

A Java Swing desktop application for managing MotorPH employee records and computing payroll.

---

## Requirements

- Java JDK 8 or higher
- NetBeans IDE (recommended) or any Java IDE
- `Employee Details.csv` and `Attendance Record.csv` in the project root directory

---

## How to Run

1. Open the project in NetBeans (or compile manually)
2. Ensure both CSV files are in the **project root** (same level as `src/`)
3. Run `MotorPHEmployeeApp.java`

**Manual compile & run:**
```bash
cd A1101-MotorPH-Employee-App-Group-18-master
javac src/MotorPHEmployeeApp.java -d out/
java -cp out/ MotorPHEmployeeApp
```

---

## Login Credentials

| Role           | Username       | Password |
|----------------|----------------|----------|
| Employee       | `employee`     | `12345`  |
| Payroll Staff  | `payroll_staff`| `12345`  |

---

## Features

### Milestone 1 – Core System
- Login screen with role-based access
- Employee self-service portal (view own details)

### Milestone 2 – Record Management, Salary Computation & Data Update

#### Feature 2 – Employee Record Management
- View all employee records in a sortable table
- Displays: Employee #, Name, Position, Status, Basic Salary, Hourly Rate

#### Feature 3 – Salary Computation
- Enter an Employee # to generate a full salary slip
- Computes:
  - **Hours Worked** (from `Attendance Record.csv`, with 1-hour lunch deduction for shifts over 5 hours)
  - **Gross Pay** = Hours Worked × Hourly Rate
  - **SSS** – bracket-based contribution table
  - **PhilHealth** – 2.5% employee share
  - **Pag-IBIG** – 2% of gross, capped at ₱100
  - **Withholding Tax** – BIR TRAIN Law monthly brackets
  - **Net Pay** = Gross Pay − Total Deductions

#### Feature 4 – Update and Delete Records
- **Add Employee** – form with input validation; saves to CSV immediately
- **Update Employee** – load by Employee #, edit fields, save with confirmation
- **Delete Employee** – preview record before deletion, double confirmation required
- All changes are persisted back to `Employee Details.csv`

---

## Project Structure

```
A1101-MotorPH-Employee-App-Group-18-master/
├── src/
│   └── MotorPHEmployeeApp.java   # Main application (all features)
├── Employee Details.csv           # Employee master data
├── Attendance Record.csv          # Daily time records
├── build.xml                      # NetBeans build file
├── manifest.mf
└── nbproject/                     # NetBeans project config
```

---

## Group 18 – MO-IT101 / A1101
