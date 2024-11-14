package tarea_11;

import java.util.Scanner;

public class Menu {

	public static Scanner sc = new Scanner(System.in);

	public void mostrarMenu() {
		int opcion = -1;

		while (opcion != 10) {
			System.out.println(
					"""
							=============================================================================================================
							AD - Tarea 11 - MySql
							=============================================================================================================
							 1. Insertar un nuevo alumno.
							 2. Mostar todos los alumnos (en consola).
							 3. Guardar todos los alumnos en un fichero (tú eliges el formato del fichero, pero no puede ser XML ni JSON).
							 4. Leer alumnos de un fichero (con el formato anterior), y guardarlo en una BD.
							 5. Modificar el nombre de un alumno guardado en la base de datos a partir de su Primary Key (PK).
							 6. Eliminar un alumno a partir de su (PK).
							 7. Eliminar los alumnos que su apellido contengan la palabra dada por el usuario.
							 8. Guardar todos los alumnos en un fichero XML o JSON.
							 9. Leer un fichero XML o JSON de alumnos (con en formato anterior) y guardarlos en la BD.
							 10. Salir.
							=============================================================================================================
														""");
			System.out.print("Por favor, seleccione una opción (Entre 0 y 10): ");
			opcion = sc.nextInt();

			switch (opcion) {
			case 1 -> System.out.println("Ha elegido: Insertar un nuevo alumno.");
			case 2 -> System.out.println();
			case 3 -> System.out.println();
			case 4 -> System.out.println();
			case 5 -> System.out.println();
			case 6 -> System.out.println();
			case 7 -> System.out.println();
			case 8 -> System.out.println();
			case 9 -> System.out.println();
			case 10 -> System.out.println("Ha elegido SALIR.");

			default -> throw new IllegalArgumentException("Opción incorrecta: " + opcion);
			}
		}
	}
}