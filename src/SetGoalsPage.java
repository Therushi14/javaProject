package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;



public class SetGoalsPage extends JFrame {
    private ArrayList<Goal> goalsList;
    private JTable goalsTable;
    private DefaultTableModel tableModel;

    public static String prn_no;
    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";


    public SetGoalsPage(String prn) {

        setTitle("Set Goals");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        goalsList = new ArrayList<>();
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Goal Name");
        tableModel.addColumn("Status");

        goalsTable = new JTable(tableModel);
        goalsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = goalsTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < goalsList.size()) {
                    goalsList.get(row).toggleStatus();
                    updateGoalsTable(); // Update the display
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(goalsTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton addGoalButton = new JButton("ADD GOAL");
        addGoalButton.addActionListener(e -> addGoal(prn));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.add(addGoalButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addGoal(String prn) {
        String goalDescription = JOptionPane.showInputDialog("Enter your goal:");
        if (goalDescription != null && !goalDescription.isEmpty()) {
            Connection connection = null;
            PreparedStatement statement = null;
            try {
                // Establishing the database connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

                // SQL statement to insert the goal description
                String query = "INSERT INTO Goals (student_id, description) VALUES (?, ?)";

                // Preparing the statement
                statement = connection.prepareStatement(query);
                statement.setString(1, prn); // Set the PRN
                statement.setString(2, goalDescription); // Set the goal description

                // Execute the INSERT statement
                statement.executeUpdate();

                // Add the goal to the local list
                Goal newGoal = new Goal(goalDescription);
                goalsList.add(newGoal);

                // Update the goals table
                updateGoalsTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while adding the goal. Please try again.");
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
    }


    private void updateGoalsTable() {
        tableModel.setRowCount(0); // Clear the table
        for (Goal goal : goalsList) {
            tableModel.addRow(new Object[]{goal.getDescription(), goal.getStatus()});
        }
    }

    private class Goal {
        private String description;
        private boolean completed;

        public Goal(String description) {
            this.description = description;
            this.completed = false;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return completed ? "Completed" : "Incomplete";
        }

        public void toggleStatus() {
            completed = !completed;
        }
    }

    public static void main(String[] args) {
        String prn = SetGoalsPage.prn_no;
        SwingUtilities.invokeLater(() -> {
            SetGoalsPage setGoalsPage = new SetGoalsPage(prn);
            setGoalsPage.setVisible(true);
        });
    }
}
