package tarea_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

	// Variables según tu configuración
	private static final String URL = "jdbc:mysql://localhost:3306/Alumnos24";
	private static final String USER = "root";
	private static final String PASSWORD = "root";

	public static void main(String[] args) {

		// Establecemos la conexión con la BD en nuestro servidor local con el usuario y
		// la password:
		Connection conexion = null;

		try {
			conexion = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("Conexión exitosa a la base de datos.");
		} catch (SQLException e) {
			System.out.println("Error al conectar a la base de datos.");
			e.printStackTrace();
		} finally {
			try {
				if (conexion != null && !conexion.isClosed()) {
					conexion.close();
					System.out.println("Conexión cerrada.");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

	}
}
