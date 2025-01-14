package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import conectarBD.Conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class iniciarSesionControlador {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink registro;

    @FXML
    private Hyperlink perdidaContrasena;

    @FXML
    public void initialize() {
        // Validar que los elementos FXML estén correctamente enlazados
        if (loginButton != null) {
        	loginButton.setOnAction(event -> manejarInicioSesion(event));
        } else {
            System.out.println("Error: 'botonIniciarSesion' no está enlazado correctamente en el archivo FXML.");
        }

        if (registro != null) {
            registro.setOnAction(event -> abrirRegistroUsuario(event));
        }

        if (perdidaContrasena != null) {
        	perdidaContrasena.setOnAction(event -> mostrarInfo("Funcionalidad no implementada",
                    "La funcionalidad para recuperar la contraseña estará disponible próximamente."));
        }
    }

    private void manejarInicioSesion(ActionEvent event) {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, introduce tu usuario y contraseña.");
            return;
        }
        String usuario = usernameField.getText().trim();
        String contrasena = passwordField.getText().trim();
        if (autenticarUsuario(usuario, contrasena)) {
            abrirInicio(event);
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Inicio de sesión fallido", "Usuario o contraseña incorrectos.");
        }
    }

    private boolean autenticarUsuario(String usuario, String contrasena) {
        boolean autenticado = false;

        Conexion conexionBD = new Conexion();
        Connection conexion = conexionBD.conectar();

        String consulta = "SELECT * FROM Usuario WHERE usuario = ? AND contraseña = ?";

        try (PreparedStatement preparedStatement = conexion.prepareStatement(consulta)) {
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contrasena);

            ResultSet resultado = preparedStatement.executeQuery();

            if (resultado.next()) {
                autenticado = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos.");
        }

        return autenticado;
    }

    private void abrirRegistroUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegistroUsuario.fxml"));
            Scene escena = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de registro.");
        }
    }

    private void abrirInicio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Inicio.fxml"));
            System.out.println("Cargando archivo FXML: " + getClass().getResource("/views/Inicio.fxml")); // Depuración
            Scene escena = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(escena);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de inicio.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String titulo, String contenido) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void mostrarInfo(String titulo, String contenido) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, contenido);
    }
}


