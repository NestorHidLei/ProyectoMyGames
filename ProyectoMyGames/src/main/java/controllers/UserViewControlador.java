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

public class UserViewControlador extends Navegacion {

	private Usuario usuario;

	@FXML
	private Label NombreField;
	@FXML
	private Label ApellidoField;
	@FXML
	private Label EmailFiel;
	@FXML
	private Label DeseadosField;
	@FXML
	private Label BibliotecaFild;
	@FXML
	private Label usernameField;

	@FXML
	private Button cerrarSesionButton;
	@FXML
	private Button cambiarPassword;
	@FXML
	private Button inicio;
	@FXML
	private Button deseados;
	@FXML
	private Button biblioteca;
	@FXML
	private Button user;
	
	@FXML
	private PasswordField nuevaPassword;

	@FXML
	public void initialize() {
		cerrarSesionButton.setOnAction(event -> cerrarSesion(event));
		cambiarPassword.setOnAction(event -> cambiarPassword());
        inicio.setOnAction(event -> abrirInicio(event, usuario));
		deseados.setOnAction(event -> abrirDeseados(event, usuario));
		biblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
	}

	/**
	 * Método para recibir el usuario autenticado y actualizar la UI con sus datos.
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;

		if (usuario != null) {
			NombreField.setText(usuario.getNombre());
			ApellidoField.setText(usuario.getApellidos());
			EmailFiel.setText(usuario.getEmail());
			usernameField.setText(usuario.getUsername());
			// Mostrar el número de juegos deseados y en biblioteca
			if (usuario.getJuegosDeseados() != null) {
				DeseadosField.setText(String.valueOf(usuario.getJuegosDeseados().size()));
			} else {
				DeseadosField.setText("0");
			}

			if (usuario.getJuegosBiblioteca() != null) {
				BibliotecaFild.setText(String.valueOf(usuario.getJuegosBiblioteca().size()));
			} else {
				BibliotecaFild.setText("0");
			}
		}
	}

	/**
	 * Método para manejar el botón "Cerrar Sesión".
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

	private void cambiarPassword() {
	    String username = usuario.getUsername();

	    // Validar que el campo de nueva contraseña no esté vacío
	    if (nuevaPassword.getText().trim().isEmpty()) {
	        mostrarAlerta(Alert.AlertType.ERROR, "Error", "El campo de contraseña no puede estar vacío.");
	        return;
	    }

	    // Conexión a la base de datos
	    String actualizarPassword = "UPDATE usuario SET password = ? WHERE usuario = ?";
	    ConexionBD conexionBD = new ConexionBD();
        Connection conexion = conexionBD.conectar();
	    try (PreparedStatement psUpdate = conexion.prepareStatement(actualizarPassword)) {

	        // Establecer los valores en la consulta
	        psUpdate.setString(1, nuevaPassword.getText());
	        psUpdate.setString(2, username);

	        int filasActualizadas = psUpdate.executeUpdate();

	        if (filasActualizadas > 0) {
	            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Se ha cambiado la nueva contraseña.");
	        } else {
	            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se encontró el usuario.");
	        }
	        
	        nuevaPassword.clear();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        mostrarAlerta(Alert.AlertType.ERROR, "Error", "Hubo un problema al procesar la solicitud.");
	    }
	}


	/**
	 * Muestra una alerta con el tipo, título y mensaje especificados.
	 * 
	 * @param tipo    El tipo de alerta (ERROR, INFORMATION, etc.).
	 * @param titulo  El título de la alerta.
	 * @param mensaje El mensaje de la alerta.
	 */
	private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}

}
