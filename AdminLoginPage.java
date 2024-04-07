import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminLoginPage extends JFrame {
    private JTextField nameField;
    private JTextField organizationField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;
    private boolean signupMode = false;
    private Connection connection;

    public AdminLoginPage() {
        setTitle("Admin Credentials");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize database connection
        try {
            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
        } catch (SQLException e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name:");
        JLabel organizationLabel = new JLabel("Organization Name:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        nameField = new JTextField(20);
        organizationField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");

        panel.add(nameLabel, gbc);
        gbc.gridx++;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(organizationLabel, gbc);
        gbc.gridx++;
        panel.add(organizationField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(emailLabel, gbc);
        gbc.gridx++;
        panel.add(emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx++;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        panel.add(loginButton, gbc);
        gbc.gridx++;
        panel.add(signupButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (validateUser(email, password)) {
                    // Login successful, open next page
                    JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Open next page here (AdminDashboard)
//                    new AdminDash();
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleSignupMode();
            }
        });

        add(panel);
        setBounds(460, 200, 600, 400);
        setVisible(true);
    }

    private void toggleSignupMode() {
        if (signupMode) {
            setTitle("Admin Login");
            signupButton.setText("Signup");
        } else {
            setTitle("Admin Signup");
            signupButton.setText("Back to Login");
        }
        signupMode = !signupMode;
    }

    private boolean validateUser(String email, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Admin WHERE email = ? AND password = ?");
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If a row is returned, user exists and credentials are correct
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new AdminLoginPage();
    }
}