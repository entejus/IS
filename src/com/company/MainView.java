package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class MainView {
    private static final String[] LABELS = {"Producent", "Przekątna", "Rozdzielczość", "Matryca", "Używany", "Procesor",
            "L. rdzeni", "Taktowanie", "RAM", "Pojemność", "Dysk", "Karta graficzna", "VRAM", "System", "Napęd"};
    private static final String FILE_PATH = "src/katalog.txt";

    private JButton fileImportButton;
    private JButton fileExportButton;
    private JButton databaseImportButton;
    private JTable dataJTable;
    private JPanel mainJPanel;
    private JButton databaseExportButton;
    private DefaultTableCellRenderer cellRenderer;


    private MainView() {
        dataJTable.setDefaultRenderer(Object.class, new CustomCellRenderer());

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
        DefaultTableModel tableModel = (DefaultTableModel) dataJTable.getModel();

        DBConnector dbConnector = new DBConnector();

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
        dbConnector.setDataRow(records);
        dbConnector.close();
    }

    private void readFromDatabase() {
        DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);

        DBConnector dbConnector = new DBConnector();
        Vector<Vector<String>> records = dbConnector.getData();

        for (Vector<String> record : records) {
            tableModel.addRow(record);
        }

        dataJTable.setModel(tableModel);
        dataJTable.getColumnModel().getColumn(0).setCellRenderer(cellRenderer);

        dbConnector.close();
    }

    public class CustomCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Color c = Color.WHITE;
            if (value.toString().equals(""))
                c = Color.RED;
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
