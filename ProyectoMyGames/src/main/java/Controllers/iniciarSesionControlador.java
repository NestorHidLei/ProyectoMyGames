package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import conectarBD.Conexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
        loginButton.setOnAction(event -> handleLogin());
        
     // Acción del enlace "No tengo cuenta"
        registro.setOnAction(event -> abrirRegistroUsuario());

        // Acción del enlace "He olvidado mi contraseña"
        perdidaContrasena.setOnAction(event -> showInfo("Funcionalidad no implementada", "La funcionalidad para recuperar la contraseña estará disponible próximamente."));
        
    }

    private void handleLogin() {
       

        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ) {
            showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, introduce tu usuario y contraseña.");
            return;
        }
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (authenticateUser(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Inicio de sesión exitoso", "¡Bienvenido, " + username + "!");
            // Aquí puedes redirigir a la siguiente pantalla o realizar otra acción
        } else {
            showAlert(Alert.AlertType.ERROR, "Inicio de sesión fallido", "Usuario o contraseña incorrectos.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        boolean isAuthenticated = false;


        Conexion conexionBD = new Conexion();
        Connection conexion = conexionBD.conectar();
        
        String query = "SELECT * FROM Usuario WHERE usuario = ? AND contraseña = ?";

        try ( PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                isAuthenticated = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error de conexión", "No se pudo conectar a la base de datos.");
        }

        return isAuthenticated;
    }

    private void abrirRegistroUsuario() {
        try {
            // Cargar el archivo RegistroUsuario.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/RegistroUsuario.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Registro de Usuario");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana de registro.");
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, content);
    }
}
