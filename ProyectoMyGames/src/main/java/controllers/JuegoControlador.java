package controllers;

import conexiones.ConexionBD;
import conexiones.ConexionAPI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Controlador para la pantalla de reseñas de juegos.
 */
public class JuegoControlador extends Navegacion{

    @FXML
    private TextArea texto;
    
    @FXML
    private Button modificar;
    
    @FXML
    private Button publicar;
    
    @FXML
    private ImageView imagenJuego;

    @FXML
    private Label nombreJuego;

    @FXML
    private Label ratingJuego;
    
    @FXML
    private Button inicio;
    @FXML
    private Button biblioteca;
    @FXML
    private Button deseados;
    @FXML
    private Button user;
    
    @FXML
    private Button mandarBiblioteca;
    @FXML
    private Button mandarDeseados;
    
    @FXML
    private TextArea descripcionJuego;

    @FXML
    private ImageView imagenJuegoSecundario;
    
    @FXML
    private ImageView imagenJuego1;
    
    
    private ConexionBD conexionBD;
    private String juegoId;
    private Usuario usuario;
    private ConexionAPI conexionAPI = new ConexionAPI();

    public void inicializar(String juegoId, Usuario usuario) {
        this.conexionBD = new ConexionBD();
        this.juegoId = juegoId;
        this.usuario = usuario;
        cargarReseña();
        cargarBotones();

        publicar.setOnAction(event -> guardarReseña());
        modificar.setOnAction(event -> modificarReseña());

        mandarBiblioteca.setOnAction(event -> toggleBiblioteca());
        mandarDeseados.setOnAction(event -> toggleDeseados());
        
        inicio.setOnAction(event -> abrirInicio(event, usuario));
        biblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
        deseados.setOnAction(event -> abrirDeseados(event, usuario));
        user.setOnAction(event -> abrirUser(event, usuario));
        
    }

    private void cargarBotones() {
        List<String> biblioteca = usuario.getJuegosBiblioteca();
        List<String> deseados = usuario.getJuegosDeseados();

        mandarBiblioteca.setStyle(biblioteca.contains(juegoId) ? "-fx-background-color: #3adb34" : "-fx-background-color: #c40404");
        mandarDeseados.setStyle(deseados.contains(juegoId) ? "-fx-background-color: #3adb34" : "-fx-background-color: #c40404");
    }

    private void verificarYAgregarJuego(String[] juego) {
        String[] juegoExistente = conexionBD.obtenerJuegoPorId(juego[0]);
        
        if (juegoExistente == null) { 
            // Si el juego no existe en la base de datos, lo agregamos
            boolean agregado = conexionBD.agregarJuego(juego[0], juego[1], juego[3], juego[2]);
            if (!agregado) {
                System.out.println("Error al agregar el juego a la base de datos.");
            }
        }
    }

    private void toggleBiblioteca() {
        verificarYAgregarJuego(conexionAPI.buscarJuegoPorId(juegoId)); // Verifica y agrega si no existe

        List<String> biblioteca = new ArrayList<>(usuario.getJuegosBiblioteca());

        if (biblioteca.contains(juegoId)) {
            biblioteca.remove(juegoId);
        } else {
            biblioteca.add(juegoId);
        }

        usuario.setJuegosBiblioteca(biblioteca);
        actualizarUsuarioEnBD();
        cargarBotones();
    }

    private void toggleDeseados() {
        verificarYAgregarJuego(conexionAPI.buscarJuegoPorId(juegoId)); // Verifica y agrega si no existe

        if (usuario.getJuegosDeseados().contains(juegoId)) {
            usuario.getJuegosDeseados().remove(juegoId);
        } else {
            usuario.getJuegosDeseados().add(juegoId);
        }
        actualizarUsuarioEnBD();
        cargarBotones();
    }


