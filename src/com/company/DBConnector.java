package com.company;

import java.sql.*;
import java.util.Objects;
import java.util.Vector;

public class DBConnector {
    public static final int COL_NUMBER = 15;

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public DBConnector() {
        try {
            connect = DriverManager
                    .getConnection("jdbc:sqlite:katalog.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Vector<Vector<String>> getData() {
        Vector<Vector<String>> records = new Vector<>();
        try {
            statement = connect.createStatement();
            resultSet = statement
                    .executeQuery("select * from katalog order by id");
            while (resultSet.next()) {
                Vector<String> record = new Vector<>();
                for (int i = 2; i <= COL_NUMBER + 1; i++) {

                    String cell = resultSet.getString(i);
                    record.add(Objects.requireNonNullElse(cell, ""));
                }
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    void setDataRow(Vector<Vector<String>> data) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select MIN(id),MAX(id) from katalog");
            int minID = resultSet.getInt(1);
            int maxID = resultSet.getInt(2);

            for (int i = minID; i <= maxID; i++) {
                preparedStatement = connect.prepareStatement("UPDATE katalog  SET " +
                        "producent=?,przekatna=?,rozdzielczosc=?,matryca=?,uzywany=?,procesor=?," +
                        "l_rdzeni=?,taktowanie=?,ram=?,pojemnosc=?,dysk=?,karta_graficzna=?,vram=?," +
                        "system=?,naped=? WHERE ID=?");

                for (int j = 1; j <= COL_NUMBER; j++) {
                    if (data.get(i).get(j-1).equals("")) {
                        preparedStatement.setString(j, null);
                    } else {
                        preparedStatement.setString(j, data.get(i).get(j-1));
                    }
                }
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}