package tarea_11;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;

/**
 * @author Alberto Polo
 */

public class Alumno implements Serializable {

	private static final long serialVersionUID = -6764637131483654272L;

	// Constante para indicar el número de alumnos
	private static final int NUMERO_DE_ALUMNOS = 5;

	// Scanner declarado como static para no tener que cerrarlo:
	private static Scanner sc = new Scanner(System.in);

	// Atributos privados de la clase Alumno
	private int nia = 0;
	private String nombre;
	private String apellidos;
	private char genero = 'S';
	private Date fechaNacimiento;
	private String ciclo;
	private String curso;
	private String grupo;

	// Constructores de la clase Alumno
	public Alumno() {
	}

	public Alumno(int nia, String nombre, String apellidos, char genero, Date fechaNacimiento, String ciclo,
			String curso, String grupo) {

		this.nia = nia;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.genero = genero;
		this.fechaNacimiento = fechaNacimiento;
		this.ciclo = ciclo;
		this.curso = curso;
		this.grupo = grupo;
	}

	public Alumno(String nombre, String apellidos, char genero, Date fechaNacimiento, String ciclo, String curso,
			String grupo) {
		super();
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.genero = genero;
		this.fechaNacimiento = fechaNacimiento;
		this.ciclo = ciclo;
		this.curso = curso;
		this.grupo = grupo;
	}

	// Getters & Setters:
	public int getNia() {
		return nia;
	}

