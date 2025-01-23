package conexiones;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión con la base de datos SQLite.
 * 
 * Esta clase establece una conexión con la base de datos especificada en la
 * ruta definida y permite que otras clases interactúen con ella.
 */
public class ConexionBD {

	/**
	 * URL de la base de datos SQLite. Especifica la ubicación del archivo de la
	 * base de datos.
	 */
	private static final String URL = "jdbc:sqlite:src/resources/bd.db"; // Ruta al archivo SQLite

	/**
	 * Establece y devuelve una conexión con la base de datos.
	 * 
	 * @return Un objeto {@link Connection} si la conexión es exitosa; null si
	 *         ocurre un error.
	 */
	public Connection conectar() {
		Connection conexion = null;

		try {
			// Intenta establecer la conexión con la base de datos
			conexion = DriverManager.getConnection(URL);
			System.out.println("Conexion OK");
		} catch (SQLException e) {
			// Maneja errores de conexión
			System.out.println("Error en la conexion");
			e.printStackTrace();
		}

		return conexion;
	}
}