package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexiones.ConexionBD;
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

/**
 * Controlador para la funcionalidad de registro de nuevos usuarios.
 * 
 * Esta clase permite a los usuarios registrarse en la aplicación mediante un formulario.
 * Realiza validaciones básicas, verifica duplicados en la base de datos y registra
 * al usuario si toda la información es válida.
 */
public class RegistroUsuarioControlador {

    /**
     * Campo de texto para que el usuario introduzca su nombre.
     */
    @FXML
    private TextField nombreField;

    /**
     * Campo de texto para que el usuario introduzca sus apellidos.
     */
    @FXML
    private TextField apellidosField;

    /**
     * Campo de texto para que el usuario introduzca su correo electrónico.
     */
    @FXML
    private TextField emailField;

    /**
     * Campo de contraseña para que el usuario introduzca su contraseña.
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Campo de texto para que el usuario introduzca su nombre de usuario.
     */
    @FXML
    private TextField usernameField;

    /**
     * Botón para registrar un nuevo usuario.
     */
    @FXML
    private Button registrarButton;

    /**
     * Botón para cancelar el registro y volver a la pantalla de inicio de sesión.
     */
    @FXML
    private Button cancelarButton;

    /**
     * Inicializa los eventos asociados a los elementos de la interfaz.
     * Este método se llama automáticamente al cargar la vista FXML.
     */
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

    /**
     * Registra un nuevo usuario en la base de datos.
     * 
     * @param event El evento asociado al botón "Registrar".
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws IOException  Si ocurre un error al cargar la siguiente vista.
     */
    private void registrarUsuario(ActionEvent event) throws SQLException, IOException {
        // Validación básica de los campos
        if (nombreField.getText().isEmpty() || apellidosField.getText().isEmpty() || emailField.getText().isEmpty()
                || passwordField.getText().isEmpty() || usernameField.getText().isEmpty()) {
            mostrarAlerta(AlertType.ERROR, "Campos vacíos", "Por favor, completa todos los campos.");
            return;
        }

        ConexionBD conexionBD = new ConexionBD();
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
        String insertQuery = "INSERT INTO usuario (nombre, apellidos, email, usuario, password) VALUES (?, ?, ?, ?, ?)";
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

    /**
     * Limpia todos los campos del formulario.
     */
    private void limpiarCampos() {
        nombreField.clear();
        apellidosField.clear();
        emailField.clear();
        passwordField.clear();
        usernameField.clear();
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     * 
     * @param tipo    El tipo de alerta (ERROR, INFORMATION, etc.).
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Carga la pantalla de inicio de sesión.
     * 
     * @param event El evento asociado al botón "Cancelar".
     * @throws IOException Si ocurre un error al cargar la vista.
     */
    private void cargarInicioSesion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/IniciarSesion.fxml"));
        Scene scene = new Scene(loader.load());

        // Obtener el Stage actual y cambiar la escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
