package com.company;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.Vector;

public class DBConnector {
    private static final int COL_NUMBER = 15;

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

    Vector<ResultSet> getData() {
        Vector<ResultSet> records= new Vector<>();
        try {
            statement = connect.createStatement();
            resultSet = statement
                    .executeQuery("select * from is.katalog order by id");
            while (resultSet.next()) {
                records.add(resultSet);
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