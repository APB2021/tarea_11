package tarea_11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

					String alumno = String.format("%d,%s,%s,%c,%s,%s,%s,%s", resultado.getInt("nia"),
							resultado.getString("nombre"), resultado.getString("apellidos"),
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
		// Exclusión del campo nia: La instrucción SQL omite nia, ya que los valores
		// para este campo los generará automáticamente la base de datos.
		String sql = "INSERT INTO alumno (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String archivoPorDefecto = "src\\main\\java/tarea_11/alumnos.dat";

		try {
			// Preguntar al usuario si desea usar un archivo diferente
			System.out.print(
					"El archivo predeterminado es 'alumnos.dat'. ¿Quieres especificar otro archivo para leer? (si/no): ");
			String respuesta = sc.nextLine().trim().toLowerCase();

			// Únicamente se aceptan las respuestas si o no.
			while (!respuesta.equalsIgnoreCase("si") && !respuesta.equalsIgnoreCase("no")) {
				System.out.print("Por favor, responde 'si' o 'no': ");
				respuesta = sc.nextLine().trim().toLowerCase();
			}

			String nombreArchivo = archivoPorDefecto;
			if ("si".equals(respuesta)) {
				System.out.print("Introduce el nombre del archivo binario personalizado: ");
				nombreArchivo = "src\\main\\java/tarea_11/" + sc.nextLine().trim();
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
						sentencia.setDate(4, new java.sql.Date(alumno.getFechaNacimiento().getTime())); // Usamos
																										// java.sql.Date
																										// para la base
																										// de datos
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
						break; // Para detener la lectura y evitar un bucle infinito en caso de error.
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

	public void leerAlumnosDeFicheroTextoYGuardarlosEnBD(Connection conexionBD) {
		// Exclusión del campo nia: La instrucción SQL omite nia, ya que los valores
		// para este campo los generará automáticamente la base de datos.
		String sql = "INSERT INTO alumno (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
		String archivoDeTextoPorDefecto = "src\\main\\java/tarea_11/alumnos.txt";

		try {
			// Preguntar al usuario si desea usar un archivo diferente
			System.out.print(
					"El archivo predeterminado es 'alumnos.txt'. ¿Quieres especificar otro archivo para leer? (si/no): ");
			String respuesta = sc.nextLine().trim().toLowerCase();

			// Únicamente se aceptan las respuestas "si" o "no".
			while (!respuesta.equals("si") && !respuesta.equals("no")) {
				System.out.print("Por favor, responde 'si' o 'no': ");
				respuesta = sc.nextLine().trim().toLowerCase();
			}

			String nombreArchivo = archivoDeTextoPorDefecto;
			if ("si".equals(respuesta)) {
				System.out.print("Introduce el nombre del archivo de texto personalizado: ");
				nombreArchivo = "src\\main\\java/tarea_11/" + sc.nextLine().trim();
			}

			// Leer archivo de texto
			try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
					PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {

				String linea;
				while ((linea = br.readLine()) != null) {
					// Suponiendo que el formato del archivo de texto es:
					// índice,nombre,apellidos,genero,fechaNacimiento,ciclo,curso,grupo
					String[] datos = linea.split(",");

					if (datos.length == 8) { // 8 campos incluyendo el índice inicial
						// Ignorar el índice inicial y procesar los siguientes 7 campos
						for (int i = 1; i < datos.length; i++) {
							datos[i] = datos[i].trim();
						}

						// Manejar formato de fecha
						String fechaNacimientoStr = datos[4]; // Cambia al índice 4 por la fecha
						Date fechaNacimiento = null;
						try {
							SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
							java.util.Date parsedDate = formatoFecha.parse(fechaNacimientoStr);
							fechaNacimiento = new java.sql.Date(parsedDate.getTime());
						} catch (ParseException e) {
							System.err.println("Formato de fecha inválido en la línea: " + linea);
							continue; // Saltar esta línea si la fecha es inválida
						}

						// Configurar parámetros e insertar
						sentencia.setString(1, datos[1]); // nombre
						sentencia.setString(2, datos[2]); // apellidos
						sentencia.setString(3, datos[3]); // genero
						sentencia.setDate(4, fechaNacimiento); // fechaNacimiento
						sentencia.setString(5, datos[5]); // ciclo
						sentencia.setString(6, datos[6]); // curso
						sentencia.setString(7, datos[7]); // grupo

						sentencia.executeUpdate();
						System.out.println("Alumno insertado: " + Arrays.toString(datos));
					} else {
						System.err.println("Línea no válida: " + linea);
					}
				}
				System.out.println("Alumnos insertados en la base de datos desde el archivo de texto.");
			} catch (IOException e) {
				System.err.println("Error al leer el archivo de texto: " + e.getMessage());
			}
		} catch (SQLException e) {
			System.err.println("Error al insertar los alumnos en la base de datos: " + e.getMessage());
		}
	}

	public void modificarNombreAlumnoPorNIA(Connection conexionBD) {
		String sqlVerificar = "SELECT COUNT(*) FROM alumno WHERE nia = ?";
		String sqlActualizar = "UPDATE alumno SET nombre = ? WHERE nia = ?";

		try {
			int nia = -1;
			boolean niaValido = false;

			// Bucle para solicitar un NIA válido
			while (!niaValido) {
				System.out.print("Introduce el NIA del alumno cuyo nombre deseas modificar: ");
				try {
					nia = Integer.parseInt(sc.nextLine().trim()); // Asumimos que el NIA es un número entero

					// Verificar si el NIA existe en la base de datos
					try (PreparedStatement verificarSentencia = conexionBD.prepareStatement(sqlVerificar)) {
						verificarSentencia.setInt(1, nia);
						try (ResultSet resultado = verificarSentencia.executeQuery()) {
							if (resultado.next() && resultado.getInt(1) > 0) {
								niaValido = true; // El NIA es válido
							} else {
								System.out.println(
										"No se encontró ningún alumno con el NIA proporcionado. Inténtalo de nuevo.");
							}
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("El NIA debe ser un número válido. Inténtalo de nuevo.");
				}
			}

			// Solicitar el nuevo nombre si el NIA es válido
			System.out.print("Introduce el nuevo nombre del alumno: ");
			String nuevoNombre = sc.nextLine().trim().toUpperCase();

			// Actualizar el nombre en la base de datos
			try (PreparedStatement actualizarSentencia = conexionBD.prepareStatement(sqlActualizar)) {
				actualizarSentencia.setString(1, nuevoNombre);
				actualizarSentencia.setInt(2, nia);

				int filasAfectadas = actualizarSentencia.executeUpdate();
				if (filasAfectadas > 0) {
					System.out.println("El nombre del alumno con NIA " + nia + " ha sido actualizado correctamente.");
				} else {
					System.out.println("No se pudo actualizar el nombre del alumno.");
				}
			}

		} catch (SQLException e) {
			System.err.println("Error al interactuar con la base de datos: " + e.getMessage());
		}
	}

	public void eliminarAlumnoPorNIA(Connection conexionBD) {
		String sqlEliminar = "DELETE FROM alumno WHERE nia = ?";
		String sqlExistencia = "SELECT nia FROM alumno WHERE nia = ?";

		int nia = -1;
		boolean alumnoExiste = false;

		// Bucle para obtener un NIA válido
		while (!alumnoExiste) {
			try {
				// Solicitar al usuario el NIA
				System.out.print("Introduce el NIA del alumno que deseas eliminar: ");
				nia = Integer.parseInt(sc.nextLine().trim()); // Verificamos que sea un número válido.

				// Verificar si el alumno con este NIA existe en la base de datos
				try (PreparedStatement consultaExistencia = conexionBD.prepareStatement(sqlExistencia)) {
					consultaExistencia.setInt(1, nia);
					try (ResultSet rs = consultaExistencia.executeQuery()) {
						if (rs.next()) { // Si encontramos un NIA que coincide
							alumnoExiste = true; // El alumno existe, podemos continuar
						} else {
							// NIA no encontrado en la base de datos
							System.out.println("No se encontró ningún alumno con ese NIA. Inténtalo de nuevo.");
						}
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("El NIA debe ser un número válido. Inténtalo de nuevo.");
			} catch (SQLException e) {
				System.out.println("Error al comprobar la existencia del NIA en la base de datos: " + e.getMessage());
			}
		}

		// Si el NIA existe, proceder a confirmar la eliminación
		try {
			System.out.print("¿Estás seguro de que deseas eliminar al alumno con NIA " + nia + "? (si/no): ");
			String confirmacion = sc.nextLine().trim().toLowerCase();
			if ("si".equals(confirmacion)) {
				// Eliminar el alumno de la base de datos
				try (PreparedStatement sentenciaEliminar = conexionBD.prepareStatement(sqlEliminar)) {
					sentenciaEliminar.setInt(1, nia);
					int filasAfectadas = sentenciaEliminar.executeUpdate();
					if (filasAfectadas > 0) {
						System.out.println("El alumno con NIA " + nia + " ha sido eliminado correctamente.");
					} else {
						System.out.println("No se pudo eliminar el alumno. Por favor, verifica los datos.");
					}
				}
			} else {
				System.out.println("Operación cancelada.");
			}
		} catch (SQLException e) {
			System.err.println("Error al procesar la solicitud de eliminación: " + e.getMessage());
		}
	}

	public void eliminarAlumnosPorApellido(Connection conexionBD) {
		String sql = "DELETE FROM alumno WHERE apellidos LIKE ?";

		try {
			// Solicitar la palabra clave para buscar en los apellidos
			System.out.print("Introduce la palabra que quieres buscar en los apellidos de los alumnos: ");
			String palabraBuscada = sc.nextLine().trim().toUpperCase();

			// Verificar que la palabra no esté vacía
			if (palabraBuscada.isEmpty()) {
				System.out.println("La palabra clave no puede estar vacía.");
				return;
			}

			// Preparar la consulta SQL
			try (PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
				sentencia.setString(1, "%" + palabraBuscada + "%"); // "%" es un comodín que permite buscar la palabra
																	// en cualquier parte del apellido

				// Ejecutar la eliminación
				int filasAfectadas = sentencia.executeUpdate();
				if (filasAfectadas > 0) {
					System.out.println(filasAfectadas + " alumno(s) eliminado(s) de la base de datos.");
				} else {
					System.out.println("No se encontraron alumnos con esa palabra en los apellidos.");
				}
			}
		} catch (SQLException e) {
			System.err.println("Error al eliminar los alumnos en la base de datos: " + e.getMessage());
		}
	}

	public void guardarAlumnosEnFicheroXML(Connection conexionBD) {
		// Utilizar los nombres correctos de campos en lugar de SELECT *
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		// Crear el documento XML y la estructura de salida
		Document documento = null;
		try {
			documento = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			System.err.println("Error al crear el documento XML: " + e.getMessage());
			return; // Si ocurre un error al crear el documento, terminamos la ejecución
		}
		Element raiz = documento.createElement("alumnos");
		documento.appendChild(raiz);

		try (Statement sentencia = conexionBD.createStatement(); ResultSet resultado = sentencia.executeQuery(sql)) {

			// Recorrer el ResultSet y crear los elementos XML correspondientes
			while (resultado.next()) {
				Element alumnoElemento = documento.createElement("alumno");

				// Lista de los nombres de las columnas en la tabla alumno
				String[] campos = { "nia", "nombre", "apellidos", "genero", "fechaNacimiento", "ciclo", "curso",
						"grupo" };

				// Recorrer los campos y añadir cada uno al XML
				for (String campo : campos) {
					// Crear el elemento y asignar el valor del campo
					Element campoElemento = documento.createElement(campo);
					campoElemento.appendChild(documento.createTextNode(resultado.getString(campo)));
					alumnoElemento.appendChild(campoElemento);
				}

				// Añadir el alumno al nodo raíz del XML
				raiz.appendChild(alumnoElemento);
			}

			// Preguntar al usuario si quiere usar el nombre de archivo por defecto o
			// personalizado
			System.out.print("¿Quieres usar el nombre de archivo por defecto (alumnos.xml)? (S/N): ");
			String respuesta = sc.nextLine().trim().toUpperCase();

			String nombreArchivo = "alumnos.xml"; // Nombre por defecto

			if (respuesta.equals("N")) {
				// Si el usuario elige nombre personalizado, solicitamos el nombre
				System.out.print("Introduce el nombre del archivo (sin la extensión .xml): ");
				nombreArchivo = sc.nextLine().trim();

				// Verificar si el nombre contiene la extensión .xml, si no, añadirla
				if (!nombreArchivo.endsWith(".xml")) {
					nombreArchivo += ".xml";
				}
			}

			// Comprobar si el archivo ya existe y preguntar al usuario si desea
			// sobrescribirlo
			File archivo = new File("src\\main\\java\\tarea_11\\" + nombreArchivo);
			if (archivo.exists()) {
				System.out.print("El archivo " + nombreArchivo + " ya existe. ¿Quieres sobrescribirlo? (S/N): ");
				String confirmar = sc.nextLine().trim().toUpperCase();
				if (confirmar.equals("N")) {
					System.out.println("Operación cancelada. El archivo no fue sobrescrito.");
					return; // Salir del método si no queremos sobrescribir el archivo
				}
			}

			// Guardar el documento XML en un archivo
			try {
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(new DOMSource(documento), new StreamResult(archivo));
			} catch (TransformerException e) {
				System.err.println("Error al guardar el archivo XML: " + e.getMessage());
			}

			System.out.println("Los alumnos han sido guardados en el archivo XML: " + nombreArchivo);

		} catch (SQLException e) {
			System.err.println("Error al guardar los alumnos en el archivo XML: " + e.getMessage());
		}
	}

	public void guardarAlumnosEnFicheroJSON(Connection conexionBD) {
		// Utilizar los nombres correctos de campos en lugar de SELECT *
		String sql = "SELECT nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo FROM alumno";

		// Lista de alumnos
		List<Alumno> listaAlumnos = new ArrayList<>();

		try (Statement sentencia = conexionBD.createStatement(); ResultSet resultado = sentencia.executeQuery(sql)) {

			// Recorrer el ResultSet y crear los objetos Alumno
			while (resultado.next()) {
				// Crear objeto Alumno sin pasar el NIA y utilizando otro de los constructores
				// de la clase Alumno:
				Alumno alumno = new Alumno(resultado.getString("nombre"), resultado.getString("apellidos"),
						resultado.getString("genero").charAt(0), resultado.getDate("fechaNacimiento"),
						resultado.getString("ciclo"), resultado.getString("curso"), resultado.getString("grupo"));

				// Asignar el NIA usando el setter
				alumno.setNia(resultado.getInt("nia"));

				listaAlumnos.add(alumno);
			}

			// Preguntar si quiere usar el nombre por defecto o uno personalizado
			System.out.print("¿Deseas guardar el archivo con el nombre por defecto 'alumnos.json'? (S/N): ");
			String respuesta = sc.nextLine().trim().toUpperCase();

			String nombreArchivo;
			if (respuesta.equals("S")) {
				nombreArchivo = "alumnos.json"; // Nombre por defecto
			} else {
				// Solicitar al usuario el nombre del archivo personalizado
				System.out.print("Introduce el nombre del archivo (sin la extensión .json): ");
				nombreArchivo = sc.nextLine().trim();

				// Verificar si el nombre contiene la extensión .json, si no, añadirla
				if (!nombreArchivo.endsWith(".json")) {
					nombreArchivo += ".json";
				}
			}

			// Verificar si el archivo ya existe
			File archivo = new File("src\\main\\java\\tarea_11\\" + nombreArchivo);
			if (archivo.exists()) {
				// Preguntar al usuario si desea sobrescribir el archivo
				System.out.print("El archivo ya existe. ¿Deseas sobrescribirlo? (S/N): ");
				respuesta = sc.nextLine().trim().toUpperCase();

				if (respuesta.equals("N")) {
					System.out.println("Operación cancelada. El archivo no se ha guardado.");
					return; // Terminar la operación sin guardar el archivo
				}
			}

			// Crear un formato de fecha personalizado para serializar las fechas
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

			// Convertir la lista de alumnos a JSON con GSON utilizando un DateFormat
			Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat(dateFormat.toPattern()) // Establecer el
																									// formato de la
																									// fecha
					.create();

			String json = gson.toJson(listaAlumnos);

			// Guardar el JSON en un archivo
			try (FileWriter writer = new FileWriter(archivo)) {
				writer.write(json);
				System.out.println("Los alumnos han sido guardados en el archivo JSON: " + nombreArchivo);
			} catch (IOException e) {
				System.err.println("Error al guardar el archivo JSON: " + e.getMessage());
			}

		} catch (SQLException e) {
			System.err.println("Error al obtener los alumnos de la base de datos: " + e.getMessage());
		}
	}

	public void leerAlumnosDeFicheroXMLYGuardarlosEnBD(Connection conexionBD) {

		// Directorio de trabajo por defecto
		String directorio = "src\\main\\java\\tarea_11\\";

		// Listar los archivos XML disponibles en el directorio
		File carpeta = new File(directorio);
		File[] ficherosXMLenCarpeta = carpeta.listFiles((dir, name) -> name.endsWith(".xml"));

		// Si no hay archivos XML, mostrar un mensaje
		if (ficherosXMLenCarpeta == null || ficherosXMLenCarpeta.length == 0) {
			System.out.println("No se encontraron archivos XML en el directorio.");
			return;
		}

		// Mostrar los archivos XML disponibles
		System.out.println("Archivos XML disponibles:");
		for (int i = 0; i < ficherosXMLenCarpeta.length; i++) {
			System.out.println((i + 1) + ". " + ficherosXMLenCarpeta[i].getName());
		}

		// Preguntar al usuario si desea usar un archivo por defecto o uno de los
		// disponibles
		System.out.print("¿Deseas usar el archivo XML por defecto (alumnos.xml)? (S/N): ");
		String respuesta = sc.nextLine().trim().toUpperCase();

		String nombreArchivo = null; // Inicializar a null

		if (respuesta.equals("S")) {
			// Si elige usar el archivo por defecto, establecemos el nombre por defecto
			nombreArchivo = directorio + "alumnos.xml";
		} else {
			// Si elige uno de los archivos disponibles
			System.out.print("Introduce el nombre del archivo XML que deseas utilizar (sin la extensión .xml): ");
			String nombreFichero = sc.nextLine().trim();

			// Buscar el archivo con el nombre ingresado
			boolean encontrado = false;
			for (File fichero : ficherosXMLenCarpeta) {
				if (fichero.getName().equalsIgnoreCase(nombreFichero + ".xml")) {
					nombreArchivo = fichero.getAbsolutePath();
					encontrado = true;
					break;
				}
			}

			if (!encontrado) {
				System.out.println("El archivo no se encontró en la lista.");
				return;
			}
		}

		// Verificar si nombreArchivo es null antes de continuar
		if (nombreArchivo == null) {
			System.out.println("No se ha seleccionado un archivo XML válido.");
			return;
		}

		// Ahora procesamos el archivo XML seleccionado
		try {
			// Crear el parser de XML
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Leer el fichero XML
			File xmlFile = new File(nombreArchivo);
			Document document = builder.parse(xmlFile);

			// Normalizar el XML
			document.getDocumentElement().normalize();

			// Obtener todos los elementos "alumno"
			NodeList nodeList = document.getElementsByTagName("alumno");

			// Recorrer la lista de alumnos
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elementoAlumno = (Element) node;

					// Obtener los valores de cada elemento de alumno
					// No necesitaremos el nia porque en la base de datos es auto_increment
					// int nia = Integer.parseInt(getTagValue("nia", elementoAlumno));

					String nombre = getTagValue("nombre", elementoAlumno);
					String apellidos = getTagValue("apellidos", elementoAlumno);
					char genero = getTagValue("genero", elementoAlumno).charAt(0);
					Date fechaNacimiento = Date.valueOf(getTagValue("fechaNacimiento", elementoAlumno));
					String ciclo = getTagValue("ciclo", elementoAlumno);
					String curso = getTagValue("curso", elementoAlumno);
					String grupo = getTagValue("grupo", elementoAlumno);

					// Preparar la sentencia SQL para insertar el alumno en la BD
					// sin insertar la columna "nia" porque es auto_increment en la BD MySQL:
					String sql = "INSERT INTO alumno (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
					try (PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
						sentencia.setString(1, nombre);
						sentencia.setString(2, apellidos);
						sentencia.setString(3, String.valueOf(genero));
						sentencia.setDate(4, fechaNacimiento);
						sentencia.setString(5, ciclo);
						sentencia.setString(6, curso);
						sentencia.setString(7, grupo);
						sentencia.executeUpdate();
					} catch (SQLException e) {
						System.err.println("Error al insertar el alumno en la base de datos: " + e.getMessage());
					}
				}
			}

			System.out.println("Todos los alumnos han sido guardados en la base de datos.");

		} catch (Exception e) {
			System.err.println("Error al procesar el archivo XML: " + e.getMessage());
		}
	}

	// Método auxiliar para obtener el valor de una etiqueta en un Elemento
	private String getTagValue(String tag, Element elemento) {
		NodeList nodeList = elemento.getElementsByTagName(tag);
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			return node.getTextContent();
		}
		return "";
	}

	public void leerAlumnosDeFicheroJSONYGuardarlosEnBD(Connection conexionBD) {
		// Directorio de trabajo por defecto
		String directorio = "src\\main\\java\\tarea_11\\";

		// Listar los archivos JSON disponibles en el directorio
		File carpeta = new File(directorio);
		File[] ficherosJSONenCarpeta = carpeta.listFiles((dir, name) -> name.endsWith(".json"));

		// Si no hay archivos JSON, mostrar un mensaje
		if (ficherosJSONenCarpeta == null || ficherosJSONenCarpeta.length == 0) {
			System.out.println("No se encontraron archivos JSON en el directorio.");
			return;
		}

		// Mostrar los archivos JSON disponibles
		System.out.println("Archivos JSON disponibles:");
		for (int i = 0; i < ficherosJSONenCarpeta.length; i++) {
			System.out.println((i + 1) + ". " + ficherosJSONenCarpeta[i].getName());
		}

		// Preguntar al usuario si desea usar un archivo por defecto o uno de los
		// disponibles
		System.out.print("¿Deseas usar el archivo JSON por defecto (alumnos.json)? (S/N): ");
		String respuesta = sc.nextLine().trim().toUpperCase();

		// Inicializar nombreArchivo con un valor por defecto para evitar el error de no
		// inicialización
		String nombreArchivo = "";

		if (respuesta.equals("S")) {
			// Si elige usar el archivo por defecto, establecemos el nombre por defecto
			nombreArchivo = directorio + "alumnos.json";
		} else {
			// Si elige uno de los archivos disponibles
			System.out.print("Introduce el nombre del archivo JSON que deseas utilizar (sin la extensión .json): ");
			String nombreFichero = sc.nextLine().trim();

			// Buscar el archivo con el nombre ingresado
			boolean encontrado = false;
			for (File fichero : ficherosJSONenCarpeta) {
				if (fichero.getName().equalsIgnoreCase(nombreFichero + ".json")) {
					nombreArchivo = fichero.getAbsolutePath();
					encontrado = true;
					break;
				}
			}

			if (!encontrado) {
				System.out.println("El archivo no se encontró en la lista.");
				return;
			}
		}

		// Ahora procesamos el archivo JSON seleccionado
		try {
			// Crear el objeto GSON
			// Gson gson = new Gson();

			// Leer el fichero JSON
			FileReader reader = new FileReader(nombreArchivo);
			JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

			// Recorrer la lista de alumnos en el JSON
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonAlumno = jsonArray.get(i).getAsJsonObject();

				// Obtener los valores de cada alumno
				// int nia = jsonAlumno.get("nia").getAsInt();
				String nombre = jsonAlumno.get("nombre").getAsString();
				String apellidos = jsonAlumno.get("apellidos").getAsString();
				char genero = jsonAlumno.get("genero").getAsString().charAt(0);

				// Manejo de la fecha de nacimiento
				String fechaNacimientoStr = jsonAlumno.get("fechaNacimiento").getAsString();
				Date fechaNacimiento = null;
				try {
					SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
					fechaNacimiento = new Date(formatoFecha.parse(fechaNacimientoStr).getTime());
				} catch (ParseException e) {
					System.out.println("Error al parsear la fecha: " + fechaNacimientoStr);
					continue; // Saltar este alumno si la fecha es inválida
				}

				String ciclo = jsonAlumno.get("ciclo").getAsString();
				String curso = jsonAlumno.get("curso").getAsString();
				String grupo = jsonAlumno.get("grupo").getAsString();

				// Preparar la sentencia SQL para insertar el alumno en la BD
				String sql = "INSERT INTO alumno (nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo) VALUES (?, ?, ?, ?, ?, ?, ?)";
				try (PreparedStatement sentencia = conexionBD.prepareStatement(sql)) {
					sentencia.setString(1, nombre);
					sentencia.setString(2, apellidos);
					sentencia.setString(3, String.valueOf(genero));
					sentencia.setDate(4, fechaNacimiento);
					sentencia.setString(5, ciclo);
					sentencia.setString(6, curso);
					sentencia.setString(7, grupo);
					sentencia.executeUpdate();
				} catch (SQLException e) {
					System.err.println("Error al insertar el alumno en la base de datos: " + e.getMessage());
				}
			}

			System.out.println("Todos los alumnos han sido guardados en la base de datos.");
		} catch (IOException e) {
			System.err.println("Error al leer el archivo JSON: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error al procesar el archivo JSON: " + e.getMessage());
		}
	}
}
