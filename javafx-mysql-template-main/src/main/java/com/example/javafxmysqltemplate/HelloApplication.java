package com.example.javafxmysqltemplate;
import com.example.database.Database;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.database.Database.newConnection;
import com.example.javafxmysqltemplate.HelloController;

public class HelloApplication extends Application {
    public String getUserNameFromQuery;
    @FXML
    private TabPane categoryPane = new TabPane();
    @FXML
    private Label mainPage;
    @FXML
    VBox listVBox;
    @FXML
    Tab generalTab;
    @FXML
    ListView listView;
    @FXML
    private Label mainLabel;
    @FXML
    private Stage mainStage;
    @FXML
//    public TabPane categoryPane;
    private static final String CONFIG_FILE_PATH = "config.properties";

    @Override
    public void start(Stage stage) throws IOException, SQLException {
//        mainLabel.setText("version");
//        scrape database for the username
        try (Connection conn = newConnection()) {
            try (PreparedStatement preparedStmt = conn.prepareStatement("SELECT UserName FROM personaluser")) {
                try (ResultSet resultSet = preparedStmt.executeQuery()) {
                    getUserNameFromQuery = String.valueOf(resultSet);
//                    TaskController initilize = new TaskController(); initialize the tasks into General for that given day
//                    initilize.retreiveTasks();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
//        mainLabel.setText("Welcome back " + getUserNameFromQuery);
        TaskController taskController = new TaskController();        //checks whether the username is empty or not
        taskController.retrieveCategorys();
        //if it isn't empty, takes you to the insert name tab
        //otherwise, takes you to the main tab

        if (getUserNameFromQuery != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hobbyTrackerMainPage-view.fxml"));
//                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("insertUserName-view.fxml"));

                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root, 600, 400);

//              stage = (Stage) mainLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Hobby Tracker");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
//                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("insertUserName-view.fxml"));
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("insertUserName-view.fxml"));
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root, 600, 400);
//                HelloController helloController = fxmlLoader.getController();
//                helloController.setMainLabel();
//                stage = (Stage) mainLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Hobby Tracker");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hobbyTrackerMainPage-view.fxml"));
//            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//            stage.setTitle("Hobby Tracker");
//            stage.setScene(scene);
//            stage.show();
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
