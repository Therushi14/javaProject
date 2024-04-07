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

        // Set cell renderer to render buttons
        studentTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        studentTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

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
                tableModel.addRow(new Object[]{prn, name, "Show"});
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

    // Editor for handling button click
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
                // Handle button click action here
                // Retrieve data from the summary table based on PRN and display according to timestamp
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
                String query = "SELECT * FROM Summary WHERE prn_no = ? ORDER BY summary_timestamp DESC";
                System.out.println(prn);
                statement = connection.prepareStatement(query);
                statement.setString(1, prn);
                resultSet = statement.executeQuery();
        
                // Create a new frame to display the summary data
                JFrame summaryFrame = new JFrame("Summary for PRN: " + prn);
                summaryFrame.setSize(600, 400);
                summaryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
                // Create a table to display the summary data
                DefaultTableModel summaryTableModel = new DefaultTableModel();
                summaryTableModel.addColumn("Timestamp");
                summaryTableModel.addColumn("summary_text");
        
                JTable summaryTable = new JTable(summaryTableModel);
                JScrollPane scrollPane = new JScrollPane(summaryTable);
                summaryFrame.add(scrollPane);
        
                // Populate the table with summary data
                while (resultSet.next()) {
                    String timestamp = resultSet.getString("summary_timestamp");
                    String summary = resultSet.getString("summary_text");
                    summaryTableModel.addRow(new Object[]{timestamp, summary});
                    System.out.println(timestamp+summary);
                }
        
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
