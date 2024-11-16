package tarea_11;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
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
		// Definimos los campos explícitos para la consulta SQL
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		// Solicitamos si el usuario quiere elegir un nombre personalizado para el
		// archivo
		System.out.print("¿Quieres darle un nombre personalizado al archivo? (sí/no): ");
		String respuesta = sc.nextLine().trim().toLowerCase();

		// Si la respuesta es 'si', pedimos el nombre; si no, usamos el nombre por
		// defecto
		String nombreArchivo = "src\\main\\java/tarea_11/";

		if ("si".equalsIgnoreCase(respuesta)) {
			System.out.print("Introduce el nombre del archivo (sin extensión): ");
			nombreArchivo += sc.nextLine().trim() + ".dat"; // Se añade la extensión .dat
		} else {
			nombreArchivo += "alumnos.dat"; // Nombre por defecto
		}

		try (PreparedStatement sentencia = conexionBD.prepareStatement(sql);
				ResultSet resultado = sentencia.executeQuery()) {

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

	public void guardarAlumnosEnFicheroTexto(Connection conexionBD) {
		// Definimos los campos explícitos para la consulta SQL
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		// Preguntamos al usuario si desea personalizar el nombre del archivo
		System.out.print("¿Quieres darle un nombre personalizado al archivo? (sí/no): ");
		String respuesta = sc.nextLine().trim().toLowerCase();

		// Ruta base para guardar el archivo
		String nombreArchivo = "src\\main\\java/tarea_11/";

		if ("si".equalsIgnoreCase(respuesta)) {
			System.out.print("Introduce el nombre del archivo (sin extensión): ");
			nombreArchivo += sc.nextLine().trim() + ".txt"; // Añade la extensión .txt
		} else {
			nombreArchivo += "alumnos.txt"; // Nombre por defecto
		}

		try (PreparedStatement sentencia = conexionBD.prepareStatement(sql);
				ResultSet resultado = sentencia.executeQuery()) {

			// Creamos el flujo de salida para escribir en el archivo de texto
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
				// Iteramos por los resultados y escribimos los datos en el archivo
				while (resultado.next()) {
					String alumno = String.format(
							"NIA: %d, Nombre: %s, Apellidos: %s, Género: %c, Fecha de Nacimiento: %s, Ciclo: %s, Curso: %s, Grupo: %s",
							resultado.getInt("nia"), resultado.getString("nombre"), resultado.getString("apellidos"),
							resultado.getString("genero").charAt(0), resultado.getDate("fechaNacimiento"),
							resultado.getString("ciclo"), resultado.getString("curso"), resultado.getString("grupo"));
					writer.write(alumno);
					writer.newLine(); // Salto de línea para separar alumnos
				}
				System.out.println("Alumnos guardados en el archivo de texto: " + nombreArchivo);
			} catch (IOException e) {
				System.err.println("Error al guardar los alumnos en el archivo de texto: " + e.getMessage());
			}

		} catch (SQLException e) {
			System.err.println("Error al obtener los alumnos de la base de datos: " + e.getMessage());
		}
	}

	public void leerAlumnosDeFicheroBinarioYGuardarlosEnBD(Connection conexionBD) {
	    // Exclusión del campo nia: La instrucción SQL omite nia, ya que los valores para este campo los generará automáticamente la base de datos.
	    String sql = "INSERT INTO alumno (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    String archivoPorDefecto = "src\\main\\java/tarea_11/alumnos.dat";

	    try {
	        // Preguntar al usuario si desea usar un archivo diferente
	        System.out.print("El archivo predeterminado es 'alumnos.dat'. ¿Quieres especificar otro archivo para leer? (si/no): ");
	        String respuesta = sc.nextLine().trim().toLowerCase();
	        
	        // Únicamente se aceptan las respuestas si o no.
	        while (!respuesta.equalsIgnoreCase("si") && !respuesta.equalsIgnoreCase("no")) {
	            System.out.print("Por favor, responde 'si' o 'no': ");
	            respuesta = sc.nextLine().trim().toLowerCase();
	        }

	        String nombreArchivo = archivoPorDefecto;
	        if ("si".equals(respuesta)) {
	            System.out.print("Introduce el nombre del archivo binario personalizado: ");
	            nombreArchivo =  "src\\main\\java/tarea_11/" + sc.nextLine().trim();
	        }

	        // Leer archivo binario
	        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nombreArchivo));
	             PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {

	            while (true) {
	                try {
	                    // Leer objeto Alumno del archivo binario
	                    Alumno alumno = (Alumno) ois.readObject();

	                    // Configurar los parámetros para la inserción (sin nia)
	                    sentencia.setString(1, alumno.getNombre());
	                    sentencia.setString(2, alumno.getApellidos());
	                    sentencia.setString(3, String.valueOf(alumno.getGenero()));
	                    sentencia.setDate(4, new java.sql.Date(alumno.getFechaNacimiento().getTime())); // Usamos java.sql.Date para la base de datos
	                    sentencia.setString(5, alumno.getCiclo());
	                    sentencia.setString(6, alumno.getCurso());
	                    sentencia.setString(7, alumno.getGrupo());

	                    // Ejecutar la inserción
	                    sentencia.executeUpdate();
	                } catch (EOFException e) {
	                    // Fin del archivo binario
	                    break;
	                } catch (ClassNotFoundException e) {
	                    System.err.println("Error al leer el archivo binario: Clase no encontrada. " + e.getMessage());
	                    break;  // Para detener la lectura y evitar un bucle infinito en caso de error.
	                }
	            }
	            System.out.println("Alumnos insertados en la base de datos desde el archivo binario.");
	        } catch (IOException e) {
	            System.err.println("Error al leer el archivo binario: " + e.getMessage());
	        }
	    } catch (SQLException e) {
	        System.err.println("Error al insertar los alumnos en la base de datos: " + e.getMessage());
	    }
	}
}
