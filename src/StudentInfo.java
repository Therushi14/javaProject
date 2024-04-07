package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentInfo extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;

    private static final String DB_URL = "jdbc:oracle:thin:@10.90.4.82:1521/xe";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "root";

    public StudentInfo(String division) {
        setTitle("Student Information - Division: " + division);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel();
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add column headers
        tableModel.addColumn("PRN");
        tableModel.addColumn("Name");
        tableModel.addColumn("Show");
        tableModel.addColumn("View Goals"); // New column for View Goals button

        // Set cell renderer to render buttons
        studentTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        studentTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));
        studentTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer()); // View Goals button
        studentTable.getColumnModel().getColumn(3).setCellEditor(new ViewGoalsButtonEditor(new JCheckBox()));

        fetchStudentData(division);

        add(panel);
    }

    private void fetchStudentData(String division) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // Establishing the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT prn_no, name FROM Student WHERE division = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, division);
            resultSet = statement.executeQuery();

            // Populate the table with student data
            while (resultSet.next()) {
                String prn = resultSet.getString("prn_no");
                String name = resultSet.getString("name");
                tableModel.addRow(new Object[]{prn, name, "Show", "View Goals"});
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

    // Renderer for rendering buttons
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(new Color(row * 30, column * 30, 255)); // Change color based on row and column
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Editor for handling button click for "Show" button
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = studentTable.getSelectedRow();
                String prn = (String) studentTable.getValueAt(selectedRow, 0); // Get PRN from selected row

                // Fetch data from summary table
                fetchSummaryData(prn);
            }
            isPushed = false;
            return new String(label);
        }

        private void fetchSummaryData(String prn) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Establishing the database connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String query = "SELECT SUMMARY_TEXT FROM Summary WHERE PRN_NO = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, prn);
                resultSet = statement.executeQuery();

                // Display summary text in a separate window with JTextArea
                JFrame summaryFrame = new JFrame("Summary for PRN: " + prn);
                summaryFrame.setSize(400, 300);
                summaryFrame.setLocationRelativeTo(null); // Center the window on the screen

                JTextArea summaryTextArea = new JTextArea();
                summaryTextArea.setEditable(false); // Make the text area read-only
                summaryTextArea.setLineWrap(true); // Enable word wrap
                summaryTextArea.setWrapStyleWord(true); // Wrap at word boundaries
                JScrollPane scrollPane = new JScrollPane(summaryTextArea);

                if (resultSet.next()) {
                    String summaryText = resultSet.getString("SUMMARY_TEXT");
                    summaryTextArea.setText(summaryText);
                } else {
                    summaryTextArea.setText("Summary not found for PRN: " + prn);
                }

                summaryFrame.add(scrollPane);
                summaryFrame.setVisible(true);
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



        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }


    // Editor for handling button click for "View Goals" button
    class ViewGoalsButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;
        private boolean isPushed;

        public ViewGoalsButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = studentTable.getSelectedRow();
                String prn = (String) studentTable.getValueAt(selectedRow, 0);
                // Get PRN from selected row
                System.out.println(prn);
                // Fetch goals for the selected PRN and display them
                fetchGoals(prn);
            }
            isPushed = false;
            return new String(label);
        }

        private void fetchGoals(String prn) {
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                // Establishing the database connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                String query = "SELECT description FROM Goals WHERE student_id = ?";
                statement = connection.prepareStatement(query);
                statement.setString(1, prn);
                resultSet = statement.executeQuery();

                // Create a new frame to display the goals
                JFrame goalsFrame = new JFrame("Goals for PRN: " + prn);
                goalsFrame.setSize(600, 400);
                goalsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Create a text area to display the goals
                JTextArea goalsTextArea = new JTextArea();
                JScrollPane scrollPane = new JScrollPane(goalsTextArea);
                goalsFrame.add(scrollPane);

                // Populate the text area with goals
                StringBuilder goals = new StringBuilder();
                while (resultSet.next()) {
                    goals.append(resultSet.getString("description")).append("\n");
                }
                goalsTextArea.setText(goals.toString());

                goalsFrame.setVisible(true);
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

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Example usage:
                StudentInfo studentInfo = new StudentInfo("A"); // Pass the division
                studentInfo.setVisible(true);
            }
        });
    }
}
