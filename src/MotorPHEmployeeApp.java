import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class MotorPHEmployeeApp extends JFrame {

    // ============================
    // DATA
    // ============================

    static Map<String, String[]> employees = new LinkedHashMap<>();
    static java.util.List<String[]> attendance = new ArrayList<>();

    static String[] empHeaders;
    static String[] attHeaders;

    static final String EMP_FILE = "Employee Details.csv";
    static final String ATT_FILE = "Attendance Record.csv";

    JTextField usernameField;
    JPasswordField passwordField;

    // ============================
    // MAIN
    // ============================

    public static void main(String[] args) {
        try {
            loadEmployees(EMP_FILE);
            loadAttendance(ATT_FILE);
            SwingUtilities.invokeLater(MotorPHEmployeeApp::new);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error Loading CSV Files\n" + e.getMessage());
        }
    }

    // ============================
    // LOGIN FRAME
    // ============================

    public MotorPHEmployeeApp() {
        setTitle("MotorPH Employee System - Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MotorPH Employee System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(30, 60, 120));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(30, 60, 120));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        JLabel hint = new JLabel("Roles: employee | payroll_staff  (password: 12345)", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.ITALIC, 10));
        hint.setForeground(Color.GRAY);
        gbc.gridy = 4;
        panel.add(hint, gbc);

        add(panel);
        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        setVisible(true);
    }

    // ============================
    // LOGIN FUNCTION
    // ============================

    void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (username.equalsIgnoreCase("employee") && password.equals("12345")) {
            dispose();
            new EmployeeDashboard();
        } else if ((username.equalsIgnoreCase("payroll_staff") || username.equalsIgnoreCase("payroll staff"))
                && password.equals("12345")) {
            dispose();
            new PayrollDashboard();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    // ============================
    // EMPLOYEE DASHBOARD
    // ============================

    static class EmployeeDashboard extends JFrame {

        JTextField empField;
        JTextArea area;

        EmployeeDashboard() {
            setTitle("Employee Dashboard - View My Records");
            setSize(550, 450);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            // Header
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            topPanel.setBackground(new Color(30, 60, 120));
            JLabel titleLbl = new JLabel("Employee Self-Service Portal");
            titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
            titleLbl.setForeground(Color.WHITE);
            topPanel.add(titleLbl);
            add(topPanel, BorderLayout.NORTH);

            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            searchPanel.setBackground(new Color(230, 235, 245));
            searchPanel.add(new JLabel("Employee Number:"));
            empField = new JTextField(12);
            searchPanel.add(empField);
            JButton searchButton = new JButton("Search");
            styleButton(searchButton, new Color(30, 60, 120));
            searchPanel.add(searchButton);
            add(searchPanel, BorderLayout.CENTER);

            area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(new JScrollPane(area), BorderLayout.SOUTH);
            ((JScrollPane) getContentPane().getComponent(2)).setPreferredSize(new Dimension(550, 280));

            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(245, 247, 250));
            JButton logoutButton = new JButton("Logout");
            styleButton(logoutButton, new Color(180, 40, 40));
            bottomPanel.add(logoutButton);

            // Restructure layout
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(searchPanel, BorderLayout.NORTH);
            centerPanel.add(new JScrollPane(area), BorderLayout.CENTER);
            centerPanel.add(bottomPanel, BorderLayout.SOUTH);

            getContentPane().removeAll();
            add(topPanel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);

            searchButton.addActionListener(e -> showEmployee());
            empField.addActionListener(e -> showEmployee());
            logoutButton.addActionListener(e -> { dispose(); new MotorPHEmployeeApp(); });

            setVisible(true);
        }

        void showEmployee() {
            String empNum = empField.getText().trim();
            if (empNum.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an Employee Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!employees.containsKey(empNum)) {
                JOptionPane.showMessageDialog(this, "Employee #" + empNum + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] emp = employees.get(empNum);
            String firstName = emp[getIndex(empHeaders, "First Name")];
            String lastName  = emp[getIndex(empHeaders, "Last Name")];
            String birthday  = emp[getIndex(empHeaders, "Birthday")];
            String position  = emp[getIndex(empHeaders, "Position")];
            String status    = emp[getIndex(empHeaders, "Status")];
            String basicSalary = emp[getIndex(empHeaders, "Basic Salary")];
            String hourlyRate  = emp[getIndex(empHeaders, "Hourly Rate")];

            area.setText(
                "============================================\n" +
                "         EMPLOYEE INFORMATION              \n" +
                "============================================\n" +
                String.format("  Employee #  : %s%n", empNum) +
                String.format("  Name        : %s %s%n", firstName, lastName) +
                String.format("  Birthday    : %s%n", birthday) +
                String.format("  Position    : %s%n", position) +
                String.format("  Status      : %s%n", status) +
                String.format("  Basic Salary: %s%n", basicSalary) +
                String.format("  Hourly Rate : %s%n", hourlyRate) +
                "============================================\n"
            );
        }
    }

    // ============================
    // PAYROLL DASHBOARD
    // ============================

    static class PayrollDashboard extends JFrame {

        DefaultTableModel tableModel;
        JTable table;

        PayrollDashboard() {
            setTitle("Payroll Staff Dashboard");
            setSize(1000, 650);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Header
            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            header.setBackground(new Color(30, 60, 120));
            JLabel titleLbl = new JLabel("MotorPH Payroll Management System");
            titleLbl.setFont(new Font("Arial", Font.BOLD, 18));
            titleLbl.setForeground(Color.WHITE);
            header.add(titleLbl);
            add(header, BorderLayout.NORTH);

            // Tabs
            JTabbedPane tabs = new JTabbedPane();
            tabs.setFont(new Font("Arial", Font.BOLD, 13));

            tabs.addTab("Employee Records", buildRecordsTab());
            tabs.addTab("Add Employee",     buildAddTab());
            tabs.addTab("Update Employee",  buildUpdateTab());
            tabs.addTab("Delete Employee",  buildDeleteTab());
            tabs.addTab("Salary Computation", buildPayrollTab());

            add(tabs, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setBackground(new Color(245, 247, 250));
            JButton logoutButton = new JButton("Logout");
            styleButton(logoutButton, new Color(180, 40, 40));
            logoutButton.addActionListener(e -> { dispose(); new MotorPHEmployeeApp(); });
            footer.add(logoutButton);
            add(footer, BorderLayout.SOUTH);

            setVisible(true);
        }

        // ---- TAB 1: RECORDS ----

        JPanel buildRecordsTab() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            String[] cols = {"Emp #", "Last Name", "First Name", "Position", "Status", "Basic Salary", "Hourly Rate"};
            tableModel = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(tableModel);
            table.setRowHeight(22);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setBackground(new Color(30, 60, 120));
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionBackground(new Color(180, 210, 255));
            table.setAutoCreateRowSorter(true);

            refreshTable();

            JScrollPane scroll = new JScrollPane(table);
            panel.add(scroll, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshBtn = new JButton("Refresh");
            styleButton(refreshBtn, new Color(30, 60, 120));
            refreshBtn.addActionListener(e -> refreshTable());
            btnPanel.add(refreshBtn);

            JLabel countLbl = new JLabel();
            countLbl.setFont(new Font("Arial", Font.ITALIC, 12));
            btnPanel.add(countLbl);

            panel.add(btnPanel, BorderLayout.SOUTH);

            refreshBtn.addActionListener(e -> countLbl.setText("  Total employees: " + employees.size()));
            countLbl.setText("  Total employees: " + employees.size());

            return panel;
        }

        void refreshTable() {
            if (tableModel == null) return;
            tableModel.setRowCount(0);
            for (Map.Entry<String, String[]> entry : employees.entrySet()) {
                String[] emp = entry.getValue();
                tableModel.addRow(new Object[]{
                    entry.getKey(),
                    safeGet(emp, "Last Name"),
                    safeGet(emp, "First Name"),
                    safeGet(emp, "Position"),
                    safeGet(emp, "Status"),
                    safeGet(emp, "Basic Salary"),
                    safeGet(emp, "Hourly Rate")
                });
            }
        }

        // ---- TAB 2: ADD EMPLOYEE ----

        JPanel buildAddTab() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

            JLabel heading = new JLabel("Add New Employee Record");
            heading.setFont(new Font("Arial", Font.BOLD, 16));
            heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panel.add(heading, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            String[] labels = {
                "Employee #", "Last Name", "First Name", "Birthday (MM-DD-YY)",
                "Address", "Phone Number", "Position", "Status",
                "Basic Salary", "Hourly Rate"
            };
            JTextField[] fields = new JTextField[labels.length];

            for (int i = 0; i < labels.length; i++) {
                gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.25;
                form.add(new JLabel(labels[i] + ":"), gbc);
                fields[i] = new JTextField(20);
                gbc.gridx = 1; gbc.weightx = 0.75;
                form.add(fields[i], gbc);
            }

            JScrollPane formScroll = new JScrollPane(form);
            panel.add(formScroll, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton addBtn = new JButton("Add Employee");
            styleButton(addBtn, new Color(30, 120, 60));
            JButton clearBtn = new JButton("Clear");
            styleButton(clearBtn, new Color(100, 100, 100));

            clearBtn.addActionListener(e -> { for (JTextField f : fields) f.setText(""); });

            addBtn.addActionListener(e -> {
                // Validate required fields
                if (fields[0].getText().trim().isEmpty() ||
                    fields[1].getText().trim().isEmpty() ||
                    fields[2].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                        "Employee #, Last Name, and First Name are required.",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String empNum = fields[0].getText().trim();
                if (employees.containsKey(empNum)) {
                    JOptionPane.showMessageDialog(panel,
                        "Employee #" + empNum + " already exists.",
                        "Duplicate Record", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate hourly rate if provided
                String rateStr = fields[9].getText().trim();
                if (!rateStr.isEmpty()) {
                    try { Double.parseDouble(rateStr.replace(",", "")); }
                    catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel, "Hourly Rate must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // Build new row matching empHeaders
                String[] newRow = new String[empHeaders.length];
                Arrays.fill(newRow, "");
                setField(newRow, "Employee #",    fields[0].getText().trim());
                setField(newRow, "Last Name",     fields[1].getText().trim());
                setField(newRow, "First Name",    fields[2].getText().trim());
                setField(newRow, "Birthday",      fields[3].getText().trim());
                setField(newRow, "Address",       fields[4].getText().trim());
                setField(newRow, "Phone Number",  fields[5].getText().trim());
                setField(newRow, "Position",      fields[6].getText().trim());
                setField(newRow, "Status",        fields[7].getText().trim());
                setField(newRow, "Basic Salary",  fields[8].getText().trim());
                setField(newRow, "Hourly Rate",   fields[9].getText().trim());

                employees.put(empNum, newRow);

                try {
                    saveEmployees(EMP_FILE);
                    refreshTable();
                    for (JTextField f : fields) f.setText("");
                    JOptionPane.showMessageDialog(panel,
                        "Employee #" + empNum + " added successfully and saved to CSV.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving CSV: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnPanel.add(addBtn);
            btnPanel.add(clearBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);
            return panel;
        }

        // ---- TAB 3: UPDATE EMPLOYEE ----

        JPanel buildUpdateTab() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

            JLabel heading = new JLabel("Update Employee Record");
            heading.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(heading, BorderLayout.NORTH);

            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            searchPanel.add(new JLabel("Employee #:"));
            JTextField searchField = new JTextField(12);
            searchPanel.add(searchField);
            JButton loadBtn = new JButton("Load");
            styleButton(loadBtn, new Color(30, 60, 120));
            searchPanel.add(loadBtn);

            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            String[] editLabels = {
                "Last Name", "First Name", "Birthday (MM-DD-YY)",
                "Address", "Phone Number", "Position", "Status",
                "Basic Salary", "Hourly Rate"
            };
            String[] editKeys = {
                "Last Name", "First Name", "Birthday",
                "Address", "Phone Number", "Position", "Status",
                "Basic Salary", "Hourly Rate"
            };
            JTextField[] editFields = new JTextField[editLabels.length];

            for (int i = 0; i < editLabels.length; i++) {
                gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.25;
                formPanel.add(new JLabel(editLabels[i] + ":"), gbc);
                editFields[i] = new JTextField(20);
                editFields[i].setEnabled(false);
                gbc.gridx = 1; gbc.weightx = 0.75;
                formPanel.add(editFields[i], gbc);
            }

            JPanel center = new JPanel(new BorderLayout());
            center.add(searchPanel, BorderLayout.NORTH);
            center.add(new JScrollPane(formPanel), BorderLayout.CENTER);
            panel.add(center, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton saveBtn = new JButton("Save Changes");
            styleButton(saveBtn, new Color(30, 120, 60));
            saveBtn.setEnabled(false);
            btnPanel.add(saveBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);

            final String[] currentEmpNum = {null};

            loadBtn.addActionListener(e -> {
                String empNum = searchField.getText().trim();
                if (empNum.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter an Employee Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!employees.containsKey(empNum)) {
                    JOptionPane.showMessageDialog(panel, "Employee #" + empNum + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String[] emp = employees.get(empNum);
                currentEmpNum[0] = empNum;
                for (int i = 0; i < editKeys.length; i++) {
                    editFields[i].setText(safeGet(emp, editKeys[i]));
                    editFields[i].setEnabled(true);
                }
                saveBtn.setEnabled(true);
            });

            saveBtn.addActionListener(e -> {
                if (currentEmpNum[0] == null) return;

                String rateStr = editFields[8].getText().trim();
                if (!rateStr.isEmpty()) {
                    try { Double.parseDouble(rateStr.replace(",", "")); }
                    catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(panel, "Hourly Rate must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Save changes for Employee #" + currentEmpNum[0] + "?",
                    "Confirm Update", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                String[] emp = employees.get(currentEmpNum[0]);
                for (int i = 0; i < editKeys.length; i++) {
                    setField(emp, editKeys[i], editFields[i].getText().trim());
                }
                employees.put(currentEmpNum[0], emp);

                try {
                    saveEmployees(EMP_FILE);
                    refreshTable();
                    JOptionPane.showMessageDialog(panel,
                        "Employee #" + currentEmpNum[0] + " updated successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving CSV: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return panel;
        }

        // ---- TAB 4: DELETE EMPLOYEE ----

        JPanel buildDeleteTab() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            JLabel heading = new JLabel("Delete Employee Record");
            heading.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(heading, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            form.add(new JLabel("Employee #:"), gbc);
            JTextField delField = new JTextField(15);
            gbc.gridx = 1;
            form.add(delField, gbc);

            JButton lookupBtn = new JButton("Lookup");
            styleButton(lookupBtn, new Color(30, 60, 120));
            gbc.gridx = 2;
            form.add(lookupBtn, gbc);

            JTextArea preview = new JTextArea(6, 40);
            preview.setEditable(false);
            preview.setFont(new Font("Monospaced", Font.PLAIN, 13));
            preview.setBackground(new Color(255, 248, 248));
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
            form.add(new JScrollPane(preview), gbc);

            panel.add(form, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            JButton deleteBtn = new JButton("Delete Employee");
            styleButton(deleteBtn, new Color(180, 40, 40));
            deleteBtn.setEnabled(false);
            btnPanel.add(deleteBtn);
            panel.add(btnPanel, BorderLayout.SOUTH);

            lookupBtn.addActionListener(e -> {
                String empNum = delField.getText().trim();
                if (empNum.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please enter an Employee Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!employees.containsKey(empNum)) {
                    preview.setText("Employee #" + empNum + " not found.");
                    deleteBtn.setEnabled(false);
                    return;
                }
                String[] emp = employees.get(empNum);
                preview.setText(
                    "Employee #  : " + empNum + "\n" +
                    "Name        : " + safeGet(emp, "First Name") + " " + safeGet(emp, "Last Name") + "\n" +
                    "Position    : " + safeGet(emp, "Position") + "\n" +
                    "Status      : " + safeGet(emp, "Status") + "\n" +
                    "Basic Salary: " + safeGet(emp, "Basic Salary") + "\n" +
                    "\n⚠ This action cannot be undone."
                );
                deleteBtn.setEnabled(true);
            });

            deleteBtn.addActionListener(e -> {
                String empNum = delField.getText().trim();
                if (!employees.containsKey(empNum)) return;

                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Permanently delete Employee #" + empNum + "?\nThis cannot be undone.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) return;

                employees.remove(empNum);
                try {
                    saveEmployees(EMP_FILE);
                    refreshTable();
                    delField.setText("");
                    preview.setText("");
                    deleteBtn.setEnabled(false);
                    JOptionPane.showMessageDialog(panel,
                        "Employee #" + empNum + " deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error saving CSV: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            return panel;
        }

        // ---- TAB 5: SALARY COMPUTATION ----

        JPanel buildPayrollTab() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JLabel heading = new JLabel("Salary Computation");
            heading.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(heading, BorderLayout.NORTH);

            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            searchPanel.add(new JLabel("Employee #:"));
            JTextField empField = new JTextField(12);
            searchPanel.add(empField);
            JButton genBtn = new JButton("Compute Salary");
            styleButton(genBtn, new Color(30, 60, 120));
            searchPanel.add(genBtn);
            panel.add(searchPanel, BorderLayout.NORTH);

            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel.add(new JScrollPane(area), BorderLayout.CENTER);

            genBtn.addActionListener(e -> computeSalary(empField, area, panel));
            empField.addActionListener(e -> computeSalary(empField, area, panel));

            return panel;
        }

        void computeSalary(JTextField empField, JTextArea area, JComponent parent) {
            String empNum = empField.getText().trim();
            if (empNum.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Please enter an Employee Number.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!employees.containsKey(empNum)) {
                JOptionPane.showMessageDialog(parent, "Employee #" + empNum + " not found.", "Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] emp = employees.get(empNum);
            String name = safeGet(emp, "First Name") + " " + safeGet(emp, "Last Name");

            String rateRaw = safeGet(emp, "Hourly Rate").replace(",", "").replace("\"", "").trim();
            double hourlyRate;
            try {
                hourlyRate = Double.parseDouble(rateRaw);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Hourly Rate is missing or invalid for this employee.", "Data Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int empIndex    = getIndexFlexible(attHeaders, "Employee #");
            int loginIndex  = getIndexFlexible(attHeaders, "Log In", "Time In");
            int logoutIndex = getIndexFlexible(attHeaders, "Log Out", "Time Out");

            double totalHours = 0;
            int daysPresent = 0;

            for (String[] row : attendance) {
                if (row.length > Math.max(empIndex, Math.max(loginIndex, logoutIndex))
                        && row[empIndex].trim().equals(empNum)) {
                    try {
                        double login  = parseTime(row[loginIndex].trim());
                        double logout = parseTime(row[logoutIndex].trim());
                        double worked = logout - login;
                        if (worked > 5) worked -= 1; // 1-hour lunch break deduction
                        if (worked > 0) { totalHours += worked; daysPresent++; }
                    } catch (Exception ignored) {}
                }
            }

            double gross = totalHours * hourlyRate;

            // Government-mandated deductions
            double sss        = computeSSS(gross);
            double philhealth = gross * 0.025;  // 2.5% employee share
            double pagibig    = Math.min(gross * 0.02, 100); // max 100
            double taxableIncome = gross - sss - philhealth - pagibig;
            double tax        = computeWithholdingTax(taxableIncome);

            double totalDeductions = sss + philhealth + pagibig + tax;
            double net = gross - totalDeductions;

            area.setText(
                "============================================\n" +
                "         SALARY COMPUTATION SLIP           \n" +
                "============================================\n" +
                String.format("  Employee #    : %s%n", empNum) +
                String.format("  Name          : %s%n", name) +
                String.format("  Hourly Rate   : PHP %.2f%n", hourlyRate) +
                "--------------------------------------------\n" +
                String.format("  Days Present  : %d%n", daysPresent) +
                String.format("  Hours Worked  : %.2f hrs%n", totalHours) +
                "--------------------------------------------\n" +
                String.format("  GROSS PAY     : PHP %,.2f%n", gross) +
                "--------------------------------------------\n" +
                "  DEDUCTIONS:%n" +
                String.format("    SSS          : PHP %,.2f%n", sss) +
                String.format("    PhilHealth   : PHP %,.2f%n", philhealth) +
                String.format("    Pag-IBIG     : PHP %,.2f%n", pagibig) +
                String.format("    Withholding Tax: PHP %,.2f%n", tax) +
                String.format("  TOTAL DEDUCTIONS: PHP %,.2f%n", totalDeductions) +
                "--------------------------------------------\n" +
                String.format("  NET PAY       : PHP %,.2f%n", net) +
                "============================================\n"
            );
        }
    }

    // ============================
    // SALARY COMPUTATION HELPERS
    // ============================

    static double computeSSS(double monthlyGross) {
        // Simplified SSS table (monthly basis)
        if (monthlyGross < 3250)       return 135.00;
        else if (monthlyGross < 3750)  return 157.50;
        else if (monthlyGross < 4250)  return 180.00;
        else if (monthlyGross < 4750)  return 202.50;
        else if (monthlyGross < 5250)  return 225.00;
        else if (monthlyGross < 5750)  return 247.50;
        else if (monthlyGross < 6250)  return 270.00;
        else if (monthlyGross < 6750)  return 292.50;
        else if (monthlyGross < 7250)  return 315.00;
        else if (monthlyGross < 7750)  return 337.50;
        else if (monthlyGross < 8250)  return 360.00;
        else if (monthlyGross < 8750)  return 382.50;
        else if (monthlyGross < 9250)  return 405.00;
        else if (monthlyGross < 9750)  return 427.50;
        else if (monthlyGross < 10250) return 450.00;
        else if (monthlyGross < 10750) return 472.50;
        else if (monthlyGross < 11250) return 495.00;
        else if (monthlyGross < 11750) return 517.50;
        else if (monthlyGross < 12250) return 540.00;
        else if (monthlyGross < 12750) return 562.50;
        else if (monthlyGross < 13250) return 585.00;
        else if (monthlyGross < 13750) return 607.50;
        else if (monthlyGross < 14250) return 630.00;
        else if (monthlyGross < 14750) return 652.50;
        else                           return 675.00;
    }

    static double computeWithholdingTax(double taxable) {
        // BIR monthly withholding tax table (Train Law)
        if (taxable <= 20833)          return 0;
        else if (taxable <= 33333)     return (taxable - 20833) * 0.20;
        else if (taxable <= 66667)     return 2500 + (taxable - 33333) * 0.25;
        else if (taxable <= 166667)    return 10833 + (taxable - 66667) * 0.30;
        else if (taxable <= 666667)    return 40833.33 + (taxable - 166667) * 0.32;
        else                           return 200833.33 + (taxable - 666667) * 0.35;
    }

    // ============================
    // CSV FUNCTIONS
    // ============================

    static void loadEmployees(String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        empHeaders = splitCSV(br.readLine());
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;
            String[] row = splitCSV(line);
            int idx = getIndex(empHeaders, "Employee #");
            if (idx >= 0 && idx < row.length) {
                employees.put(row[idx].trim(), row);
            }
        }
        br.close();
    }

    static void loadAttendance(String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        attHeaders = splitCSV(br.readLine());
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) attendance.add(splitCSV(line));
        }
        br.close();
    }

    static void saveEmployees(String file) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.println(String.join(",", empHeaders));
        for (String[] row : employees.values()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < row.length; i++) {
                String val = row[i] == null ? "" : row[i];
                // Quote fields that contain commas
                if (val.contains(",")) val = "\"" + val + "\"";
                if (i > 0) sb.append(",");
                sb.append(val);
            }
            pw.println(sb.toString());
        }
        pw.close();
    }

    // ============================
    // UTILITIES
    // ============================

    static String[] splitCSV(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    static int getIndex(String[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static int getIndexFlexible(String[] headers, String... names) {
        for (int i = 0; i < headers.length; i++) {
            String current = headers[i].toLowerCase().replace(" ", "").replace("#", "");
            for (String n : names) {
                if (current.equals(n.toLowerCase().replace(" ", "").replace("#", ""))) return i;
            }
        }
        return -1;
    }

    static String safeGet(String[] row, String key) {
        int idx = getIndex(empHeaders, key);
        if (idx < 0 || idx >= row.length) return "";
        return row[idx].trim();
    }

    static void setField(String[] row, String key, String value) {
        int idx = getIndex(empHeaders, key);
        if (idx >= 0 && idx < row.length) row[idx] = value;
    }

    static double parseTime(String time) {
        String[] p = time.trim().split(":");
        return Integer.parseInt(p[0]) + Integer.parseInt(p[1]) / 60.0;
    }

    static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }
}
