package tarea_11;

import java.util.Scanner;
import java.sql.Connection;

public class Menu {

	private static final Scanner sc = new Scanner(System.in);

	public void mostrarMenu() {

		// Objeto para gestionar la conexión
		ConexionBD conexionBD = new ConexionBD();

		// Gestor de Alumnos
		GestorAlumnosBD gestor = new GestorAlumnosBD();

		int opcion = -1;

		while (opcion != 10) {
			mostrarOpciones();

			try {
				System.out.print("Por favor, seleccione una opción (Entre 1 y 10): ");
				opcion = sc.nextInt();
				sc.nextLine(); // Limpiar buffer

				switch (opcion) {
				case 1 -> {
					System.out.println("Ha elegido: Insertar un nuevo alumno.");
					gestor.insertarAlumno(conexionBD); // Llamamos al método en GestorAlumnosBD
				}
				case 2 -> System.out.println("Mostrar todos los alumnos: (Funcionalidad no implementada aún).");
				case 3 -> System.out.println("Guardar alumnos en un fichero: (Funcionalidad no implementada aún).");
				case 4 -> System.out.println("Leer alumnos de un fichero: (Funcionalidad no implementada aún).");
				case 5 -> System.out.println("Modificar nombre de un alumno: (Funcionalidad no implementada aún).");
				case 6 -> System.out.println("Eliminar un alumno: (Funcionalidad no implementada aún).");
				case 7 -> System.out.println("Eliminar alumnos por apellido: (Funcionalidad no implementada aún).");
				case 8 -> System.out.println("Guardar alumnos en un XML o JSON: (Funcionalidad no implementada aún).");
				case 9 -> System.out.println("Leer XML o JSON de alumnos: (Funcionalidad no implementada aún).");
				case 10 -> System.out.println("Ha elegido SALIR. Hasta pronto.");
				default -> System.out.println("Opción incorrecta. Por favor, elija una opción entre 1 y 10.");
				}
			} catch (Exception e) {
				System.out.println("Error: Entrada no válida. Inténtelo de nuevo.");
				sc.nextLine(); // Limpiar el buffer
			}
		}
	}

	private void mostrarOpciones() {
		System.out.println(
				"""
						=============================================================================================================
						AD - Tarea 11 - MySQL
						=============================================================================================================
						 1. Insertar un nuevo alumno.
						 2. Mostrar todos los alumnos (en consola).
						 3. Guardar todos los alumnos en un fichero (tú eliges el formato del fichero, pero no puede ser XML ni JSON).
						 4. Leer alumnos de un fichero (con el formato anterior), y guardarlos en una BD.
						 5. Modificar el nombre de un alumno guardado en la base de datos a partir de su Primary Key (PK).
						 6. Eliminar un alumno a partir de su (PK).
						 7. Eliminar los alumnos que su apellido contengan la palabra dada por el usuario.
						 8. Guardar todos los alumnos en un fichero XML o JSON.
						 9. Leer un fichero XML o JSON de alumnos (con en formato anterior) y guardarlos en la BD.
						10. Salir.
						=============================================================================================================
						""");
	}
}