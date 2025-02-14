package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import conexiones.ConexionBD;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import model.Usuario;

/**
 * Controlador para la vista de usuario.
 * Permite visualizar la información del usuario y realizar cambios como actualizar la contraseña.
 */
public class UserViewControlador extends Navegacion {

	private Usuario usuario;

	@FXML
	private Label NombreField, ApellidoField, EmailFiel, DeseadosField, BibliotecaFild, usernameField;

	@FXML
	private Button cerrarSesionButton, cambiarPassword, inicio, deseados, biblioteca, user;

	@FXML
	private PasswordField nuevaPassword;

	/**
	 * Inicializa la vista y configura los eventos de los botones de navegación y acciones.
	 */
	@FXML
	public void initialize() {
		cerrarSesionButton.setOnAction(event -> cerrarSesion(event));
		cambiarPassword.setOnAction(event -> cambiarPassword());
		inicio.setOnAction(event -> abrirInicio(event, usuario));
		deseados.setOnAction(event -> abrirDeseados(event, usuario));
		biblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
	}

	/**
	 * Asigna los datos del usuario autenticado a la vista.
	 *
	 * @param usuario Usuario autenticado en la sesión.
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;

		if (usuario != null) {
			NombreField.setText(usuario.getNombre());
			ApellidoField.setText(usuario.getApellidos());
			EmailFiel.setText(usuario.getEmail());
			usernameField.setText(usuario.getUsername());

			// Mostrar el número de juegos deseados y en biblioteca
			DeseadosField.setText(usuario.getJuegosDeseados() != null ? String.valueOf(usuario.getJuegosDeseados().size()) : "0");
			BibliotecaFild.setText(usuario.getJuegosBiblioteca() != null ? String.valueOf(usuario.getJuegosBiblioteca().size()) : "0");
		}
	}

	/**
	 * Maneja la acción de cerrar sesión y redirige a la pantalla de inicio de sesión.
	 *
	 * @param event Evento de acción del botón "Cerrar Sesión".
	 */
	@FXML
	private void cerrarSesion(ActionEvent event) {
		try {
			// Cargar la nueva vista de inicio de sesión
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/IniciarSesion.fxml"));
			Parent root = loader.load();

			// Obtener la escena actual y cambiarla
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace(); // Manejo de error si no se encuentra el archivo FXML
		}
	}

	/**
	 * Cambia la contraseña del usuario autenticado en la base de datos.
	 */
	private void cambiarPassword() {
		String username = usuario.getUsername();

		// Validar que el campo de nueva contraseña no esté vacío
		if (nuevaPassword.getText().trim().isEmpty()) {
			mostrarAlerta(Alert.AlertType.ERROR, "Error", "El campo de contraseña no puede estar vacío.");
			return;
		}

		// Consulta SQL para actualizar la contraseña
		String actualizarPassword = "UPDATE usuario SET password = ? WHERE usuario = ?";
		ConexionBD conexionBD = new ConexionBD();
		try (Connection conexion = conexionBD.conectar();
		     PreparedStatement psUpdate = conexion.prepareStatement(actualizarPassword)) {

			// Establecer los valores en la consulta
			psUpdate.setString(1, nuevaPassword.getText());
			psUpdate.setString(2, username);

			int filasActualizadas = psUpdate.executeUpdate();

			// Mostrar mensaje de éxito o advertencia según el resultado
			if (filasActualizadas > 0) {
				mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Se ha cambiado la contraseña con éxito.");
			} else {
				mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se encontró el usuario.");
			}

			// Limpiar el campo de contraseña
			nuevaPassword.clear();

		} catch (SQLException e) {
			e.printStackTrace();
			mostrarAlerta(Alert.AlertType.ERROR, "Error", "Hubo un problema al procesar la solicitud.");
		}
	}

	/**
	 * Muestra una alerta con el tipo, título y mensaje especificados.
	 *
	 * @param tipo    Tipo de alerta (ERROR, INFORMATION, WARNING).
	 * @param titulo  Título de la alerta.
	 * @param mensaje Mensaje a mostrar en la alerta.
	 */
	private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}
}

