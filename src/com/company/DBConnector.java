package com.company;

import java.io.ByteArrayInputStream;
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
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/is?"
                            + "user=root&password=password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Vector<Vector<String>> getData() {
        Vector<Vector<String>> records = new Vector<>();
        try {
            statement = connect.createStatement();
            resultSet = statement
                    .executeQuery("select * from is.katalog order by id");
            while (resultSet.next()) {
                Vector<String> record = new Vector<>();
                for (int i = 2; i <= COL_NUMBER+1; i++) {

                    String cell = resultSet.getString(i);
                    record.add(Objects.requireNonNullElse(cell, "---"));
                }
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    void setDataRow(Vector<String> data) {
        try {
            preparedStatement = connect.prepareStatement("insert into is.katalog values " +
                    "(default ,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for (int i = 1; i <= COL_NUMBER; i++) {
                if (data.get(i - 1).equals("---")) {
                    preparedStatement.setString(i, null);
                } else {
                    preparedStatement.setString(i, data.get(i - 1));
                }
            }
            preparedStatement.executeUpdate();
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