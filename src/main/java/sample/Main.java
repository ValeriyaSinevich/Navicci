package sample;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    private static Main instance;

    private Controller controller;


    final ConcurrentLinkedQueue<Object> results
            = new ConcurrentLinkedQueue<Object>();


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/sample.fxml"));
        GridPane root = null;
        fxmlLoader.setRoot(root);
        root = (GridPane)fxmlLoader.load();
        controller = fxmlLoader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public Main() {
        instance = this;
    }
    public static Main getInstance() {
        return instance;
    }

    public void addResult(Object newEl) {
        results.add(newEl);
    }

    public List<Gyro> getResult() {
        return (List<Gyro>)results.poll();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Controller getController() {
        return controller;
    }
}
