package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayStudent extends JFrame {
    private final JLabel nameLabel;
    private JLabel prnLabel;
    private JLabel classLabel;
    private JLabel divisionLabel;
    private JLabel skillsLabel;
    private JLabel achievementsLabel;
    private JTextArea summaryTextArea;
    private JButton saveSummaryButton;
    private JButton setGoalsButton;
    private JButton feedbackButton; // Moved button declaration here
    private JButton viewClubImagesButton; // Added button for viewing club images

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public DisplayStudent(String prn) {
        setTitle("Student Profile");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(6, 2, 5, 5));
        profilePanel.setBackground(new Color(220, 255, 220)); // Faint Green color

        nameLabel = new JLabel("Name:");
        profilePanel.add(nameLabel);

        prnLabel = new JLabel("PRN:");
        profilePanel.add(prnLabel);

        classLabel = new JLabel("Class:");
        profilePanel.add(classLabel);

        divisionLabel = new JLabel("Division:");
        profilePanel.add(divisionLabel);

        skillsLabel = new JLabel("Skills:");
        profilePanel.add(skillsLabel);

        achievementsLabel = new JLabel("Achievements:");
        profilePanel.add(achievementsLabel);

        // Fetch student data from the database using PRN
        fetchStudentData(prn);

        mainPanel.add(profilePanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Today's Summary"));

        summaryTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(summaryTextArea);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        saveSummaryButton = new JButton("Save Summary");
        saveSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSummary(prn);
            }
        });
        summaryPanel.add(saveSummaryButton, BorderLayout.SOUTH);

        feedbackButton = new JButton("Feedback"); // Button initialization
        feedbackButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Redirect to FeedbackPage
                FeedbackPage feedbackPage = new FeedbackPage();
                feedbackPage.setVisible(true);
                dispose(); // Close current frame
            }
        });
        mainPanel.add(feedbackButton, BorderLayout.SOUTH); // Add button to mainPanel


        setGoalsButton = new JButton("Set Goals");
        setGoalsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Redirect to FeedbackPage
               SetGoalsPage sg = new SetGoalsPage(prn);
               sg.setVisible(true);
               dispose();

            }
        });
        mainPanel.add(setGoalsButton, BorderLayout.SOUTH);

        viewClubImagesButton = new JButton("View Club "); // Button for viewing club images
        viewClubImagesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchClubImages();
            }
        });
        mainPanel.add(viewClubImagesButton, BorderLayout.SOUTH); // Add button to mainPanel

        mainPanel.add(summaryPanel, BorderLayout.CENTER);

        add(mainPanel);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3)); // Create a panel for buttons with GridLayout
        buttonPanel.add(setGoalsButton);
        buttonPanel.add(feedbackButton);
        buttonPanel.add(viewClubImagesButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // Method to fetch student data from the database using PRN
    private void fetchStudentData(String prn) {
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
            if (resultSet.next()) {
                nameLabel.setText("Name: " + resultSet.getString("name"));
                prnLabel.setText("PRN: " + resultSet.getString("prn_no"));
                classLabel.setText("Class: " + resultSet.getString("class"));
                divisionLabel.setText("Division: " + resultSet.getString("division"));
                skillsLabel.setText("Skills: " + resultSet.getString("skills"));
                achievementsLabel.setText("Achievements: " + resultSet.getString("achievements"));
            } else {
                JOptionPane.showMessageDialog(this, "Student data not found in the database.");
            }
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
    }

    // Method to save summary to the database
    private void saveSummary(String prn) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "INSERT INTO Summary (PRN_NO, SUMMARY_TEXT) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, prn);
            statement.setString(2, summaryTextArea.getText());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Summary saved successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save summary.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
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


    private void fetchClubImages() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT image FROM ClubImages";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            List<ImageIcon> images = new ArrayList<>();
            while (resultSet.next()) {
                Blob blob = resultSet.getBlob("image");
                byte[] imageBytes = blob.getBytes(1, (int) blob.length());
                ImageIcon imageIcon = new ImageIcon(imageBytes);
                images.add(imageIcon);
            }

            if (!images.isEmpty()) {
                displayImages(images);
            } else {
                JOptionPane.showMessageDialog(this, "No club images found in the database.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void displayImages(List<ImageIcon> images) {
        JFrame imageFrame = new JFrame("Club Images");
        imageFrame.setSize(600, 400);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setLocationRelativeTo(null);

        JPanel imagePanel = new JPanel(new GridLayout(0, 3, 5, 5));
        for (ImageIcon image : images) {
            JLabel imageLabel = new JLabel(image);
            imagePanel.add(imageLabel);
        }

        JScrollPane scrollPane = new JScrollPane(imagePanel);
        imageFrame.add(scrollPane);

        imageFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Example usage:
                DisplayStudent displayStudent = new DisplayStudent("1234567890"); // Pass the PRN
                displayStudent.setVisible(true);
            }
        });
    }
}