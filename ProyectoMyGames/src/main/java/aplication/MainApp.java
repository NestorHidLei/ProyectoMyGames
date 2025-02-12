package aplication;

import controllers.BibliotecaControlador;
import controllers.DeseadosControlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Usuario;

public class MainApp extends Application {

    private Usuario usuario; // Usuario autenticado

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Biblioteca.fxml"));
            Parent root = loader.load();
            usuario = new Usuario("Prueba", "Usuario", "prueba@example.com", "test123", "usuarioPrueba", "4,5,6,4,5,6,4,5,6,4,5,6,4,5,6,4,5,6", "4,5,6,4,5,6,4,5,6,4,5,6,4,5,6,4,5,6");
            // Obtener el controlador después de cargar el FXML
            BibliotecaControlador controlador = loader.getController();

            if (controlador != null) {
                if (usuario != null) {
                    controlador.setUsuario(usuario);
                } else {
                    System.out.println("ERROR: Usuario es null.");
                }
            } else {
                System.out.println("ERROR: El controlador no se pudo cargar.");
            }

            primaryStage.setTitle("Juegos Deseados");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarSesion() {
        // Simulación de obtención del usuario autenticado
        usuario = new Usuario("Prueba", "Usuario", "prueba@example.com", "test123", "usuarioPrueba", "3328,3498,4200", "4,5,6");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

