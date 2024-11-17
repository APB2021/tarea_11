package tarea_11;

import java.sql.Connection;
import java.util.Scanner;

public class Menu {
	/**
	 * Utilizamos el Scanner como static para no tener que cerrarlo:
	 */
	private static final Scanner sc = new Scanner(System.in);

	/**
	 * Método dedicado a presentar el menú de la App.
	 */
	public void mostrarMenu() {

		// Objeto para gestionar la conexión
		ConexionBD conexionBD = new ConexionBD();

		// Gestor de Alumnos
		GestorAlumnosBD gestor = new GestorAlumnosBD();

		int opcion = -1;

		while (opcion != 14) {
			mostrarOpciones();

			try {
				System.out.print("Por favor, seleccione una opción (Entre 1 y 14): ");
				opcion = sc.nextInt();
				sc.nextLine(); // Limpiar buffer

				switch (opcion) {
				case 1 -> {
					System.out.println("Ha elegido: Insertar un nuevo alumno.");
					gestor.insertarAlumno(conexionBD);
				}

				case 2 -> {
					System.out.println("Ha elegido: Mostrar todos los alumnos en consola.");
					gestor.mostrarAlumnosEnBD(conexionBD.obtenerConexion());
				}

				case 3 -> {
					System.out.println("Ha elegido: Guardar alumnos en un fichero binario.");
					gestor.guardarAlumnosEnFicheroBinario(conexionBD.obtenerConexion());
				}

				case 4 -> {
					System.out.println("Ha elegido: Guardar alumnos en un fichero de texto.");
					gestor.guardarAlumnosEnFicheroTexto(conexionBD.obtenerConexion());
				}
				case 5 -> {
					System.out.println("Ha elegido: Leer alumnos de un fichero binario y guardarlos en una BD.");
					gestor.leerAlumnosDeFicheroBinarioYGuardarlosEnBD(conexionBD.obtenerConexion());
				}
				case 6 -> {
					System.out.println("Ha elegido: Leer alumnos de un fichero de texto y guardarlos en una BD.");
					gestor.leerAlumnosDeFicheroTextoYGuardarlosEnBD(conexionBD.obtenerConexion());
				}
				case 7 -> {
					System.out.println(
							"Ha elegido: Modificar el nombre de un alumno guardado en la base de datos a partir de su Primary Key (PK)");
					gestor.modificarNombreAlumnoPorNIA(conexionBD.obtenerConexion());
				}

				case 8 -> {
					System.out.println("Ha elegido: Eliminar un alumno a partir de su (PK)");
					gestor.eliminarAlumnoPorNIA(conexionBD.obtenerConexion());
				}
				case 9 -> {
					System.out.println(
							"Ha elegido: Eliminar los alumnos cuyos apellidos contengan la palabra indicada por el usuario.");
					gestor.eliminarAlumnosPorApellido(conexionBD.obtenerConexion());
				}
				case 10 -> {
					System.out.println("Ha elegido: Guardar todos los alumnos en un fichero XML.");
					gestor.guardarAlumnosEnFicheroXML(conexionBD.obtenerConexion());
				}

				case 11 -> System.out.println(
						"Guardar todos los alumnos en un fichero JSON con GSON: (Funcionalidad no implementada aún).");

				case 12 -> System.out.println(
						"Leer un fichero XML de alumnos y guardarlos en la BD: (Funcionalidad no implementada aún).");
				case 13 -> System.out.println(
						"Leer fichero JSON de alumnos y guardarlos en la BD: (Funcionalidad no implementada aún).");
				case 14 -> System.out.println("Ha elegido SALIR. Hasta pronto.");
				default -> System.out.println("Opción incorrecta. Por favor, elija una opción entre 1 y 14.");
				}
			} catch (Exception e) {
				System.out.println("Error: Entrada no válida. Inténtelo de nuevo.");
				sc.nextLine(); // Limpiar el buffer
			}
		}
	}

	/**
	 * Método para presentar el texto del menú de forma más limpia:
	 */
	private void mostrarOpciones() {
		System.out.println(
				"""
						=============================================================================================================
						AD - Tarea 11 - MySQL
						=============================================================================================================
						 1. Insertar un nuevo alumno.
						 2. Mostrar todos los alumnos (en consola).
						 3. Guardar todos los alumnos en un fichero binario.
						 4. Guardar todos los alumnos en un fichero de texto.
						 5. Leer alumnos de un fichero binario y guardarlos en una BD.
						 6. Leer alumnos de un fichero de texto y guardarlos en una BD.
						 7. Modificar el nombre de un alumno guardado en la base de datos a partir de su Primary Key (PK).
						 8. Eliminar un alumno a partir de su (PK).
						 9. Eliminar los alumnos cuyos apellidos contengan la palabra indicada por el usuario.
						 10. Guardar todos los alumnos en un fichero XML.
						 11. Guardar todos los alumnos en un fichero JSON.
						 12. Leer un fichero XML de alumnos y guardarlos en la BD.
						 13. Leer un fichero JSON de alumnos y guardarlos en la BD.
						 14. Salir.
						=============================================================================================================
						""");
	}

}