package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class MainView {
    private static final String[] LABELS = {"Producent", "Przekątna", "Rozdzielczość", "Matryca", "?", "Procesor",
            "L. rdzeni", "Taktowanie", "RAM", "Pojemność", "Dysk", "Karta graficzna", "VRAM", "System", "Napęd"};
    private static final String FILE_PATH = "src/katalog.txt";

    private JButton importButton;
    private JButton exportButton;
    private JTable dataJTable;
    private JPanel mainJPanel;


    private MainView() {
        importButton.addActionListener(e -> {
            try {
                File readFile = new File(FILE_PATH);
                readFromFile(readFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        exportButton.addActionListener(e -> {
            try {
                File writeFile = new File(FILE_PATH);
                saveToFile(writeFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
                String cell = tableModel.getValueAt(row, column).toString();
                if (cell.equals("---")) {
                    lineBuilder.append(";");
                } else {
                    lineBuilder.append(cell).append(";");
                }
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


    private Vector<String> splitToCells(String line) {
        Vector<String> cells = new Vector<>();

        StringTokenizer st = new StringTokenizer(line, ";", true);

        String lastToken = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals(lastToken) || (token.equals(";") && lastToken.equals(""))) {
                cells.add("---");
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
