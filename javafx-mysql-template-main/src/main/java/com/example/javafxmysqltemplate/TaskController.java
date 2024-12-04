package com.example.javafxmysqltemplate;

import com.example.database.Database;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class TaskController {
    @FXML
    private TabPane categoryPane = new TabPane();
    @FXML
    private Label mainPage;
    @FXML
    VBox listVBox;
    @FXML
    Tab allTasksTab = new Tab();
    @FXML
    ListView listView;
    @FXML
    Tab tab;
    String userName;
    private String taskName;
    private java.sql.Date currentDate;
    Optional<String> task;
    Optional<String> result;
    int categoryId;


    @FXML
    protected void onAddTaskClick() throws SQLException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter your tasks:");
        dialog.setContentText("Task Name:");
        task = dialog.showAndWait();
        String insertTaskName = "INSERT INTO todo (TaskName,CurrentDate,Completed,UserName,CategoryID) VALUES (?,?,?,?,?)";
        taskName = String.valueOf(task);

        if(task.isPresent()){
            taskName = task.get();
            Tab categoryTab = categoryPane.getSelectionModel().getSelectedItem();
            categoryId = getCategoryID(categoryTab.getText());
            System.out.println(categoryId +"ID");
            System.out.println(taskName + " = taskname");
            System.out.println(task.get() + "other");

            if (categoryTab != null) {
                try (Connection conn = Database.newConnection()) {
                    java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                    //inserts name into the table
                    try (PreparedStatement preparedStmt = conn.prepareStatement(insertTaskName)) {
                        preparedStmt.setString(1, task.get());
                        preparedStmt.setString(2, String.valueOf(currentDate));
                        preparedStmt.setBoolean(3, false);
                        preparedStmt.setString(4, userName);
                        preparedStmt.setInt(5, categoryId);
                        preparedStmt.execute();
                    }
                } catch (SQLException sql) {
                    sql.printStackTrace();
                }


                //update UI
                VBox vbox = (VBox) categoryTab.getContent();
                ListView<String> listView = (ListView<String>) vbox.getChildren().get(0);
                listView.getItems().add(taskName);
            }
        }
//        task.ifPresent(name -> {
//            Tab categoryTab = categoryPane.getSelectionModel().getSelectedItem();
////            id.set(getCategoryID(categoryTab));
//            //add items into list
//            if (categoryTab != null) {
//                VBox vbox = (VBox) categoryTab.getContent();
//                ListView<String> listView = (ListView<String>) vbox.getChildren().get(0);
//                listView.getItems().add(name);
//            }
//        });


//        // Query stuff: save tasks into database
//        try (Connection conn = Database.newConnection()) {
//            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
//            //inserts name into the table
//            try (PreparedStatement preparedStmt = conn.prepareStatement(insertTaskName)) {
//                preparedStmt.setString(1, String.valueOf(task));
//                preparedStmt.setString(2, String.valueOf(currentDate));
//                preparedStmt.setBoolean(3, false);
//                preparedStmt.setString(4, userName);
//                preparedStmt.setInt(5,categoryId);
//                preparedStmt.execute();
//                boolean taskPresent = task.isPresent();
//                if (taskPresent) {
//                    System.out.println(task.get());
//                }
//            }
//        } catch (SQLException sql) {
//            sql.printStackTrace();
//        }
    }

    @FXML
    protected void onBackButtonClick() throws SQLException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hobbyTrackerMainPage-view.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600, 400);

            Stage stage = (Stage) mainPage.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Hobby Tracker");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddCategoryClick() throws SQLException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter a name for your new category tab:");
        dialog.setContentText("Category Name:");
        result = dialog.showAndWait();

        // If the user provided a name, create a new tab with that name
        result.ifPresent(name -> {
            Tab newTab = new Tab(name);
            newTab.setId("tab");
            listVBox = new VBox();
            ListView<String> listView = new ListView<>();
            newTab.setContent(listVBox);

            listVBox.getChildren().addAll(listView);
            categoryPane.getTabs().add(newTab);

//            VBox vbox = new VBox();
//            newTab.setContent(list);
//            vbox.getChildren().addAll(listView);

            try (Connection conn = Database.newConnection()) {
                String insertCategory = "INSERT INTO category (CatName, CategoryID) VALUES (?,?)";
                try (PreparedStatement preparedStmt = conn.prepareStatement(insertCategory)) {
                    preparedStmt.setString(1, result.get());
                    preparedStmt.setInt(2, 0);
                    preparedStmt.execute();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    protected void onDeleteClick() throws SQLException {
        Tab selectedTab = categoryPane.getSelectionModel().getSelectedItem();
        String deleteTask = "DELETE FROM todo WHERE TaskName = ?";
        String deleteTab = "DELETE FROM category WHERE CatName = ?";

        if (selectedTab != null) {
            VBox vbox = (VBox) selectedTab.getContent();
            ListView<String> listView = (ListView<String>) vbox.getChildren().get(0);

            String selectedTask = listView.getSelectionModel().getSelectedItem();


            if (selectedTask != null) {
                listView.getItems().remove(selectedTask);
                try (Connection conn = Database.newConnection()) {
                    try (PreparedStatement preparedStmt = conn.prepareStatement(deleteTask)) {
                        preparedStmt.setString(1, selectedTask);
                        preparedStmt.execute();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (!Objects.equals(selectedTab.getText(), "All Tasks")) { //selectedTab != null &&
                categoryPane.getTabs().remove(selectedTab);
                try (Connection conn = Database.newConnection()) {
                    try (PreparedStatement preparedStmt = conn.prepareStatement(deleteTab)) {
                        preparedStmt.setString(1, selectedTab.getText());
                        preparedStmt.execute();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    @FXML
    protected void onCompleted() throws SQLException {
        Tab selectedTab = categoryPane.getSelectionModel().getSelectedItem();

        if (selectedTab != null) {
            VBox vbox = (VBox) selectedTab.getContent();
            ListView<String> listView = (ListView<String>) vbox.getChildren().get(0);
            String selectedTask = listView.getSelectionModel().getSelectedItem();

            if (selectedTask != null) {
                listView.setCellFactory(cell -> new ListCell<String>() {
                    @Override
                    public void updateItem(String task, boolean empty) {
                        super.updateItem(task, empty);
                        if (task == null || empty) {
                            setText("");
                        } else {
                            setText(task);
                            if (task.equals(selectedTask)) {
                                setStyle("-fx-text-fill: green;");

                                //update the task as completed within the database
                                try (Connection conn = Database.newConnection()) {
                                    String updateCategory = "UPDATE todo SET Completed = ? WHERE TaskName = ?";
                                    try (PreparedStatement preparedStmt = conn.prepareStatement(updateCategory)) {
                                        String taskText = !selectedTask.isEmpty() ? selectedTask : "";
                                        preparedStmt.setBoolean(1, true);
                                        preparedStmt.setString(2, taskText);
                                        preparedStmt.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    //updates the tabs with the already saved categorys in the system
    @FXML
    public void retrieveCategorys() {
        ArrayList<String> results = new ArrayList<>();
        try (Connection conn = Database.newConnection()) {
            String updateCategory = "SELECT CatName FROM category";
            try (PreparedStatement preparedStmt = conn.prepareStatement(updateCategory)) {
                ResultSet categorys = preparedStmt.executeQuery();
                while (categorys.next()) {
                    String value = categorys.getString("CatName");
                    results.add(value);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            for (String str : results) {
                Tab newTab = new Tab(str);
                newTab.setId(str);
                listVBox = new VBox();
                ListView<String> listView = new ListView<>();
                newTab.setContent(listVBox);

                listVBox.getChildren().add(listView);
                categoryPane.getTabs().add(newTab);

                System.out.println(str);
            }
        });
    }

    private int getCategoryID(String tabName) {
        String getQuery = "SELECT CategoryID FROM category WHERE CatName = ?";

        try (Connection conn = Database.newConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(getQuery);
            preparedStatement.setString(1,tabName);
            try (ResultSet id = preparedStatement.executeQuery()) {
//                String catName = id.getString("CatName");
                if(id.next()){
                    return id.getInt("CategoryID");
                }
                else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    //gets all the tasks in the Database on that day
    public void retrieveTasks() {
        currentDate = getCurrentDate();
        try (Connection conn = Database.newConnection()) {
            String retrieveTasks = "SELECT TaskName FROM todo WHERE CurrentDate = ?";
            try (PreparedStatement preparedStmt = conn.prepareStatement(retrieveTasks)) {
                preparedStmt.setDate(1, currentDate);
                ResultSet tasks = preparedStmt.executeQuery();

//                VBox vbox = (VBox) allTasksTab.getContent();
//                ListView<String> listView = (ListView<String>) vbox.getChildren().get(0);

                while (tasks.next()) {
                    String taskName = tasks.getString("TaskName");
                    listView.getItems().add(taskName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getCurrentDate() {
        currentDate = new java.sql.Date(System.currentTimeMillis());
        return currentDate;
    }

    public void initialize() {
        retrieveCategorys();
        retrieveTasks();
    }
}


