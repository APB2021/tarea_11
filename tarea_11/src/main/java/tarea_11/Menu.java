package tarea_11;

public class Menu {

	public void mostrarMenu() {
		int opcion = -1;

		while (true) {
			System.out.println(
					"""
							==================================================
							 AD - Tarea 11
							==================================================
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
														""");
		}
	}
}