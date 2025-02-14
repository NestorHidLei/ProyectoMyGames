package test;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.RegistroUsuarioControlador;
import controllers.iniciarSesionControlador;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

import org.testfx.api.FxRobot;

@ExtendWith(ApplicationExtension.class)
class InicioSesiontest {
	iniciarSesionControlador controlador = new iniciarSesionControlador();

	private Label labelRegistro;
	private Label recuperarContrasena;
	private TextField usernameField;
	private PasswordField passwordField;
	private Button loginButton;
	private Hyperlink registro;
	private Hyperlink perdidaContrasena;
	private Label resultado;

	@Start
	private void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/iniciarSesion.fxml"));

		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		controlador = loader.getController();

	}

	@Test
	void testNavegacionRegistro(FxRobot robot) throws InterruptedException {
		registro = (Hyperlink) robot.lookup("#registro").query();
		robot.clickOn(registro);

		Thread.sleep(2000);

		labelRegistro = (Label) robot.lookup("#labelRegistro").query();
		// Verificar que el Label "registroo" contiene "Registro"
		assertEquals("Registro", labelRegistro.getText());
	}

	@Test
	void testNavegacionRecuperar(FxRobot robot) throws InterruptedException {
		perdidaContrasena = (Hyperlink) robot.lookup("#perdidaContrasena").query();
		robot.clickOn(perdidaContrasena);

		Thread.sleep(2000);

		recuperarContrasena = (Label) robot.lookup("#recuperarContrasena").query();
		// Verificar que el Label "registroo" contiene "Registro"
		assertEquals("Recuperar Contraseña", recuperarContrasena.getText());
	}

	@Test
	void testCamposVacio(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
        
		robot.clickOn(loginButton);
		
		Alert alert = controlador.getAlert();
        
        assertEquals("Por favor, introduce tu usuario y contraseña.",alert.getContentText());
	}
	
	@Test
	void testContrasenaVacio(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
		
		// Simular entrada de datos de usuario nuevo
        robot.interact(() -> {

        	usernameField.setText("carlosg");
        });
		robot.clickOn(loginButton);
		
		Alert alert = controlador.getAlert();
        
        assertEquals("Por favor, introduce tu usuario y contraseña.",alert.getContentText());
	}
	
	@Test
	void testUsuairoVacio(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
		
		// Simular entrada de datos de usuario nuevo
        robot.interact(() -> {

        	passwordField.setText("carlosg");
        });
		robot.clickOn(loginButton);
		
		Alert alert = controlador.getAlert();
        
        assertEquals("Por favor, introduce tu usuario y contraseña.",alert.getContentText());
	}
	
	@Test
	void testUsuarioIncorrecto(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
		
		// Simular entrada de datos de usuario nuevo
        robot.interact(() -> {

        	usernameField.setText("sgrgsdfefs");
        	passwordField.setText("carlosg");
        });
		robot.clickOn(loginButton);
		
		Alert alert = controlador.getAlert();
        
        assertEquals("Usuario o contraseña incorrectos.",alert.getContentText());
	}
	
	@Test
	void testContrasenaIncorrecta(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
		
		// Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
        	passwordField.setText("sgrgsdfefs");
        	usernameField.setText("carlosg");
        });
		robot.clickOn(loginButton);
		
		Alert alert = controlador.getAlert();
        
        assertEquals("Usuario o contraseña incorrectos.",alert.getContentText());
	}
	
	@Test
	void testAcceso(FxRobot robot) throws InterruptedException {
		usernameField = (TextField) robot.lookup("#usernameField").query();
		passwordField = (PasswordField) robot.lookup("#passwordField").query();
		loginButton = (Button) robot.lookup("#loginButton").query();
		
		
		// Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
        	passwordField.setText("password123");
        	usernameField.setText("carlosg");
        });
		robot.clickOn(loginButton);
		
		resultado = (Label) robot.lookup("#resultado").query();
        
        assertEquals("RESULTADOS DE BÚSQUEDA",resultado.getText());
	}
}