    private void actualizarUsuarioEnBD() {
        String query = "UPDATE Usuario SET Deseados = ?, Biblioteca = ? WHERE usuario = ?";
        String deseados = String.join(",", usuario.getJuegosDeseados());
        System.out.println(deseados);
        String biblioteca = String.join(",", usuario.getJuegosBiblioteca());

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, deseados);
            preparedStatement.setString(2, biblioteca);
            preparedStatement.setString(3, usuario.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarReseña() {
        String query = "SELECT Texto FROM Reseña WHERE JuegoID = ? AND UsuarioID = ?";
        try (Connection conexion = conexionBD.conectar();
             PreparedStatement preparedStatement = conexion.prepareStatement(query)) {
            preparedStatement.setString(1, juegoId);
            preparedStatement.setString(2, usuario.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                texto.setText(resultSet.getString("Texto"));
                texto.setEditable(false);
                publicar.setDisable(true);
                modificar.setDisable(false);
            } else {
                texto.setText("");
                texto.setEditable(true);
                publicar.setDisable(false);
                modificar.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void modificarReseña() {
        texto.setEditable(true);
        publicar.setDisable(false);
        modificar.setDisable(true);
    }

    private void guardarReseña() {
        String nuevaReseña = texto.getText().trim();
        if (nuevaReseña.isEmpty()) return;

        String queryExiste = "SELECT COUNT(*) FROM Reseña WHERE JuegoID = ? AND UsuarioID = ?";
        String queryInsert = "INSERT INTO Reseña (JuegoID, UsuarioID, Texto) VALUES (?, ?, ?)";
        String queryUpdate = "UPDATE Reseña SET Texto = ? WHERE JuegoID = ? AND UsuarioID = ?";

        try (Connection conexion = conexionBD.conectar();
             PreparedStatement checkStmt = conexion.prepareStatement(queryExiste)) {
            checkStmt.setString(1, juegoId);
            checkStmt.setString(2, usuario.getUsername());
            ResultSet resultSet = checkStmt.executeQuery();
            boolean existeReseña = resultSet.next() && resultSet.getInt(1) > 0;

            try (PreparedStatement stmt = conexion.prepareStatement(existeReseña ? queryUpdate : queryInsert)) {
                if (existeReseña) {
                    stmt.setString(1, nuevaReseña);
                    stmt.setString(2, juegoId);
                    stmt.setString(3, usuario.getUsername());
                } else {
                    stmt.setString(1, juegoId);
                    stmt.setString(2, usuario.getUsername());
                    stmt.setString(3, nuevaReseña);
                }
                stmt.executeUpdate();
                texto.setEditable(false);
                publicar.setDisable(true);
                modificar.setDisable(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Setea el juego en la pantalla
     * @param juego
     */
    public void setJuego(String[] juego) {
    	String[] datosJuegos = conexionAPI.buscarJuegoPorId(juego[0]);
    	for (int i = 0; i < datosJuegos.length; i++) {
			System.out.println(datosJuegos[i]);
		}
        // Mostrar la información básica del juego
        nombreJuego.setText(datosJuegos[1]);
        ratingJuego.setText("Rating: " + datosJuegos[3]);

        // Cargar la imagen del juego
        String urlImagen = datosJuegos[2];
        if (urlImagen != null && !urlImagen.isEmpty() && urlImagen.startsWith("http")) {
            try {
                Image imagen = new Image(urlImagen);
                imagenJuego.setImage(imagen);
            } catch (IllegalArgumentException e) {
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                imagenJuego.setImage(imagenPredeterminada);
            }
        }
        
        String urlImagen1 = datosJuegos[2];
        if (urlImagen1 != null && !urlImagen1.isEmpty() && urlImagen1.startsWith("http")) {
            try {
                Image imagen = new Image(urlImagen1);
                imagenJuego1.setImage(imagen);
            } catch (IllegalArgumentException e) {
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                imagenJuego1.setImage(imagenPredeterminada);
            }
        }
        
        // Mostrar la descripción
        Document document = Jsoup.parse(datosJuegos[8]);

        // Reemplazar <br> por saltos de línea
        for (Element br : document.select("br")) {
            br.after("\n");
        }

        // Reemplazar <p> por saltos de línea (para mayor claridad)
        for (Element p : document.select("p")) {
            p.prepend("\n");
        }

        // Convertir a texto plano
        String plainText = document.text();
        
        descripcionJuego.setText(plainText);
        
        System.out.println("Screenshots recibidos: " + datosJuegos[11]);

        // Cargar la imagen del juego
        String urlImagenSecundaria = datosJuegos[11];
        if (urlImagenSecundaria != null && !urlImagenSecundaria.isEmpty() && urlImagenSecundaria.startsWith("http")) {
            try {
                Image imagen = new Image(urlImagenSecundaria);
                imagenJuegoSecundario.setImage(imagen);
            } catch (IllegalArgumentException e) {
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                imagenJuegoSecundario.setImage(imagenPredeterminada);
            }
        }
    }
}

