package com.example.javafxmysqltemplate;

import com.example.database.Database;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.database.Database.newConnection;

public class HelloApplication extends Application {
    public String getUserNameFromQuery;
    private static final String CONFIG_FILE_PATH = "config.properties";

    @Override
    public void start(Stage stage) throws IOException, SQLException {
//        scrape database for the username
        try (Connection conn = newConnection()) {
            try (PreparedStatement preparedStmt = conn.prepareStatement("SELECT UserName FROM personaluser")) {
                    ResultSet resultSet = preparedStmt.executeQuery();
                if (resultSet.next()) {
                    getUserNameFromQuery = resultSet.getString("UserName");
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
                }
        }



        //checks whether the username is empty or not
        //if it isn't empty, takes you to the insert name tab
        //otherwise, takes you to the main tab
        FXMLLoader fxmlLoader;
        if (getUserNameFromQuery != null && !getUserNameFromQuery.isEmpty()) {
            fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hobbyTrackerMainPage-view.fxml"));
        }
        else {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("insertUserName-view.fxml"));
            }

        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            stage.setTitle("Hobby Tracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            }
    }

        public static void main(String[] args) {
        try {
            Database.initialize(CONFIG_FILE_PATH);
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize database", e);
        }
        launch();
    }
}
