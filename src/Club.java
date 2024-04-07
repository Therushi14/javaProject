package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Club extends JFrame {
    private JLabel clubNameLabel;
    private JLabel imageLabel;
    private JButton uploadButton;

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public Club(String clubName) {
        setTitle("Club Page");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        clubNameLabel = new JLabel("Club Name: " + clubName);
        clubNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(clubNameLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(imageLabel, BorderLayout.CENTER);

        uploadButton = new JButton("Upload Image");
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                int result = fileChooser.showOpenDialog(Club.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
                    imageLabel.setIcon(icon);
                    // Save image to database
                    saveImageToDatabase(clubName, selectedFile);
                }
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(uploadButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void saveImageToDatabase(String clubName, File imageFile) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO ClubImages (club_name, image) VALUES (?, ?)")) {
            stmt.setString(1, clubName);
            FileInputStream fis = new FileInputStream(imageFile);
            stmt.setBinaryStream(2, fis, (int) imageFile.length());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Image saved to database.");
            } else {
                System.out.println("Failed to save image to database.");
            }
        } catch (SQLException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Example usage:
                Club club = new Club("Sample Club");
                club.setVisible(true);
            }
        });
    }
}

