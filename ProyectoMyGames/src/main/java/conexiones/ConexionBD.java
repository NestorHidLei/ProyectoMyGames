package conexiones;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para gestionar la conexión con la base de datos SQLite.
 */
public class ConexionBD {

    private static final String URL = "jdbc:sqlite:src/resources/bd.db";

    public Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexión OK");
        } catch (SQLException e) {
            System.out.println("Error en la conexión");
            e.printStackTrace();
        }
        return conexion;
    }

    /**
     * Obtiene un usuario de la base de datos por su nombre de usuario.
     * 
     * @param username Nombre de usuario.
     * @return Objeto `Usuario` si existe, `null` si no se encuentra.
     */
    public Usuario obtenerUsuarioPorNombre(String username) {
        Usuario usuario = null;
        String query = "SELECT nombre, apellidos, email, password, usuario FROM Usuario WHERE usuario = ?";
        
        try (Connection conexion = conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuario = new Usuario(
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidos"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("usuario")
                );

                // Cargar lista de juegos deseados
                usuario.setJuegosDeseados(obtenerJuegosPorUsuario(conexion, username, "Deseados"));

                // Cargar lista de juegos en la biblioteca
                usuario.setJuegosBiblioteca(obtenerJuegosPorUsuario(conexion, username, "Biblioteca"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Obtiene la lista de juegos de un usuario según la categoría.
     * 
     * @param conexion Conexión a la base de datos.
     * @param username Nombre de usuario.
     * @param categoria Tipo de juegos a obtener ("Deseados" o "Biblioteca").
     * @return Lista de IDs de juegos.
     */
    private List<Integer> obtenerJuegosPorUsuario(Connection conexion, String username, String categoria) {
        List<Integer> juegos = new ArrayList<>();
        String query = "SELECT juego_id FROM Juegos WHERE usuario = ? AND categoria = ?";

        try (PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, categoria);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                juegos.add(resultSet.getInt("juego_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return juegos;
    }
}

