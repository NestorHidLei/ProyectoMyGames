package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Person;
import utils.DateUtil;

/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class PersonEditDialogController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidosField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField usernameField;


    private Stage dialogStage;
    private Person person;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Sets the person to be edited in the dialog.
     * 
     * @param person
     */
    public void setPerson(Person person) {
        this.person = person;

        nombreField.setText(person.getNomrbe());
        apellidosField.setText(person.getApellidos());
        emailField.setText(person.getEmail());
        passwordField.setText(person.getPassword());
        usernameField.setText(person.getUsername());

    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            person.setNombre(nombreField.getText());
            person.setApellidos(apellidosField.getText());
            person.setEmail(emailField.getText());
            person.setPassword(passwordField.getText());
            person.setUsername(usernameField.getText());

            
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nombreField.getText() == null || nombreField.getText().length() == 0) {
            errorMessage += "Nombre no valido\n"; 
        }
        if (apellidosField.getText() == null || apellidosField.getText().length() == 0) {
            errorMessage += "Apellidos no valido\n"; 
        }
        if (emailField.getText() == null || emailField.getText().length() == 0) {
            errorMessage += "Email no valido\n"; 
        }
        if (passwordField.getText() == null || passwordField.getText().length() == 0) {
            errorMessage += "Password no valido\n"; 
        }
        if (usernameField.getText() == null || usernameField.getText().length() == 0) {
            errorMessage += "Username no valido\n"; 
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
     