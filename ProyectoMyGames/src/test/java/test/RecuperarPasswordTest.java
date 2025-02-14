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

import controllers.RecuperarPasswordControlador;
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
class RecuperarPasswordTest {
	RecuperarPasswordControlador controlador = new RecuperarPasswordControlador();

	private Label inicio;

    private TextField usernameField;
    private Button aceptarButton;
    private Button cancelarButton;

    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RecuperarPassword.fxml"));
      
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
		controlador = loader.getController();
 
    }
    
    @Test
    void testCorrecto(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
    	usernameField = (TextField) robot.lookup("#usernameField").query();
    	aceptarButton = (Button) robot.lookup("#aceptarButton").query();
        Thread.sleep(2000);
        // Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
            usernameField.setText("carlosg");
        });

        // Simular clic en "Registrar"
        robot.clickOn(aceptarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("Se ha enviado la nueva contraseña a su correo.",alert.getContentText());
        }

   
    @Test
    void testCancelar(FxRobot robot) throws InterruptedException {
        // Obtener elementos de la pantalla de registro
    	cancelarButton = (Button) robot.lookup("#cancelarButton").query();

        // Simular clic en "Registrar"
        robot.clickOn(cancelarButton);
        Thread.sleep(2000);
        inicio = (Label) robot.lookup("#inicio").query();
        // Verificar que el Label "registroo" contiene "Registro"
    	assertEquals("Login",inicio.getText());
        }

    @Test
    void testIncorrecto(FxRobot robot) throws InterruptedException {
    	  // Obtener elementos de la pantalla de registro
    	usernameField = (TextField) robot.lookup("#usernameField").query();
    	aceptarButton = (Button) robot.lookup("#aceptarButton").query();
        Thread.sleep(2000);
        // Simular entrada de datos de usuario nuevo
        robot.interact(() -> {
            usernameField.setText("abbcd");
        });

        // Simular clic en "Registrar"
        robot.clickOn(aceptarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("El usuario no existe en la base de datos.",alert.getContentText());
        }
    
    @Test
    void testrConCampoVacio(FxRobot robot) throws InterruptedException {
    	aceptarButton = (Button) robot.lookup("#aceptarButton").query();
        Thread.sleep(2000);
        

        // Simular clic en "Registrar"
        robot.clickOn(aceptarButton);
        Thread.sleep(2000);
        Alert alert = controlador.getAlert();
        
        assertEquals("El campo de usuario no puede estar vacío.",alert.getContentText());
        }
    
    
}