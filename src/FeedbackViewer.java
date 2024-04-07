package src;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FeedbackViewer extends JFrame {
    private JTextArea feedbackTextArea;

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public FeedbackViewer() {
        setTitle("Feedback Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        feedbackTextArea = new JTextArea();
        feedbackTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        fetchFeedback(); // Fetch feedback when the page is created
        setVisible(true);
    }

    private void fetchFeedback() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT feedback_text FROM feedback";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            StringBuilder feedbackText = new StringBuilder();
            while (rs.next()) {
                String feedback = rs.getString("feedback_text");
                feedbackText.append(feedback).append("\n");
            }
            feedbackTextArea.setText(feedbackText.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching feedback: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FeedbackViewer::new);
    }
}

