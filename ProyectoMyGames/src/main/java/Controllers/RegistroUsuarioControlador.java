package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conectarBD.Conexion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class RegistroUsuarioControlador {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Button registrarButton;

    @FXML
    private Button cancelarButton;

    @FXML
    public void initialize() {
        // Configurar acción para el botón "Registrar"
        registrarButton.setOnAction(event -> {
            try {
                registrarUsuario(event);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        // Configurar acción para el botón "Cancelar"
        cancelarButton.setOnAction(event -> {
            try {
                cargarInicioSesion(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void registrarUsuario(ActionEvent event) throws SQLException, IOException {
        // Validación básica de los campos
        if (nombreField.getText().isEmpty() || apellidosField.getText().isEmpty() || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty() || usernameField.getText().isEmpty()) {
            mostrarAlerta(AlertType.ERROR, "Campos vacíos", "Por favor, completa todos los campos.");
            return;
        }

        Conexion conexionBD = new Conexion();
        Connection conexion = conexionBD.conectar();

        // Verificar si el usuario o email ya existe
        String checkQuery = "SELECT COUNT(*) FROM usuario WHERE usuario = ? OR email = ?";
        try (PreparedStatement checkStatement = conexion.prepareStatement(checkQuery)) {
            checkStatement.setString(1, usernameField.getText());
            checkStatement.setString(2, emailField.getText());
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) > 0) {
                mostrarAlerta(AlertType.WARNING, "Usuario duplicado", "El usuario o correo ya existe. Intenta con otros.");
                return;
            }
        }

        // Insertar el nuevo usuario en la base de datos
        String insertQuery = "INSERT INTO usuario (nombre, apellidos, email, usuario, contraseña) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conexion.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, nombreField.getText());
            preparedStatement.setString(2, apellidosField.getText());
            preparedStatement.setString(3, emailField.getText());
            preparedStatement.setString(4, usernameField.getText());
            preparedStatement.setString(5, passwordField.getText());
            preparedStatement.execute();

            // Limpiar los campos y mostrar un mensaje de éxito
            limpiarCampos();
            mostrarAlerta(AlertType.INFORMATION, "Registro exitoso", "El usuario se ha registrado con éxito.");

            // Cargar la página de InicioSesion.fxml
            cargarInicioSesion(event);
        }
    }

    private void limpiarCampos() {
        nombreField.clear();
        apellidosField.clear();
        emailField.clear();
        passwordField.clear();
        usernameField.clear();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarInicioSesion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/IniciarSesion.fxml"));
        Scene scene = new Scene(loader.load());

        // Obtener el Stage actual y cambiar la escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}


