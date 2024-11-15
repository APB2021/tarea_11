package tarea_11;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;

public class GestorAlumnosBD {

	private static final Scanner sc = new Scanner(System.in);

	// Método para insertar un alumno en la base de datos

	public void insertarAlumno(ConexionBD conexionBD) {
		int nia = obtenerNIA(conexionBD);
		String nombre = obtenerTexto("Ingrese el Nombre: ");
		String apellidos = obtenerTexto("Ingrese los Apellidos: ");
		char genero = obtenerGenero();
		java.sql.Date fechaNacimiento = obtenerFechaNacimiento();
		String ciclo = obtenerTexto("Ingrese el Ciclo: ");
		String curso = obtenerTexto("Ingrese el Curso: ");
		String grupo = obtenerTexto("Ingrese el Grupo: ");

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

	private int obtenerNIA(ConexionBD conexionBD) {
		// Este método obtiene el NIA automáticamente de la base de datos
		try (Connection conexion = conexionBD.obtenerConexion()) {
			String sql = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'alumno' AND table_schema = DATABASE();";
			try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
				try (java.sql.ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("AUTO_INCREMENT"); // Devolver el próximo valor de autoincremento
					}
				}
			}
		} catch (SQLException e) {
			mostrarMensaje("Error al obtener el NIA: " + e.getMessage());
		}
		return -1; // Valor predeterminado en caso de error
	}

	private String obtenerTexto(String mensaje) {
		System.out.print(mensaje);
		return sc.nextLine().toUpperCase();
	}

	private char obtenerGenero() {
		while (true) {
			System.out.print("Ingrese el Género (M/F): ");
			String input = sc.nextLine().toUpperCase();
			if (input.equals("M") || input.equals("F"))
				return input.charAt(0);
			mostrarMensaje("Por favor, ingrese 'M' para Masculino o 'F' para Femenino.");
		}
	}

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
				mostrarMensaje("Formato de fecha inválido. El formato debe ser dd-MM-yyyy.");
			}
		}
	}

	private void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}

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
			mostrarMensaje(
					filasInsertadas > 0 ? "Alumno insertado en la base de datos." : "No se pudo insertar el alumno.");
		} catch (SQLException e) {
			mostrarMensaje("Error al insertar el alumno: " + e.getMessage());
		}
	}
}
