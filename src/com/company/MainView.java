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
    private JButton importButton;
    private JButton exportButton;
    private JTable dataJTable;
    private JPanel mainJPanel;


    private MainView() {
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File readFile = new File("src/katalog.txt");
                    readFromFile(readFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    private void readFromFile(File readFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(readFile));

        DefaultTableModel tableModel = new DefaultTableModel(LABELS, 0);
        String line;

        while ((line = br.readLine()) != null) {
            Vector<String> lineCells = splitToCells(line);
            tableModel.addRow(lineCells);
        }

        dataJTable.setModel(tableModel);
        br.close();
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
