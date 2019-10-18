package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

    private static List<String> splitToCells(String line) {
        List<String> cells = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(line, ";", true);

        String lastToken = "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals(lastToken) || (token.equals(";") && lastToken.equals(""))){
                cells.add("---");
            } else if (!token.equals(";")) {
                cells.add(token);
            }
            lastToken = token;
        }

        return cells;
    }

    public static void main(String[] args) throws Exception {
        File readFile = new File("src/katalog.txt");

        BufferedReader br = new BufferedReader(new FileReader(readFile));

        String line;

        String[] LABELS = {"Producent", "Przekątna", "Rozdzielczość", "Matryca", "?", "Procesor",
                "L. rdzeni", "Cena", "RAM", "Pojemność", "Dysk", "Karta graficzna", "VRAM", "System", "Napęd"};

        String[] FORMATSTYLES = {"| %-10s", "| %-10s", "| %-13s", "| %-11s", "| %-4s", "| %-9s", "| %-10s", "| %-5s",
                "| %-5s", "| %-10s", "| %-5s", "| %-25s", "| %-5s", "| %-25s", "| %-8s |"};

        for (int i = 0; i < LABELS.length; i++) {
            System.out.printf(FORMATSTYLES[i], LABELS[i]);
        }
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------------------");
        while ((line = br.readLine()) != null) {
            List<String> lineCells = splitToCells(line);

            for (int i = 0; i < lineCells.size(); i++) {
                System.out.printf(FORMATSTYLES[i], lineCells.get(i));
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------------------");
    }

}
