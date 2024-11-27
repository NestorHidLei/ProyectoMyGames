package aplication;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    /**
     * Constructor
     */
    public MainApp() {
        // Initialize if needed
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Login");

        iniciarSesion();
    }

    /**
     * Displays the login window.
     */
    public void iniciarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/RegistroUsuario.fxml"));
            AnchorPane iniciarSesion = (AnchorPane) loader.load();

            Scene scene = new Scene(iniciarSesion);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo FXML: " + e.getMessage());
        }
    }


    /**
     * Returns the main stage.
     * 
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
