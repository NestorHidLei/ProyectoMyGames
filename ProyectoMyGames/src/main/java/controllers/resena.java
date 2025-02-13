package controllers;


import conexiones.ConexionBD;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la pantalla de reseñas de juegos.
 */
public class resena {

    @FXML
    private TextArea texto;
    
    @FXML
    private Button modificar;
    
    @FXML
    private Button publicar;

    private ConexionBD conexionBD;
    private String juegoId;
    private Usuario usuario;

    /**
     * Método para inicializar la vista con los datos necesarios.
     * @param juegoId ID del juego cuya reseña se va a cargar.
     * @param usuario Usuario actual.
     */
    public void inicializar(String juegoId, Usuario usuario) {
        this.conexionBD = new ConexionBD();
        this.juegoId = juegoId;
        this.usuario = usuario;
        cargarReseña();
        publicar.setOnAction(event -> guardarReseña());
        modificar.setOnAction(event -> modificarReseña());
    }

    /**
     * Carga la reseña del usuario para el juego si existe.
     */
    private void cargarReseña() {
        String query = "SELECT Texto FROM Reseña WHERE JuegoID = ? AND UsuarioID = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, juegoId);
            preparedStatement.setString(2, usuario.getUsername()); // Suponiendo que UsuarioID es el username

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Si ya existe una reseña, la mostramos y deshabilitamos edición
                texto.setText(resultSet.getString("Texto"));
                texto.setEditable(false);
                publicar.setDisable(true);
                modificar.setDisable(false);
            } else {
                // Si no hay reseña, habilitamos la edición y el botón "Guardar"
                texto.setText("");
                texto.setEditable(true);
                publicar.setDisable(false);
                modificar.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Habilita la edición de la reseña para que pueda ser modificada.
     */
    @FXML
    private void modificarReseña() {
        texto.setEditable(true);
        publicar.setDisable(false);
        modificar.setDisable(true);
    }

    /**
     * Guarda o actualiza la reseña en la base de datos.
     */
    @FXML
    private void guardarReseña() {
        String nuevaReseña = texto.getText().trim();

        if (nuevaReseña.isEmpty()) {
            System.out.println("La reseña no puede estar vacía.");
            return;
        }

        String queryExiste = "SELECT COUNT(*) FROM Reseña WHERE JuegoID = ? AND UsuarioID = ?";
        String queryInsert = "INSERT INTO Reseña (JuegoID, UsuarioID, Texto) VALUES (?, ?, ?)";
        String queryUpdate = "UPDATE Reseña SET Texto = ? WHERE JuegoID = ? AND UsuarioID = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement checkStmt = conexion.prepareStatement(queryExiste)) {

            checkStmt.setString(1, juegoId);
            checkStmt.setString(2, usuario.getUsername());

            ResultSet resultSet = checkStmt.executeQuery();
            boolean existeReseña = resultSet.next() && resultSet.getInt(1) > 0;

            try (PreparedStatement stmt = conexion.prepareStatement(existeReseña ? queryUpdate : queryInsert)) {
                if (existeReseña) {
                    stmt.setString(1, nuevaReseña);
                    stmt.setString(2, juegoId);
                    stmt.setString(3, usuario.getUsername());
                } else {
                    stmt.setString(1, juegoId);
                    stmt.setString(2, usuario.getUsername());
                    stmt.setString(3, nuevaReseña);
                }

                int filasAfectadas = stmt.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("Reseña guardada exitosamente.");
                    texto.setEditable(false);
                    publicar.setDisable(true);
                    modificar.setDisable(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
