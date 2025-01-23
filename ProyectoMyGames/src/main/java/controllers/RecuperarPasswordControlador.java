package controllers;

import javax.mail.*;
import javax.mail.internet.*;

import conexiones.ConexionBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Controlador para la funcionalidad de recuperación de contraseña.
 * 
 * Esta clase permite al usuario recuperar su contraseña mediante un proceso
 * que incluye la generación de una nueva contraseña aleatoria, su actualización
 * en la base de datos y el envío de la nueva contraseña por correo electrónico.
 */
public class RecuperarPasswordControlador {

    /**
     * Correo electrónico del remitente para el envío de correos.
     */
    final String remitente = "gamedex.sprt@gmail.com";

    /**
     * Contraseña o clave de aplicación asociada al correo remitente.
     */
    final String contrasena = "wuxq lmcd aqgm kujc";

    /**
     * Campo de texto para que el usuario introduzca su nombre de usuario.
     */
    @FXML
    private TextField usernameField;

    /**
     * Botón para aceptar y proceder con la recuperación de contraseña.
     */
    @FXML
    private Button aceptarButton;

    /**
     * Botón para cancelar la acción y volver a la pantalla de inicio de sesión.
     */
    @FXML
    private Button cancelarButton;

    /**
     * Inicializa los eventos asociados a los elementos de la interfaz.
     * Este método se llama automáticamente al cargar la vista FXML.
     */
    @FXML
    public void initialize() {
        // Configurar acción para el botón "Aceptar"
        aceptarButton.setOnAction(event -> {
            try {
                recuperarPassword(event);
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
     * Recupera la contraseña del usuario generando una nueva, actualizándola en la base de datos
     * y enviándola al correo electrónico asociado al usuario.
     * 
     * @param event El evento asociado al botón "Aceptar".
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     * @throws IOException  Si ocurre un error al cargar la siguiente vista.
     */
    private void recuperarPassword(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El campo de usuario no puede estar vacío.");
            return;
        }

        ConexionBD conexionBD = new ConexionBD();
        Connection conexion = conexionBD.conectar();

        String consultaUsuario = "SELECT email FROM usuario WHERE usuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(consultaUsuario)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El usuario no existe en la base de datos.");
                return;
            }

            String email = rs.getString("email");

            // Generar nueva contraseña
            String nuevaPassword = generarStringAleatorio(8);

            // Actualizar la contraseña en la base de datos
            String actualizarPassword = "UPDATE usuario SET password = ? WHERE usuario = ?";
            try (PreparedStatement psUpdate = conexion.prepareStatement(actualizarPassword)) {
                psUpdate.setString(1, nuevaPassword);
                psUpdate.setString(2, username);
                psUpdate.executeUpdate();
            }

            // Enviar correo con la nueva contraseña
            enviaCorreo(email, nuevaPassword);

            // Mostrar alerta de éxito y cargar inicio de sesión
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Se ha enviado la nueva contraseña a su correo.");
            cargarInicioSesion(event);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Hubo un problema al procesar la solicitud.");
        } finally {
            if (conexion != null) {
                conexion.close();
            }
        }
    }

    /**
     * Envía un correo electrónico con la nueva contraseña generada al destinatario especificado.
     * 
     * @param destinatario  El correo electrónico del destinatario.
     * @param nuevaPassword La nueva contraseña generada.
     */
    private void enviaCorreo(String destinatario, String nuevaPassword) {
        // Configuración de propiedades para el servidor SMTP
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");

        // Autenticación del correo
        Session sesion = Session.getInstance(propiedades, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, contrasena);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Recuperación de contraseña MyGames");
            mensaje.setText("La nueva contraseña para entrar a la aplicación es: \"" + nuevaPassword
                    + "\"\nPara cambiar la contraseña necesita cambiarla desde dentro, en el apartado de ajustes.");

            // Enviar el correo
            Transport.send(mensaje);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo.");
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

    /**
     * Genera un string aleatorio de la longitud especificada.
     * 
     * @param longitud La longitud del string aleatorio.
     * @return Un string aleatorio generado.
     */
    public static String generarStringAleatorio(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            int index = random.nextInt(caracteres.length());
            sb.append(caracteres.charAt(index));
        }

        return sb.toString();
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
