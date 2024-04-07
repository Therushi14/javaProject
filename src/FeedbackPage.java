package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FeedbackPage extends JFrame {

    private JLabel titleLabel;
    private JTextArea feedbackArea;
    private JButton submitButton;

    public FeedbackPage() {
        super("Feedback Form");

        // Title label
        titleLabel = new JLabel("Please share your feedback:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        // Feedback text area
        feedbackArea = new JTextArea(10, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String feedback = feedbackArea.getText();

                try (Connection conn = getConnection()) {
                    String sql = "INSERT INTO feedback (feedback_text) VALUES (?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, feedback);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(FeedbackPage.this, "Thank you for your feedback!");
                    feedbackArea.setText("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(FeedbackPage.this, "Error saving feedback: " + ex.getMessage());
                }
            }
        });

        // Layout using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBackground(new Color(240, 240, 240));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 15, 10, 15); // Padding
        panel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 15, 15, 15);
        panel.add(submitButton, gbc);

        add(panel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        String dbURL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
        String username = "system";
        String password = "root";
        return DriverManager.getConnection(dbURL, username, password);
    }

    public static void main(String[] args) {
        new FeedbackPage();
    }
}
