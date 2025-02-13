package conexiones;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Usuario;

/**
 * Clase para gestionar la conexión con la base de datos SQLite.
 */
public class ConexionBD {

    private static final String URL = "jdbc:sqlite:src/resources/bd.db"; // Ajuste a la ubicación correcta de la BD

    public Connection conectar() {
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error en la conexión a la BD");
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
        String query = "SELECT UsuarioID, Nombre, Apellidos, email, password, usuario, Deseados, Biblioteca FROM Usuario WHERE usuario = ?";

        try (Connection conexion = conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuario = new Usuario(
                        resultSet.getString("Nombre"),
                        resultSet.getString("Apellidos"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("usuario"),
                        resultSet.getString("Deseados"),
                        resultSet.getString("Biblioteca")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    /**
     * Obtiene la información de un juego por su ID desde la base de datos.
     * 
     * @param id ID del juego a buscar.
     * @return Un array con la información del juego (id, nombre, imagen, rating) o null si no se encuentra.
     */
    public String[] obtenerJuegoPorId(String id) {
        String query = "SELECT Id, Nombre, Imagen, Calificacion FROM Juego WHERE id = ?";
        String[] juego = null;

        try (Connection conexion = conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                juego = new String[] {
                    resultSet.getString("id"),
                    resultSet.getString("nombre"),
                    resultSet.getString("imagen"),
                    resultSet.getString("Calificacion")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return juego;
    }

    /**
     * Obtiene una lista de juegos por múltiples IDs desde la base de datos.
     * 
     * @param ids Lista de IDs de juegos.
     * @return Lista de arrays con información de los juegos.
     */
    public List<String[]> obtenerJuegosPorIds(List<String> ids) {
        List<String[]> juegos = new ArrayList<>();
        String query = "SELECT id, nombre, imagen, Calificacion FROM Juego WHERE id IN (" + String.join(",", ids) + ")";

        try (Connection conexion = conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                juegos.add(new String[]{
                        resultSet.getString("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("imagen"),
                        resultSet.getString("Calificacion")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return juegos;
    }

    /**
     * Agrega un nuevo juego a la base de datos.
     * 
     * @param id ID del juego.
     * @param nombre Nombre del juego.
     * @param imagen URL de la imagen del juego.
     * @param calificacion Calificación del juego.
     * @return `true` si el juego fue agregado correctamente, `false` en caso de error.
     */
    public boolean agregarJuego(String id, String nombre, String imagen, String calificacion) {
        String query = "INSERT INTO Juego (Id, Nombre, Calificacion, Imagen) VALUES (?, ?, ?, ?)";

        try (Connection conexion = conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, imagen);
            preparedStatement.setString(4, calificacion);

            int filasAfectadas = preparedStatement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


