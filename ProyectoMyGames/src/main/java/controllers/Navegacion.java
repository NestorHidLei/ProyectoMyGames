package controllers;

import java.io.IOException;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import model.Usuario;

public class Navegacion {

    /**
     * Cambia la vista actual en la misma ventana y asigna el usuario al controlador correcto.
     * 
     * @param event    Evento del botón o componente de la UI.
     * @param rutaFXML Ruta del archivo FXML de la vista.
     * @param usuario  Objeto usuario a pasar al controlador.
     */
    private void cambiarVista(Event event, String rutaFXML, Usuario usuario) {
        if (usuario == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Usuario no encontrado.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();

            // Obtener el controlador correcto y asignar el usuario
            Object controlador = loader.getController();

            if (controlador instanceof HomeControlador) {
                ((HomeControlador) controlador).setUsuario(usuario);
            } else if (controlador instanceof DeseadosControlador) {
                ((DeseadosControlador) controlador).setUsuario(usuario);
            } else if (controlador instanceof BibliotecaControlador) {
                ((BibliotecaControlador) controlador).setUsuario(usuario);
            } else if (controlador instanceof UserViewControlador) {
                ((UserViewControlador) controlador).setUsuario(usuario);
            }

            // Obtener el Stage actual y cambiar la escena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la pantalla: " + rutaFXML);
        }
    }

    // Métodos públicos para cambiar de vista
    public void abrirInicio(Event event, Usuario usuario) {
        cambiarVista(event, "/views/Home.fxml", usuario);
    }

    public void abrirDeseados(Event event, Usuario usuario) {
        cambiarVista(event, "/views/Deseados.fxml", usuario);
    }

    public void abrirBiblioteca(Event event, Usuario usuario) {
        cambiarVista(event, "/views/Biblioteca.fxml", usuario);
    }

    public void abrirUser(Event event, Usuario usuario) {
        cambiarVista(event, "/views/UserView.fxml", usuario);
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
}


