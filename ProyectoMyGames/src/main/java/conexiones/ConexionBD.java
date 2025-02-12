package conexiones;

import model.Usuario;
import java.sql.*;

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
}
