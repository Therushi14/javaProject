import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SetGoalsPage extends JFrame {
    private ArrayList<Goal> goalsList;
    private JTable goalsTable;
    private DefaultTableModel tableModel;

    public SetGoalsPage() {
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
        addGoalButton.addActionListener(e -> addGoal());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.add(addGoalButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addGoal() {
        String goalDescription = JOptionPane.showInputDialog("Enter your goal:");
        if (goalDescription != null && !goalDescription.isEmpty()) {
            Goal newGoal = new Goal(goalDescription);
            goalsList.add(newGoal);
            updateGoalsTable();
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
        SwingUtilities.invokeLater(() -> {
            SetGoalsPage setGoalsPage = new SetGoalsPage();
            setGoalsPage.setVisible(true);
        });
    }
}
