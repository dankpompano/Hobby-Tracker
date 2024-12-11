package com.example.javafxmysqltemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class CSVDownloader {
    public static void main(String[] args){
       download();
    }

    public static void download(){
        String url = "jdbc:mysql://localhost:3306/hobbytracker";
        String username = "root";
        String password = "password";
        String query = "SELECT * FROM todo";

        String csv = "taskHistory.csv";


        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
             FileWriter fileWriter = new FileWriter(csv)) {

            int columnCount = resultSet.getMetaData().getColumnCount();
            for(int i = 1; i <= columnCount; ++i){

                fileWriter.append(resultSet.getMetaData().getColumnName(i));
                if (i < columnCount) {
                    fileWriter.append(",");
                }
            }
            fileWriter.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.append(resultSet.getString(i) != null ? resultSet.getString(i) : "");
                    if (i < columnCount) {
                        fileWriter.append(",");
                    }
                }
                fileWriter.append("\n");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
