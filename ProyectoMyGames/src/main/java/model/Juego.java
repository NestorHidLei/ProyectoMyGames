package model;

public class Juego {
	
	private int id;
	
	private String nombre;
	
	private String descripcion;
	
	private String plataformas;
	
	private double calificacion;
	
	private String imagen;

	public Juego(int id, String nombre, String descripcion, String plataformas, double calificacion, String imagen) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.plataformas = plataformas;
		this.calificacion = calificacion;
		this.imagen = imagen;
	}

	public int getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getPlataformas() {
		return plataformas;
	}

	public double getCalificacion() {
		return calificacion;
	}

	public String getImagen() {
		return imagen;
	}
	
	
	

}
