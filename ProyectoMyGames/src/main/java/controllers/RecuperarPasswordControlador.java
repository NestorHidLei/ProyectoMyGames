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

public class RecuperarPasswordControlador {

    final String remitente = "maperaza2005@gmail.com"; // Cambia a tu correo
    final String contrasena = "xvmf icpv yyel ijjl"; // Cambia a tu contraseña o app password

    @FXML
    private TextField usernameField;

    @FXML
    private Button aceptarButton;

    @FXML
    private Button cancelarButton;

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

    private void recuperarPassword(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El campo de usuario no puede estar vacío.");
            return;
        }

        ConexionBD conexionBD = new ConexionBD();
        Connection conexion = conexionBD.conectar();

        String consultaUsuario = "SELECT email FROM usuario WHERE username = ?";
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
            String actualizarPassword = "UPDATE usuario SET password = ? WHERE username = ?";
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

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

    private void cargarInicioSesion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/IniciarSesion.fxml"));
        Scene scene = new Scene(loader.load());

        // Obtener el Stage actual y cambiar la escena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
