package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentPanel extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField prnField;
    private JTextField classField;
    private JTextField divisionField;
    private JTextField skillsField;
    private JTextField achievementsField;
    private JButton submitButton;

    // Database Connection Details for Oracle
    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public StudentPanel() {
        setTitle("Student Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Student Information");
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

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel prnLabel = new JLabel("PRN:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(prnLabel, gbc);

        prnField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(prnField, gbc);

        JLabel classLabel = new JLabel("Class:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(classLabel, gbc);

        classField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(classField, gbc);

        JLabel divisionLabel = new JLabel("Division:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(divisionLabel, gbc);

        divisionField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(divisionField, gbc);

        JLabel skillsLabel = new JLabel("Skills:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(skillsLabel, gbc);

        skillsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(skillsField, gbc);

        JLabel achievementsLabel = new JLabel("Achievements:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(achievementsLabel, gbc);

        achievementsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(achievementsField, gbc);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String name = nameField.getText();
            String prn = prnField.getText();
            String classValue = classField.getText();
            String division = divisionField.getText();
            String skills = skillsField.getText();
            String achievements = achievementsField.getText();

            // Insert data into the Student table
            insertIntoStudentTable(name, prn, classValue, division, skills, achievements);

            JOptionPane.showMessageDialog(this, "Data inserted successfully!");

            // Clear fields after submission
            clearFields();
        }
    }

    // Method to insert data into the Student table
    private void insertIntoStudentTable(String name, String prn, String classValue, String division, String skills, String achievements) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Establishing the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO Student (name, prn_no, class, division, skills, achievements) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, prn);
            statement.setString(3, classValue);
            statement.setString(4, division);
            statement.setString(5, skills);
            statement.setString(6, achievements);
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

    // Method to clear all input fields
    private void clearFields() {
        nameField.setText("");
        prnField.setText("");
        classField.setText("");
        divisionField.setText("");
        skillsField.setText("");
        achievementsField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StudentPanel studentPanel = new StudentPanel();
                studentPanel.setVisible(true);
            }
        });
    }
}