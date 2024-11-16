package tarea_11;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

	private static final String URL = "jdbc:mysql://localhost:3306/Alumnos24";
	private static final String USER = "alberto";
	private static final String PASSWORD = "alberto";

	/**
	 * Método para obtener una conexión a la Base de Datos.
	 * 
	 * @return Devuelve un objeto Connection ("conexion").
	 * @author Alberto Polo
	 */
	public Connection obtenerConexion() {
		Connection conexion = null;
		String mensaje = "Conexión ESTABLECIDA con BD: ";

		try {
			// Primero obtenemos la conexión
			conexion = DriverManager.getConnection(URL, USER, PASSWORD);

			// Obtenemos el nombre de la base de datos
			String nombreConexion = conexion.getCatalog();

			// Imprimimos el mensaje de conexión de manera visual
			imprimirDelimitadores(mensaje.length() + nombreConexion.length());
			System.out.println(mensaje + nombreConexion);
			imprimirDelimitadores(mensaje.length() + nombreConexion.length());

		} catch (SQLException e) {
			System.err.println("Error de conexión: " + e.getMessage());
		}

		return conexion;
	}

	/**
	 * Método para imprimir una línea de delimitadores visuales.
	 * 
	 * @param longitud Longitud del mensaje a mostrar.
	 * @author Alberto Polo
	 */
	private void imprimirDelimitadores(int longitud) {
		for (int i = 0; i < longitud; i++) {
			System.out.print("=");
		}
		System.out.println();
	}
}
