package com.company;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class MainView {
    private static final String[] LABELS = {"Producent", "Przekątna", "Rozdzielczość", "Matryca", "Dotykowy", "Procesor",
            "L. rdzeni", "Taktowanie", "RAM", "Pojemność", "Dysk", "Karta graficzna", "VRAM", "System", "Napęd"};
    private static final String FILE_PATH = "src/katalog.txt";

    private JButton fileImportButton;
    private JButton fileExportButton;
    private JButton databaseImportButton;
    private JTable dataJTable;
    private JPanel mainJPanel;
    private JButton databaseExportButton;
    private JButton xmlExportButton;
    private JButton xmlImportButton;
    private Vector<Vector<String>> dataInDatabase;


    private void initDatabaseData() {
        dataJTable.setDefaultRenderer(Object.class, new CustomCellRenderer());
        DBConnector dbConnector = new DBConnector();
        dataInDatabase = dbConnector.getData();
        dbConnector.close();
    }

    private MainView() {
        initDatabaseData();
        fileImportButton.addActionListener(e -> {
            try {
                File readFile = new File(FILE_PATH);
                readFromFile(readFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        fileExportButton.addActionListener(e -> {
            try {
                File writeFile = new File(FILE_PATH);
                saveToFile(writeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        databaseImportButton.addActionListener(e -> {
            readFromDatabase();
        });
        databaseExportButton.addActionListener(e -> {
            saveToDatabase();
        });
        xmlExportButton.addActionListener(e -> {
            saveToXML();
        });
        xmlImportButton.addActionListener(e -> {
            readFromXML();
        });
    }

    private void saveToFile(File writeFile) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(writeFile));

        DefaultTableModel tableModel = (DefaultTableModel) dataJTable.getModel();
        int rowsNumber = tableModel.getRowCount();
        int columnsNumber = tableModel.getColumnCount();

        for (int row = 0; row < rowsNumber; row++) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int column = 0; column < columnsNumber; column++) {
                lineBuilder.append(tableModel.getValueAt(row, column)).append(";");
            }
            bufferedWriter.write(lineBuilder.toString());
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    private void readFromFile(File readFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(readFile));

        DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            Vector<String> lineCells = splitToCells(line);
            tableModel.addRow(lineCells);
        }

        dataJTable.setModel(tableModel);
        bufferedReader.close();
    }

    private void saveToDatabase() {
        DBConnector dbConnector = new DBConnector();

        Vector<Vector<String>> records = getRecords();

        if (dataInDatabase.size() == 0) {
            dbConnector.insertData(records);
        } else {
            dbConnector.updateData(records);
        }
        dbConnector.close();
        dataInDatabase = records;
        refreshTable();
    }

    private void readFromDatabase() {
        DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);
        Vector<Vector<String>> records = new Vector<>();

        DBConnector dbConnector = new DBConnector();
        try {
            if (dbConnector.checkExisting())
                records = dbConnector.getData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Vector<String> record : records) {
            tableModel.addRow(record);
        }

        dataJTable.setModel(tableModel);

        dbConnector.close();
    }

    private void saveToXML() {
        try {
            XMLHelper xmlHelper = new XMLHelper();

            Vector<Vector<String>> records = getRecords();

            xmlHelper.createXML(records);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void readFromXML() {
        try {
            DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);
            XMLHelper xmlHelper = new XMLHelper();

            Vector<Vector<String>> records = new Vector<>();
            records = xmlHelper.readXML();

            for (Vector<String> record : records) {
                tableModel.addRow(record);
            }

            dataJTable.setModel(tableModel);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Vector<Vector<String>> getRecords() {
        DefaultTableModel tableModel = (DefaultTableModel) dataJTable.getModel();
        int rowsNumber = tableModel.getRowCount();
        int columnsNumber = tableModel.getColumnCount();
        Vector<Vector<String>> records = new Vector<>();

        for (int row = 0; row < rowsNumber; row++) {
            Vector<String> record = new Vector<>();
            for (int column = 0; column < columnsNumber; column++) {
                String cell = tableModel.getValueAt(row, column).toString();
                record.add(Objects.requireNonNullElse(cell, ""));
            }
            records.add(record);
        }
        return records;
    }

    private void refreshTable() {
        DefaultTableModel tableModel = (DefaultTableModel) dataJTable.getModel();
        tableModel.fireTableDataChanged();
    }

    public class CustomCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color c = Color.WHITE;
            if (dataInDatabase.size() != 0) {
                String databaseCellValue = dataInDatabase.get(row).get(column);
                if (!value.toString().equals(databaseCellValue))
                    c = Color.RED;
            } else {
                c = Color.RED;
            }
            label.setBackground(c);
            return label;
        }

    }

    private Vector<String> splitToCells(String line) {
        Vector<String> cells = new Vector<>();

        StringTokenizer st = new StringTokenizer(line, ";", true);

        String lastToken = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals(lastToken) || (token.equals(";") && lastToken.equals(""))) {
                cells.add("");
            } else if (!token.equals(";")) {
                cells.add(token);
            }
            lastToken = token;
        }

        return cells;
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("Integracja Systemów - Jakub Mielniczuk");
        frame.setContentPane(new MainView().mainJPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(true);
    }

}
