package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
		registrarButton.setOnAction(event -> registrarUsuario());

		// Configurar acción para el botón "Cancelar"
		cancelarButton.setOnAction(event -> limpiarCampos());
	}

	private void registrarUsuario() {
		// Validación básica de los campos
		if (nombreField.getText().isEmpty() || apellidosField.getText().isEmpty() || emailField.getText().isEmpty()
				|| passwordField.getText().isEmpty() || usernameField.getText().isEmpty()) {
			System.out.println("Por favor, llena todos los campos.");
			return;
		}

		// Lógica para registrar al usuario (solo muestra en consola por ahora)
		System.out.println("Usuario registrado con éxito:");
		System.out.println("Nombre: " + nombreField.getText());
		System.out.println("Apellidos: " + apellidosField.getText());
		System.out.println("Email: " + emailField.getText());
		System.out.println("Username: " + usernameField.getText());
	}

	private void limpiarCampos() {
		nombreField.clear();
		apellidosField.clear();
		emailField.clear();
		passwordField.clear();
		usernameField.clear();
		System.out.println("Campos limpiados.");
	}

}
