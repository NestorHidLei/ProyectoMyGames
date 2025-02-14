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
class RegistroDesdeLoginTest {
	RegistroUsuarioControlador controlador = new RegistroUsuarioControlador();

	private Label inicio;
    private TextField nombreField;
    private TextField apellidosField;
    private TextField emailField;
    private TextField registroUsernameField;
    private PasswordField registroPasswordField;
    private Button registrarButton;
    private Button cancelarButton;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegistroUsuario.fxml"));
      
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
		controlador = loader.getController();
 
    }
    
    @Order(1)
    @Test
    void testRegistrar(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
        nombreField = (TextField) robot.lookup("#nombreField").query();
        apellidosField = (TextField) robot.lookup("#apellidosField").query();
        emailField = (TextField) robot.lookup("#emailField").query();
        registroUsernameField = (TextField) robot.lookup("#usernameField").query();
        registroPasswordField = (PasswordField) robot.lookup("#passwordField").query();
        registrarButton = (Button) robot.lookup("#registrarButton").query();
        Thread.sleep(2000);
        // Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
            nombreField.setText("Carlos");
            apellidosField.setText("Gómez");
            emailField.setText("carlosgomez@email.com");
            registroUsernameField.setText("carlosg");
            registroPasswordField.setText("password123");
        });

        // Simular clic en "Registrar"
        robot.clickOn(registrarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("El usuario se ha registrado con éxito.",alert.getContentText());
        }

   
    @Test
    void testRegistrarUsuarioYaInsertado(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
        nombreField = (TextField) robot.lookup("#nombreField").query();
        apellidosField = (TextField) robot.lookup("#apellidosField").query();
        emailField = (TextField) robot.lookup("#emailField").query();
        registroUsernameField = (TextField) robot.lookup("#usernameField").query();
        registroPasswordField = (PasswordField) robot.lookup("#passwordField").query();
        registrarButton = (Button) robot.lookup("#registrarButton").query();
        Thread.sleep(2000);
        // Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
            nombreField.setText("Carlos");
            apellidosField.setText("Gómez");
            emailField.setText("carlosgomez@email.com");
            registroUsernameField.setText("carlosg");
            registroPasswordField.setText("password123");
        });

        // Simular clic en "Registrar"
        robot.clickOn(registrarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("El usuario o correo ya existe. Intenta con otros.",alert.getContentText());
        }

    @Test
    void testRegistrarConCamposVacios(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
        nombreField = (TextField) robot.lookup("#nombreField").query();
        apellidosField = (TextField) robot.lookup("#apellidosField").query();
        emailField = (TextField) robot.lookup("#emailField").query();
        registroUsernameField = (TextField) robot.lookup("#usernameField").query();
        registroPasswordField = (PasswordField) robot.lookup("#passwordField").query();
        registrarButton = (Button) robot.lookup("#registrarButton").query();
        Thread.sleep(2000);

        // Simular clic en "Registrar"
        robot.clickOn(registrarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("Por favor, completa todos los campos.",alert.getContentText());
        }
    
    @Test
    void testRegistrarConCampoVacio(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
        nombreField = (TextField) robot.lookup("#nombreField").query();
        apellidosField = (TextField) robot.lookup("#apellidosField").query();
        emailField = (TextField) robot.lookup("#emailField").query();
        registroUsernameField = (TextField) robot.lookup("#usernameField").query();
        registroPasswordField = (PasswordField) robot.lookup("#passwordField").query();
        registrarButton = (Button) robot.lookup("#registrarButton").query();
        Thread.sleep(2000);
        // Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
            nombreField.setText("Carlos");
            apellidosField.setText("Gómez");
            emailField.setText("carlosgomez@email.com");
            registroUsernameField.setText("carlosg");
        });
        
        // Simular clic en "Registrar"
        robot.clickOn(registrarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("Por favor, completa todos los campos.",alert.getContentText());
        }
    
    @Test
    void testBotonCancelar (FxRobot robot) throws InterruptedException {
    	cancelarButton = (Button) robot.lookup("#cancelarButton").query();
    	robot.clickOn(cancelarButton);

    	Thread.sleep(2000);
    	inicio = (Label) robot.lookup("#inicio").query();
        // Verificar que el Label "registroo" contiene "Registro"
    	assertEquals("Login",inicio.getText());
    }
    
}
