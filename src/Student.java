package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Student extends JFrame implements ActionListener {
    private final JTextField prnField;
    private final JButton submitButton;

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";



    public Student() {
        //this.setGoalsButton = setGoalsButton;
        setTitle("Enter the PRN");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel prnLabel = new JLabel("PRN:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(prnLabel, gbc);

        prnField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(prnField, gbc);





        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        JLabel titleLabel = new JLabel("<html>First Time Logging In as Student then <u>Click here</u></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Label clicked"); // Debugging message
                // Redirect to StudentPanel.java
                StudentPanel studentPanel = new StudentPanel();
                studentPanel.setVisible(true);
                dispose(); // Close current frame
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            String prn = prnField.getText();

            // Check if the PRN exists in the Student table
            boolean prnExists = checkPRNExists(prn);

            if (prnExists) {
                JOptionPane.showMessageDialog(this, "PRN exists in the database. Redirecting to student profile...");
                // Redirect to DisplayStudent.java passing the PRN
                DisplayStudent displayStudent = new DisplayStudent(prn);
                displayStudent.setVisible(true);
                dispose(); // Close the current frame
            } else {
                JOptionPane.showMessageDialog(this, "PRN does not exist in the database. Please try again.");
            }
        }
    }

    // Method to check if PRN exists in the Student table
    private boolean checkPRNExists(String prn) {
        boolean prnExists = false;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // Establishing the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT * FROM Student WHERE prn_no = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, prn);
            resultSet = statement.executeQuery();
            prnExists = resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Closing the database resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
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
        return prnExists;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Student student = new Student();
                student.setVisible(true);
            }
        });
    }
}