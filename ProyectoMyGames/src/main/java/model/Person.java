package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Person.
 *
 * @author Marco Jakob
 */
public class Person {

	private final StringProperty nombre;
	private final StringProperty apellidos;
	private final StringProperty email;
	private final StringProperty password;
	private final StringProperty username;


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
		this.nombre = new SimpleStringProperty(nombre);
		this.apellidos = new SimpleStringProperty(apellidos);
		this.email = new SimpleStringProperty(email);
		this.password = new SimpleStringProperty(password);
		this.username = new SimpleStringProperty(username);
	}
	
	public String getNomrbe() {
		return nombre.get();
	}

	public void setNombre(String firstName) {
		this.nombre.set(firstName);
	}
	
	public StringProperty nombreProperty() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos.get();
	}

	public void setApellidos(String lastName) {
		this.apellidos.set(lastName);
	}
	
	public StringProperty apellidosProperty() {
		return apellidos;
	}

	public String getEmail() {
		return email.get();
	}

	public void setEmail(String lastName) {
		this.email.set(lastName);
	}
	
	public StringProperty emailProperty() {
		return email;
	}
	
	public String getPassword() {
		return password.get();
	}

	public void setPassword(String lastName) {
		this.password.set(lastName);
	}
	
	public StringProperty passwordProperty() {
		return password;
	}
	
	public String getUsername() {
		return username.get();
	}

	public void setUsername(String lastName) {
		this.username.set(lastName);
	}
	
	public StringProperty usernameProperty() {
		return username;
	}
	
}