	public void setNia(int nia) {
		this.nia = nia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public char getGenero() {
		return genero;
	}

	public void setGenero(char genero) {
		this.genero = genero;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getCiclo() {
		return ciclo;
	}

	public void setCiclo(String ciclo) {
		this.ciclo = ciclo;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	@Override
	public String toString() {
		return "NIA: " + nia + " --> " + nombre + ", " + apellidos + " - Género: " + genero + " - Fecha de Nacimiento: "
				+ fechaNacimiento + " - Ciclo: " + ciclo + " - Curso: " + curso + " Grupo: " + grupo;
	}

	/**
	 * Recibe una lista de objetos de la clase Alumno y, a partir de ella, genera un
	 * documento JSON utilizando la librería de Google GSON.
	 * 
	 * @author Alberto Polo
	 * @param listaAlumnos es una lista de objetos de la clase Alumno.
	 */

	/*
	 * public void generaJSONconGSONdesdeLista(List<Alumno> listaAlumnos) {
	 * 
	 * // Antes de convertir la lista a JSON, verificaremos que la lista no sea
	 * "null" // ni esté vacía:
	 * 
	 * if (listaAlumnos == null || listaAlumnos.isEmpty()) { System.out.
	 * println("La lista de alumnos está vacía o es nula. No se generará el archivo JSON."
	 * ); return; }
	 * 
	 * // Crear un objeto Gson con formato "pretty printing" (indentación para //
	 * legibilidad)
	 * 
	 * Gson gson = new GsonBuilder().setPrettyPrinting().create();
	 * 
	 * // Convertir la lista de alumnos a un JSON y guardarlo en un archivo String
	 * jsonAlumnos = gson.toJson(listaAlumnos);
	 * 
	 * // Guardar el JSON en un archivo try (FileWriter fileWriter = new
	 * FileWriter("alumnos.json")) { fileWriter.write(jsonAlumnos);
	 * System.out.println("Archivo JSON generado correctamente: alumnos.json"); }
	 * catch (IOException e) {
	 * System.out.println("Error al escribir el archivo JSON: " + e.getMessage()); }
	 * }
	 */

	/**
	 * @author Alberto Polo Método para leer un archivo JSON que contiene una lista
	 *         de alumnos y mostrarlos por pantalla.
	 * @param ficheroJSON es el fichero JSON que contiene los datos de los alumnos.
	 */

	public File solicitarFicheroJSON() {

		String rutaFichero;
		File directorio;

		// Bucle para pedir la ruta hasta que sea válida
		while (true) {
			System.out.println("Indique la ruta donde se encuentran el fichero JSON:");
			System.out.println("(Por ejemplo ---> D:\\AD\\tarea_10b\\src\\main\\java\\tarea_10b)");

			rutaFichero = sc.nextLine();
			directorio = new File(rutaFichero);

			// Verificar si la ruta existe y es un directorio
			if (directorio.exists() && directorio.isDirectory()) {
				// Listar archivos JSON en la ruta especificada
				File[] archivosJSON = directorio.listFiles((dir, name) -> name.endsWith(".json"));
				if (archivosJSON != null && archivosJSON.length > 0) {
					System.out.println("Archivos JSON disponibles en la ruta especificada:");
					for (File archivo : archivosJSON) {
						System.out.println("- " + archivo.getName());
					}
				} else {
					System.out.println("No se encontraron archivos JSON en la ruta especificada.");
				}
				break; // Salir del bucle si la ruta es válida
			} else {
				System.out.println(
						"La ruta especificada no existe o no es un directorio. Por favor, inténtalo de nuevo.");
			}
		}

		// Bucle para pedir el nombre del archivo hasta que exista en la ruta
		// especificada

		File ficheroJSON;

		while (true) {
			System.out.println("Indique el nombre del fichero JSON que desea leer:");
			String nombreFichero = sc.nextLine();

			// Validar si el nombre del archivo tiene la extensión ".json"
			if (!nombreFichero.endsWith(".json")) {
				nombreFichero += ".json";
			}

			// Crear el archivo con la ruta y el nombre proporcionado
			ficheroJSON = new File(rutaFichero, nombreFichero);

			// Verificar si el archivo existe en la ruta especificada
			if (ficheroJSON.exists()) {
				break; // Salir del bucle si el archivo existe
			} else {
				System.out.println("El archivo especificado no existe en la ruta. Por favor, inténtalo de nuevo.");
			}
		}

		return ficheroJSON;
	}

	/**
	 * Método para leer un archivo JSON que contiene una lista de alumnos y
	 * mostrarlos por pantalla.
	 * 
	 * @author Alberto Polo
	 * @param ficheroJSON el archivo JSON que contiene los datos de los alumnos.
	 */
	/*
	 * public void leerJSONconGSONyMostrarAlumnos(File ficheroJSON) {
	 * 
	 * Gson gson = new Gson();
	 * 
	 * // Definir el tipo de dato como List<Alumno> para que GSON sepa cómo //
	 * deserializar Type tipoListaAlumnos = new TypeToken<List<Alumno>>() {
	 * }.getType();
	 * 
	 * try (FileReader fileReader = new FileReader(ficheroJSON.getAbsolutePath())) {
	 * // Leer y convertir el archivo JSON a una lista de objetos Alumno
	 * List<Alumno> listaAlumnos = gson.fromJson(fileReader, tipoListaAlumnos);
	 * 
	 * // Mostrar los datos de cada alumno
	 * System.out.println("LISTA DE ALUMNOS EN EL FICHERO: " +
	 * ficheroJSON.getName()); for (Alumno alumno : listaAlumnos) {
	 * System.out.println("NIA: " + alumno.getNia()); System.out.println("Nombre: "
	 * + alumno.getNombre()); System.out.println("Apellidos: " +
	 * alumno.getApellidos()); System.out.println("Género: " + alumno.getGenero());
	 * System.out.println("Fecha de Nacimiento: " + alumno.getFechaNacimiento());
	 * System.out.println("Ciclo: " + alumno.getCiclo());
	 * System.out.println("Curso: " + alumno.getCurso());
	 * System.out.println("Grupo: " + alumno.getGrupo());
	 * System.out.println("-----------------------------------------------"); } }
	 * catch (IOException e) { System.out.println("Error al leer el archivo JSON: "
	 * + e.getMessage()); } }
	 */

	/**
	 * El método recoge los datos de la cantidad especificada en la constante
	 * NUMERO_DE_ALUMNOS y los va añadiendo a una lista de objetos de tipo Alumno.
	 * 
	 * @author Alberto Polo
	 * @return Devuelve una lista de Objetos de tipo Alumno.
	 */
	public List<Alumno> leeAlumnos() {
		List<Alumno> listaAlumnos = new ArrayList<>();

		for (int i = 0; i < NUMERO_DE_ALUMNOS; i++) {

			// NIA - int
			// Vamos añadiendo el nia secuencialmente incrementándolo en 1 unidad:
			nia = i + 1;

			// NOMBRE - String
			System.out.print("Introduzca el NOMBRE del alumno " + (i + 1) + ": ");
			nombre = sc.nextLine().toUpperCase();

			// APELLIDOS - String
			System.out.print("Introduzca los APELLIDOS del alumno " + (i + 1) + ": ");
			apellidos = sc.nextLine().toUpperCase();

			// GÉNERO - char

			do {
				System.out.print("Introduzca el GÉNERO del alumno(H/M): ");
				String entradaTeclado = sc.nextLine().toUpperCase();

				if (entradaTeclado.length() > 0) {
					genero = entradaTeclado.charAt(0);
				} else {
					genero = ' ';
				}

			} while (genero != 'H' && genero != 'M');

			// FECHA DE NACIMIENTO - Date
			System.out.print("Introduzca la FECHA DE NACIMIENTO del alumno en formato (dd/MM/yyyy): ");
			String fechaNacimientoString = sc.nextLine();
			fechaNacimiento = convierteStringEnDate(fechaNacimientoString);

			// CICLO - String
			System.out.print("Introduzca el CICLO del alumno " + (i + 1) + ": ");
			ciclo = sc.nextLine().toUpperCase();

			// CURSO - String
			System.out.print("Introduzca el CURSO del alumno " + (i + 1) + ": ");
			curso = sc.nextLine().toUpperCase();

			// GRUPO - String
			System.out.print("Introduzca el GRUPO del alumno " + (i + 1) + ": ");
			grupo = sc.nextLine().toUpperCase();
			System.out.println("----------------------------------------------------------------------");

			// Creo un nuevo objeto Alumno y lo añado a la lista:
			Alumno alumno = new Alumno(nia, nombre, apellidos, genero, fechaNacimiento, ciclo, curso, grupo);
			listaAlumnos.add(alumno);
		}

		return listaAlumnos;
	}

	/**
	 * @author Alberto Polo
	 * @param Recibe fechaDate como fecha en formato java.util.Date
	 * @return Devuelve la fecha introducida como parámetro transformada en String
	 */
	public String convierteDateEnString(Date fechaDate) {
		// Definimos el formato de fecha:
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

		// Convertimos el objeto Date a String:
		String fechaString = formatoFecha.format(fechaDate);

		return fechaString;
	}

	/**
	 * @author Alberto Polo
	 * @param Recibe fechaString como fecha en formato String
	 * @return Devuelve la fecha introducida como parámetro transformada en
	 *         java.util.Date
	 */
	public Date convierteStringEnDate(String fechaString) {
		// Definir formato de fecha según el String:
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

		Date fechaDate = new Date();
		try {
			// Convertir el String en Date;
			fechaDate = formato.parse(fechaString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fechaDate;
	}

	/**
	 * @author Alberto Polo
	 * @param listaAlumnos recibe una lista de Objetos de tipo Alumno que se
	 *                     utilizará para generar el fichero Alumnos.XML
	 * 
	 */

	public void generaXML(List<Alumno> listaAlumnos) {

		// Creamos la factoría para generar documentos XML:
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation implementation = builder.getDOMImplementation();

			// Creamos el documento XML vacio:
			Document document = implementation.createDocument(null, "Alumnos", null);
			document.setXmlVersion("1.0");

			for (Alumno alumno : listaAlumnos) {
				// Creamos el nodo alumno:
				Element nodoAlumno = document.createElement("alumno");
				// Agregamos el nodo a la raíz:
				document.getDocumentElement().appendChild(nodoAlumno);
				// Creamos elementos hijo del nodo alumno mediante función
				// CrearElementoAlumnoEtiquetas():
				crearElementoAlumnoEtiquetas("nia", Integer.toString(alumno.getNia()), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("nombre", alumno.getNombre(), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("apellidos", alumno.getApellidos(), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("genero", Character.toString(alumno.getGenero()), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("fecha_de_nacimiento", convierteDateEnString(alumno.getFechaNacimiento()),
						nodoAlumno, document);
				crearElementoAlumnoEtiquetas("ciclo", alumno.getCiclo(), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("curso", alumno.getCurso(), nodoAlumno, document);
				crearElementoAlumnoEtiquetas("grupo", alumno.getGrupo(), nodoAlumno, document);
			}

			// Generamos el fichero XML a partir del documento creado:
			Source source = new DOMSource(document);
			Result result = new StreamResult(new File("Alumnos.xml"));
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);

		} catch (ParserConfigurationException | TransformerException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Alberto Polo
	 * @param datoAlumno recibe el nombre que tendrá la etiqueta del nodo
	 * @param valor      recibe el contenido que tendrá el nodo
	 * @param alumno     recibe el nodo alumno
	 * @param documento  recibe el documento XML
	 */
	private static void crearElementoAlumnoEtiquetas(String datoAlumno, String valor, Element alumno,
			Document documento) {

		Element elem = documento.createElement(datoAlumno);
		Text text = documento.createTextNode(valor);
		elem.appendChild(text);
		alumno.appendChild(elem);
	}

}
