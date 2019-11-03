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
            if (!checkExisting()) {
                initDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initDatabase() {
        try {
            statement = connect.createStatement();
            statement.execute("CREATE TABLE `katalog` (\n" +
                    "  `ID` INTEGER NOT NULL constraint katalog_pk\n" +
                    "primary key autoincrement,\n" +
                    "  `producent` varchar(25) DEFAULT NULL,\n" +
                    "  `przekatna` varchar(11) DEFAULT NULL,\n" +
                    "  `rozdzielczosc` varchar(10) DEFAULT NULL,\n" +
                    "  `matryca` varchar(25) DEFAULT NULL,\n" +
                    "  `uzywany` varchar(5) DEFAULT NULL,\n" +
                    "  `procesor` varchar(25) DEFAULT NULL,\n" +
                    "  `l_rdzeni` varchar(5) DEFAULT NULL,\n" +
                    "  `taktowanie` varchar(10) DEFAULT NULL,\n" +
                    "  `ram` varchar(10) DEFAULT NULL,\n" +
                    "  `pojemnosc` varchar(10) DEFAULT NULL,\n" +
                    "  `dysk` varchar(15) DEFAULT NULL,\n" +
                    "  `karta_graficzna` varchar(50) DEFAULT NULL,\n" +
                    "  `vram` varchar(10) DEFAULT NULL,\n" +
                    "  `system` varchar(50) DEFAULT NULL,\n" +
                    "  `naped` varchar(25) DEFAULT NULL\n" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExisting() throws SQLException {
        DatabaseMetaData databaseMetaData = connect.getMetaData();
        ResultSet tables = databaseMetaData.getTables(null, null, "katalog", null);
        if (tables.next()) {
            return true;
        } else {
            return false;
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

    void insertData(Vector<Vector<String>> data) {
        try {
            statement = connect.createStatement();
            for (int i = 0; i < data.size(); i++) {
                preparedStatement = connect.prepareStatement("INSERT INTO " +
                        "katalog (producent,przekatna,rozdzielczosc,matryca,uzywany," +
                        "procesor,l_rdzeni,taktowanie,ram,pojemnosc,dysk," +
                        "karta_graficzna,vram,system,naped)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


                setStatementParameters(i,data);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void updateData(Vector<Vector<String>> data) {
        try {
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select MIN(id),MAX(id) from katalog");
            int minID = resultSet.getInt(1);
            int id = minID;

            for (int i = 0; i < data.size(); i++) {
                preparedStatement = connect.prepareStatement("UPDATE katalog  SET " +
                        "producent=?,przekatna=?,rozdzielczosc=?,matryca=?,uzywany=?,procesor=?," +
                        "l_rdzeni=?,taktowanie=?,ram=?,pojemnosc=?,dysk=?,karta_graficzna=?,vram=?," +
                        "system=?,naped=? WHERE ID=?");


                setStatementParameters(i,data);

                preparedStatement.setInt(COL_NUMBER + 1, id);
                preparedStatement.executeUpdate();
                id++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setStatementParameters(int rowNumber,Vector<Vector<String>> data) throws SQLException {
        for (int j = 1; j <= COL_NUMBER; j++) {
            if (data.get(rowNumber).get(j - 1).equals("")) {
                preparedStatement.setString(j, null);
            } else {
                preparedStatement.setString(j, data.get(rowNumber).get(j - 1));
            }
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