package model;

/**
 * Model class for a Person.
 *
 */
public class Person {

	private String nombre;
	private String apellidos;
	private String email;
	private String password;
	private String username;
	//private List<Juegos> juegosGuardados


	/**
	 * Default constructor.
	 */
	public Person() {
		this(null, null, null, null, null);
	}
	
	/**
	 * Constructor with some initial data.
	 * 
	 * @param firstName
	 * @param lastName
	 */
	public Person(String nombre, String apellidos, String email, String password, String username) {
		this.nombre =  nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
		this.username = username;
	}
	
	public String getNomrbe() {
		return nombre;
	}

	public void setNombre(String firstName) {
		this.nombre = (firstName);
	}
	
	public String nombreProperty() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String lastName) {
		this.apellidos = lastName;
	}
	
	public String apellidosProperty() {
		return apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String lastName) {
		this.email = lastName;
	}
	
	public String emailProperty() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String lastName) {
		this.password = lastName;
	}
	
	public String passwordProperty() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String lastName) {
		this.username = lastName;
	}
	
	public String usernameProperty() {
		return username;
	}
	
}