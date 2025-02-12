package model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase modelo para representar a un usuario.
 * 
 * Esta clase almacena información relacionada con un usuario, como su nombre,
 * apellidos, correo electrónico, contraseña y nombre de usuario.
 */
public class Usuario {

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Apellidos del usuario.
     */
    private String apellidos;

    /**
     * Correo electrónico del usuario.
     */
    private String email;

    /**
     * Contraseña del usuario.
     */
    private String password;

    /**
     * Nombre de usuario (username).
     */
    private String username;
    
    private List<String> juegosDeseados;
    
    
    private List<String> juegosBiblioteca;

    /**
     * Constructor que inicializa los datos del usuario.
     * 
     * @param nombre    El nombre del usuario.
     * @param apellidos Los apellidos del usuario.
     * @param email     El correo electrónico del usuario.
     * @param password  La contraseña del usuario.
     * @param username  El nombre de usuario.
     */
    public Usuario(String nombre, String apellidos, String email, String password, String username) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    
    
    public Usuario( String nombre, String apellidos, String email, String password, String usuario, String deseados, String biblioteca) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.username = usuario;
        this.juegosDeseados = convertirALista(deseados);
        this.juegosBiblioteca = convertirALista(biblioteca);
    }

    /**
     * Convierte una cadena de IDs separados por comas en una lista de enteros.
     */
    private List<String> convertirALista(String ids) {
        if (ids == null || ids.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(ids.split(","))
                     .map(String::trim)
                     .filter(s -> s.matches("\\d+")) // Asegura que solo toma números
                     .collect(Collectors.toList());
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUsername() { return username; }
    public List<String> getJuegosDeseados() { return juegosDeseados; }
    public List<String> getJuegosBiblioteca() { return juegosBiblioteca; }

    public void setJuegosDeseados(String deseados) { this.juegosDeseados = convertirALista(deseados); }
    public void setJuegosBiblioteca(String biblioteca) { this.juegosBiblioteca = convertirALista(biblioteca); }
}
