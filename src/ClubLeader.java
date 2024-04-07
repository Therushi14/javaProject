package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ClubLeader extends JFrame implements ActionListener {
    private JTextField nameField;
    private JTextField divisionField;
    private JTextField departmentField;
    private JTextField yearField;
    private JTextField clubNameField;
    private JButton enterButton;

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public ClubLeader() {
        setTitle("Club Leader Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Club Leader Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(nameField, gbc);

        JLabel divisionLabel = new JLabel("Division:");
        divisionLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(divisionLabel, gbc);

        divisionField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(divisionField, gbc);

        JLabel departmentLabel = new JLabel("Department:");
        departmentLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(departmentLabel, gbc);

        departmentField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(departmentField, gbc);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(yearLabel, gbc);

        yearField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(yearField, gbc);

        JLabel clubNameLabel = new JLabel("Club Name:");
        clubNameLabel.setForeground(new Color(0, 102, 204)); // Dark blue color
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(clubNameLabel, gbc);

        clubNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(clubNameField, gbc);

        enterButton = new JButton("Enter");
        enterButton.addActionListener(this);
        enterButton.setBackground(new Color(0, 102, 204)); // Dark blue color
        enterButton.setForeground(Color.WHITE); // White text color
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(enterButton, gbc);

        add(panel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterButton) {
            String name = nameField.getText();
            String division = divisionField.getText();
            String department = departmentField.getText();
            String year = yearField.getText();
            String clubName = clubNameField.getText();

            // Insert data into the ClubLeader table
            insertIntoClubLeaderTable(name, division, department, year, clubName);

            JOptionPane.showMessageDialog(this, "Data inserted successfully!");

            // Open the Club page
            Club clubPage = new Club(clubName);
            clubPage.setVisible(true);

            // Close the current frame
            dispose();
        }
    }

    // Method to insert data into the ClubLeader table
    private void insertIntoClubLeaderTable(String name, String division, String department, String year, String clubName) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Establishing the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "INSERT INTO ClubLeader (name, division, department, year, club_name) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, division);
            statement.setString(3, department);
            statement.setString(4, year);
            statement.setString(5, clubName);
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
                ClubLeader clubLeader = new ClubLeader();
                clubLeader.setVisible(true);
            }
        });
    }
}
