package model;

public class Juego {
	
	private int id;
	
	private String nombre;
	
	private double calificacion;
	
	private String imagen;

	public Juego(int id, String nombre, double calificacion, String imagen) {
		this.id = id;
		this.nombre = nombre;
		this.calificacion = calificacion;
		this.imagen = imagen;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}


	public double getCalificacion() {
		return calificacion;
	}

	public String getImagen() {
		return imagen;
	}
	
	
	

}
