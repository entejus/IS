package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class MainView {
    private JButton importButton;
    private JButton exportButton;
    private JTable dataJTable;
    private JPanel mainJPanel;


    public MainView() throws IOException {


        File readFile = new File("src/katalog.txt");

        BufferedReader br = new BufferedReader(new FileReader(readFile));

        String line;

        String[] LABELS = {"Producent", "Przekątna", "Rozdzielczość", "Matryca", "?", "Procesor",
                "L. rdzeni", "Taktowanie", "RAM", "Pojemność", "Dysk", "Karta graficzna", "VRAM", "System", "Napęd"};

        String[] FORMATSTYLES = {"| %-10s", "| %-10s", "| %-13s", "| %-11s", "| %-4s", "| %-9s", "| %-10s", "| %-10s",
                "| %-5s", "| %-10s", "| %-5s", "| %-25s", "| %-5s", "| %-25s", "| %-8s |"};

        DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);


        while ((line = br.readLine()) != null) {
            Vector<String> lineCells = splitToCells(line);

            tableModel.addRow(lineCells);
        }
        dataJTable.setModel(tableModel);
    }


    private static Vector<String> splitToCells(String line) {
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
