package tarea_11;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
		try (Connection conexion = conexionBD.obtenerConexion()) {
			if (conexion != null) {
				int nia = obtenerNIA(conexionBD);
				String nombre = obtenerTexto("Nombre del alumno: ");
				String apellidos = obtenerTexto("Apellidos: ");
				char genero = obtenerGenero();
				Date fechaNacimiento = obtenerFechaNacimiento();
				String ciclo = obtenerTexto("Ciclo: ");
				String curso = obtenerTexto("Curso: ");
				String grupo = obtenerTexto("Grupo: ");
				Alumno alumno = new Alumno(nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo);
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
		try (Connection conexion = conexionBD.obtenerConexion()) {
			String sql = "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE table_name = 'alumno' AND table_schema = DATABASE();";
			try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
				try (ResultSet resultado = sentencia.executeQuery()) {
					if (resultado.next()) {
						return resultado.getInt("AUTO_INCREMENT"); // Devolverá el próximo valor de autoincremento
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
	private Date obtenerFechaNacimiento() {
		while (true) {
			System.out.print("Ingrese la Fecha de Nacimiento (dd-MM-yyyy): ");
			String fechaIntroducida = sc.nextLine();
			try {
				// Creamos el formateador para el formato 'dd-MM-yyyy'
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				// Convertir la fecha introducida por el usuario en LocalDate
				LocalDate fecha = LocalDate.parse(fechaIntroducida, formatter);
				// Convertir LocalDate a java.sql.Date
				return Date.valueOf(fecha);
			} catch (DateTimeParseException e) {
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

		try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

			sentencia.setString(1, alumno.getNombre());
			sentencia.setString(2, alumno.getApellidos());
			sentencia.setString(3, String.valueOf(alumno.getGenero()));

			Date fecha = (Date) alumno.getFechaNacimiento();
			sentencia.setDate(4, new Date(fecha.getTime()));

			sentencia.setString(5, alumno.getCiclo());
			sentencia.setString(6, alumno.getCurso());
			sentencia.setString(7, alumno.getGrupo());

			int filasInsertadas = sentencia.executeUpdate();
			mostrarMensaje(filasInsertadas > 0 ? "Alumno correctamente insertado en la BD."
					: "No se pudo insertar el alumno en  la BD.");
		} catch (SQLException e) {
			mostrarMensaje("Error al insertar el alumno: " + e.getMessage());
		}
	}

	public void mostrarAlumnosEnBD(Connection conexion) {
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		try (PreparedStatement sentencia = conexion.prepareStatement(sql);
				ResultSet resultado = sentencia.executeQuery()) {

			System.out.println("Lista de Alumnos:");
			System.out.println("======================================");

			while (resultado.next()) {
				int nia = resultado.getInt("nia");
				String nombre = resultado.getString("nombre");
				String apellidos = resultado.getString("apellidos");
				char genero = resultado.getString("genero").charAt(0);
				Date fechaNacimiento = resultado.getDate("fechaNacimiento");
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

	public void guardarAlumnosEnFicheroBinario(Connection conexionBD) {
		// Definimos los campos explícitos:
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		try (PreparedStatement sentencia = conexionBD.prepareStatement(sql);
				ResultSet resultado = sentencia.executeQuery()) {

			// Pedimos el nombre del archivo al usuario o usamos uno predeterminado
			System.out.print("Introduzca el nombre del archivo (por defecto 'alumnos.dat'): ");
			String nombreArchivo = sc.nextLine().trim();
			if (nombreArchivo.isEmpty()) {
				nombreArchivo = "alumnos.dat"; // Usamos el nombre por defecto si el usuario no introduce uno
			}

			// Creamos el flujo de salida de objetos para el archivo binario
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nombreArchivo))) {
				// Procesamos todos los alumnos de la base de datos
				while (resultado.next()) {
					// Creamos un objeto Alumno con los datos del ResultSet
					Alumno alumno = new Alumno(resultado.getInt("nia"), resultado.getString("nombre"),
							resultado.getString("apellidos"), resultado.getString("genero").charAt(0),
							resultado.getDate("fechaNacimiento"), resultado.getString("ciclo"),
							resultado.getString("curso"), resultado.getString("grupo"));
					// Escribimos el objeto Alumno en el archivo binario
					oos.writeObject(alumno);
				}
				System.out.println("Alumnos guardados en el archivo binario: " + nombreArchivo);
			} catch (IOException e) {
				// Captura y muestra los errores al guardar el archivo
				System.err.println("Error al guardar los alumnos en el archivo binario: " + e.getMessage());
			}

		} catch (SQLException e) {
			// Captura y muestra los errores de la consulta SQL
			System.err.println("Error al obtener los alumnos de la base de datos: " + e.getMessage());
		}
	}

}
