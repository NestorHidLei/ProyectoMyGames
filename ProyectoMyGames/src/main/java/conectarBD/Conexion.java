package conectarBD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

	private  static final String URL = "jdbc:sqlite:src/resources/bd.db"; // Ruta a tu archivo SQLite
	public Connection conectar() {
		Connection conexion = null;

		try {
			conexion = DriverManager.getConnection(URL);
			System.out.println("Conexion OK");
		} catch (SQLException e) {
			System.out.println("Error en la conexion");
			e.printStackTrace();
		}

		return conexion;
	}

}