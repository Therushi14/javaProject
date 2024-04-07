package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import src.StudentInfo;

public class Faculty extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField divisionField;
    private JTextField departmentField;
    private JButton enterButton;
    private JButton viewFeedbackButton; // New button for viewing feedback

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public Faculty() {
        setTitle("Faculty Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Faculty Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel divisionLabel = new JLabel("Division:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(divisionLabel, gbc);

        divisionField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(divisionField, gbc);

        JLabel departmentLabel = new JLabel("Department:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(departmentLabel, gbc);

        departmentField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(departmentField, gbc);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(enterButton, gbc);

        // Button to view feedback
        viewFeedbackButton = new JButton("View Feedback");
        viewFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FeedbackViewer(); // Open FeedbackViewer page
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(viewFeedbackButton, gbc);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterButton) {
            String name = nameField.getText();
            String division = divisionField.getText();
            String department = departmentField.getText();

            // Insert data into the Faculty table
            insertIntoFacultyTable(name, division, department);

            JOptionPane.showMessageDialog(this, "Data inserted successfully!");

            // Redirect to StudentInfo.java page
            StudentInfo studentInfo = new StudentInfo(division);
            studentInfo.setVisible(true);
            dispose(); // Close the faculty panel after redirection
        }
    }

    // Method to insert data into the Faculty table
    private void insertIntoFacultyTable(String name, String division, String department) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Establishing the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO Faculty (faculty_id, faculty_name, division, department) VALUES (faculty_id_seq.NEXTVAL, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, division);
            statement.setString(3, department);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Closing the database resources
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Faculty faculty = new Faculty();
                faculty.setVisible(true);
            }
        });
    }
}
