package com.example.javafxmysqltemplate;
//package src.main.java.com.example.javafxmysqltemplate;

import com.example.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static com.example.database.Database.newConnection;

public class HelloController {
    @FXML
    public TextField NameStatusTextField;
    @FXML
    private Label mainLabel = new Label("");
    @FXML
    private Label mainPage;
    @FXML
    private Label taskPage;
    private String userName;
    public String getUserNameFromQuery;

    //takes you to enter the user name tab
    //if a username is already there, will take you to the main tab
    @FXML
    protected void onStartHobbyTrackerButtonClick() throws SQLException {
        try (Connection conn = newConnection()) {
            try (PreparedStatement preparedStmt = conn.prepareStatement("SELECT UserName FROM personaluser")) {
                ResultSet resultSet = preparedStmt.executeQuery();
                if (resultSet.next()) {
                    getUserNameFromQuery = resultSet.getString("UserName");
                }
            }

            mainLabel.setText("Welcome back, " + getUserNameFromQuery);
            System.out.println(getUserNameFromQuery);

        }
}


    //saves the data from the text field
    @FXML
    protected void onSaveNameButtonClick() throws SQLException {
        userName = NameStatusTextField.getText();

        if(!userName.trim().isEmpty()){
            saveName(userName);
        }
        else{
            NameStatusTextField.setText("Please enter your name.");
        }
    }

    //takes you to the main tab from the insert name tab
    @FXML
    protected void onNextButtonClick(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hobbyTrackerMainPage-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600,400);

            Stage stage = (Stage) mainPage.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hobby Tracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //method that saves the name
    //queries get the current date as well as insert the name into the database
    private void saveName(String name) throws SQLException {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        String insertName = "INSERT INTO personaluser (UserName,DateJoined) VALUES (?,?)";
        //String saveName = "SELECT UserName FROM personaluser";
        try (Connection conn = Database.newConnection()){
            //inserts name into the table
            try(PreparedStatement preparedStmt = conn.prepareStatement(insertName)){
                preparedStmt.setString(1,name);
                preparedStmt.setString(2, String.valueOf(currentDate));
                preparedStmt.execute();
            }
        }
        catch (SQLException sql){
            sql.printStackTrace();
        }
    }
    @FXML
    protected void onTaskButtonClick(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("tasksPage-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600,400);

            Stage stage = (Stage) taskPage.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hobby Tracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onCalendarButtonClick(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Calendar-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600,400);

            Stage stage = (Stage) taskPage.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hobby Tracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName(){
        return getUserNameFromQuery;
    }
    public void setMainLabel() {
        mainLabel.setText("Welcome back " + getUserNameFromQuery);
    }
}