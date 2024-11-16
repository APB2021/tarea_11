package tarea_11;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GestorAlumnosBD {

	private static final Scanner sc = new Scanner(System.in);

	/**
	 * Método para insertar un alumno en la base de datos.
	 * 
	 * @param conexionBD es el objeto con la conexión a la BD.
	 * @author Alberto Polo
	 */
	public void insertarAlumno(ConexionBD conexionBD) {
		int nia = obtenerNIA(conexionBD);
		String nombre = obtenerTexto("Nombre del alumno: ");
		String apellidos = obtenerTexto("Apellidos: ");
		char genero = obtenerGenero();
		java.sql.Date fechaNacimiento = obtenerFechaNacimiento();
		String ciclo = obtenerTexto("Ciclo: ");
		String curso = obtenerTexto("Curso: ");
		String grupo = obtenerTexto("Grupo: ");

		Alumno alumno = new Alumno(nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo);

		try (Connection conexion = conexionBD.obtenerConexion()) {
			if (conexion != null) {
				insertarAlumnoEnBD(conexion, alumno);
			} else {
				mostrarMensaje("No se pudo establecer la conexión a la base de datos.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para obtener el nia del alumno que es generado por la BD.
	 * 
	 * @param conexionBD es el objeto con la conexión a la BD.
	 * @return Devuelve el nia del alumno que se obtiene de la BD porque es
	 *         autoincremental.
	 * @author Alberto Polo
	 */
	private int obtenerNIA(ConexionBD conexionBD) {
		// Este método obtiene el nia automáticamente de la base de datos
		try (Connection conexion = conexionBD.obtenerConexion()) {
			String sql = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'alumno' AND table_schema = DATABASE();";
			try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
				try (java.sql.ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("AUTO_INCREMENT"); // Devolverá el próximo valor de autoincremento
					}
				}
			}
		} catch (SQLException e) {
			mostrarMensaje("Error al obtener el nia: " + e.getMessage());
		}
		return -1; // Valor predeterminado en caso de error
	}

	/**
	 * Método para recoger la respuesta del usuario.
	 * 
	 * @param mensaje es un String con el contenido del mensaje en cada supuesto.
	 * @return Devuelve la respuesta del usuario como String al mensaje anterior
	 *         según convenga.
	 * @author Alberto Polo
	 */
	private String obtenerTexto(String mensaje) {
		System.out.print(mensaje);
		return sc.nextLine().toUpperCase();
	}

	/**
	 * Método para obtener el género del alumno.
	 * 
	 * @return devolverá 'M' o 'F' si el usuario responde correctamente.
	 * @author Alberto Polo
	 */
	private char obtenerGenero() {
		while (true) {
			System.out.print("Género (M/F): ");
			String respuestaGenero = sc.nextLine().toUpperCase();
			if (respuestaGenero.equals("M") || respuestaGenero.equals("F"))
				return respuestaGenero.charAt(0);
			mostrarMensaje("Por favor, teclee 'M' para Masculino o 'F' para Femenino.");
		}
	}

	/**
	 * Método que se ocupa de recoger la fecha de nacimiento del alumno y
	 * convertirla al formato java.sql.Date
	 * 
	 * @return devuelve la fecha en formato sql para la BD MySQL
	 * @author Alberto Polo
	 */
	private java.sql.Date obtenerFechaNacimiento() {
		while (true) {
			System.out.print("Ingrese la Fecha de Nacimiento (dd-MM-yyyy): ");
			String inputFecha = sc.nextLine();
			try {
				// Crear el formateador para el formato 'dd-MM-yyyy'
				java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				// Convertir la fecha ingresada en LocalDate
				java.time.LocalDate fecha = java.time.LocalDate.parse(inputFecha, formatter);
				// Convertir LocalDate a java.sql.Date
				return java.sql.Date.valueOf(fecha);
			} catch (java.time.format.DateTimeParseException e) {
				mostrarMensaje("Formato de fecha incorrecto. El formato debe ser dd-MM-aaaa.");
			}
		}
	}

	private void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}

	/**
	 * Método para insertar 1 alumno en la BD MySQL
	 * 
	 * @param conexion este primer parámetro recibe el objeto de la conexión a la
	 *                 BD.
	 * @param alumno   el segundo parámetro recibe el objeto de la clase Alumno
	 * @author Alberto Polo
	 */
	private void insertarAlumnoEnBD(Connection conexion, Alumno alumno) {
		String sql = "INSERT INTO alumno ( nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) "
				+ "VALUES ( ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = conexion.prepareStatement(sql)) {

			stmt.setString(1, alumno.getNombre());
			stmt.setString(2, alumno.getApellidos());
			stmt.setString(3, String.valueOf(alumno.getGenero()));

			java.sql.Date fecha = (Date) alumno.getFechaNacimiento();
			stmt.setDate(4, new java.sql.Date(fecha.getTime()));

			stmt.setString(5, alumno.getCiclo());
			stmt.setString(6, alumno.getCurso());
			stmt.setString(7, alumno.getGrupo());

			int filasInsertadas = stmt.executeUpdate();
			mostrarMensaje(filasInsertadas > 0 ? "Alumno correctamente insertado en la BD."
					: "No se pudo insertar el alumno en  la BD.");
		} catch (SQLException e) {
			mostrarMensaje("Error al insertar el alumno: " + e.getMessage());
		}
	}

	public void mostrarAlumnosEnBD(Connection conexion) {
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		try (PreparedStatement stmt = conexion.prepareStatement(sql);
				ResultSet resultado = stmt.executeQuery()) {

			System.out.println("Lista de Alumnos:");
			System.out.println("======================================");

			while (resultado.next()) {
				int nia = resultado.getInt("nia");
				String nombre = resultado.getString("nombre");
				String apellidos = resultado.getString("apellidos");
				char genero = resultado.getString("genero").charAt(0);
				java.sql.Date fechaNacimiento = resultado.getDate("fechaNacimiento");
				String ciclo = resultado.getString("ciclo");
				String curso = resultado.getString("curso");
				String grupo = resultado.getString("grupo");

				// Formato salida en consola:
				System.out.printf(
						"NIA: %d, Nombre: %s, Apellidos: %s, Género: %c, Fecha de Nacimiento: %s, Ciclo: %s, Curso: %s, Grupo: %s%n",
						nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo);
			}
		} catch (SQLException e) {
			System.out.println("Error al obtener la lista de alumnos: " + e.getMessage());
		}
	}

}
