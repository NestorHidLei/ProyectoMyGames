package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import conexiones.ConexionBD;
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

/**
 * Controlador para la pantalla de inicio de sesión en la aplicación.
 * 
 * Esta clase gestiona la interacción del usuario con la interfaz de inicio de
 * sesión, incluyendo la autenticación del usuario, redirección a las pantallas
 * de registro y recuperación de contraseña, y manejo de errores relacionados.
 */
public class iniciarSesionControlador {

	/**
	 * Campo de texto para que el usuario introduzca su nombre de usuario.
	 */
	@FXML
	private TextField usernameField;

	/**
	 * Campo de texto para que el usuario introduzca su contraseña.
	 */
	@FXML
	private PasswordField passwordField;

	/**
	 * Botón para iniciar sesión.
	 */
	@FXML
	private Button loginButton;

	/**
	 * Enlace para redirigir a la pantalla de registro de usuario.
	 */
	@FXML
	private Hyperlink registro;

	/**
	 * Enlace para redirigir a la pantalla de recuperación de contraseña.
	 */
	@FXML
	private Hyperlink perdidaContrasena;

	/**
	 * Inicializa los eventos asociados a los elementos de la interfaz. Este método
	 * se llama automáticamente al cargar la vista FXML.
	 */
	@FXML
	public void initialize() {
		// Acción del botón de inicio de sesión
		loginButton.setOnAction(event -> handleLogin());

		// Acción del enlace "No tengo cuenta"
		registro.setOnAction(event -> abrirRegistroUsuario(event));

		// Acción del enlace "He olvidado mi contraseña"
		perdidaContrasena.setOnAction(event -> abrirRecuperarPassword(event));
	}

	/**
	 * Maneja el inicio de sesión verificando las credenciales del usuario. Muestra
	 * un mensaje de éxito o error según el resultado de la autenticación.
	 */
	private void handleLogin() {
		// Verifica si los campos de usuario o contraseña están vacíos
		if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Campos vacíos", "Por favor, introduce tu usuario y contraseña.");
			return;
		}

		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();

		 if (authenticateUser(username, password)) {
		            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
		            Scene scene = null;
		    		try {
		    			scene = new Scene(loader.load());
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}

		            Stage stage = (Stage) loginButton.getScene().getWindow();
		            stage.setScene(scene);
		            stage.show();
		    } else {
		        showAlert(Alert.AlertType.ERROR, "Inicio de sesión fallido", "Usuario o contraseña incorrectos.");
		    }
	}

	/**
	 * Autentica al usuario verificando las credenciales en la base de datos.
	 * 
	 * @param username El nombre de usuario introducido.
	 * @param password La contraseña introducida.
	 * @return true si las credenciales son válidas, false en caso contrario.
	 */
	private boolean authenticateUser(String username, String password) {
		boolean isAuthenticated = false;

		ConexionBD conexionBD = new ConexionBD();
		Connection conexion = conexionBD.conectar();

		String query = "SELECT * FROM Usuario WHERE usuario = ? AND password = ?";

		try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
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

	/**
	 * Abre la pantalla de registro de usuario.
	 * 
	 * @param event El evento asociado al enlace "No tengo cuenta".
	 */
	private void abrirRegistroUsuario(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegistroUsuario.fxml"));
		Scene scene = null;
		try {
			scene = new Scene(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Obtener el Stage actual y cambiar la escena
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Abre la pantalla de recuperación de contraseña.
	 * 
	 * @param event El evento asociado al enlace "He olvidado mi contraseña".
	 */
	private void abrirRecuperarPassword(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RecuperarPassword.fxml"));
		Scene scene = null;
		try {
			scene = new Scene(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Obtener el Stage actual y cambiar la escena
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Muestra una alerta con el tipo, título y contenido especificados.
	 * 
	 * @param alertType El tipo de alerta (ERROR, INFORMATION, etc.).
	 * @param title     El título de la alerta.
	 * @param content   El contenido del mensaje de la alerta.
	 */
	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * Muestra un mensaje informativo con el título y contenido especificados.
	 * 
	 * @param title   El título del mensaje.
	 * @param content El contenido del mensaje.
	 */
	private void showInfo(String title, String content) {
		showAlert(Alert.AlertType.INFORMATION, title, content);
	}
}
