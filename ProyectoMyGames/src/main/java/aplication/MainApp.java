package aplication;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase principal de la aplicación.
 * 
 * Esta clase extiende {@link Application} y sirve como punto de entrada para la aplicación.
 * Configura y muestra la ventana principal de inicio de sesión.
 */
public class MainApp extends Application {

    /**
     * Escenario principal de la aplicación.
     */
    private Stage primaryStage;

    /**
     * Constructor de la clase principal.
     */
    public MainApp() {

    }

    /**
     * Método de inicio que se ejecuta al lanzar la aplicación.
     * 
     * @param primaryStage El escenario principal.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Login");

        iniciarSesion();
    }

    /**
     * Muestra la ventana de inicio de sesión.
     * 
     * Este método carga el archivo FXML correspondiente y configura la escena para el escenario principal.
     */
    public void iniciarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/views/IniciarSesion.fxml"));
            Pane iniciarSesion = (Pane) loader.load();

            Scene scene = new Scene(iniciarSesion);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar el archivo FXML: " + e.getMessage());
        }
    }

    /**
     * Devuelve el escenario principal de la aplicación.
     * 
     * @return El escenario principal.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Punto de entrada principal para lanzar la aplicación.
     * 
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
