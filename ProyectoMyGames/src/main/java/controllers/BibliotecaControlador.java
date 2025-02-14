package controllers;

import conexiones.ConexionAPI;
import conexiones.ConexionBD;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import model.Usuario;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BibliotecaControlador extends Navegacion {
	 private Usuario usuario; // Usuario autenticado
	 private ConexionBD conexionBD = new ConexionBD();
	    @FXML
	    private VBox contenedorResultadosBusqueda; // Ajustado para coincidir con el FXML

	    @FXML
	    private HBox contenedorJuegosBusqueda;

	    @FXML
	    private Label mensajeNoJuegos;

	    @FXML
		private Button cerrarSesionButton;
		@FXML
		private Button cambiarPassword;
		@FXML
		private Button inicio;
		@FXML
		private Button deseados;
		@FXML
		private Button biblioteca;
		@FXML
		private Button user;
	    
	    @FXML
	    public void initialize() {
	        mensajeNoJuegos.setVisible(true);
	        mensajeNoJuegos.setText("Cargando juegos biblioteca...");
	        contenedorResultadosBusqueda.setVisible(false);
	        inicio.setOnAction(event -> abrirInicio(event, usuario));
			deseados.setOnAction(event -> abrirDeseados(event, usuario));
			user.setOnAction(event -> abrirUser(event, usuario));
	    }

	    /**
	     * Método para recibir el usuario autenticado y cargar sus juegos deseados.
	     */
	    public void setUsuario(Usuario usuario) {
	        if (usuario == null) {
	            System.out.println("ERROR: Usuario es null en bibliotecaControlador.");
	            mensajeNoJuegos.setText("Error al cargar los juegos.");
	            return;
	        }
	        this.usuario = usuario;
	        cargarJuegosBiblioteca();
	    }

	    /**
	     * Carga los juegos deseados del usuario y los muestra en el contenedor.
	     */
	    private void cargarJuegosBiblioteca() {
	        if (usuario == null || usuario.getJuegosBiblioteca() == null) {
	            mostrarMensajeSinJuegos();
	            return;
	        }

	        List<String> getJuegosBiblioteca = usuario.getJuegosBiblioteca();

	        if (getJuegosBiblioteca.isEmpty()) {
	            mostrarMensajeSinJuegos();
	            return;
	        }

	        // Cargar juegos en segundo plano desde la base de datos
	        Service<List<String[]>> service = new Service<>() {
	            @Override
	            protected Task<List<String[]>> createTask() {
	                return new Task<>() {
	                    @Override
	                    protected List<String[]> call() {
	                        return conexionBD.obtenerJuegosPorIds(getJuegosBiblioteca);
	                    }
	                };
	            }
	        };

	        service.setOnSucceeded(event -> {
	            List<String[]> juegos = service.getValue();

	            if (juegos.isEmpty()) {
	                mostrarMensajeSinJuegos();
	            } else {
	                mensajeNoJuegos.setText("Juegos en Biblioteca");
	                contenedorResultadosBusqueda.setVisible(true);
	                cargarYMostrarJuegos(juegos);
	            }
	        });

	        service.start();
	    }


	    /**
	     * Muestra el mensaje de "No se encontraron juegos en deseados".
	     */
	    private void mostrarMensajeSinJuegos() {
	        mensajeNoJuegos.setText("No se encontraron juegos en deseados");
	        mensajeNoJuegos.setVisible(true);
	        contenedorResultadosBusqueda.setVisible(false);
	        contenedorJuegosBusqueda.getChildren().clear();
	    }

	    private void cargarYMostrarJuegos(List<String[]> juegos) {
	        contenedorJuegosBusqueda.getChildren().clear();
	        for (String[] juego : juegos) {
	            StackPane juegoBox = crearClipJuego(juego);
	            contenedorJuegosBusqueda.getChildren().add(juegoBox);
	        }
	    }

	    private StackPane crearClipJuego(String[] juego) {
	        StackPane juegoBox = new StackPane();
	        juegoBox.getStyleClass().add("juego-box");
	        juegoBox.setPrefSize(250, 400);

	     // Fondo desenfocado
	        ImageView fondoBlur = new ImageView();
	        fondoBlur.setFitWidth(250);
	        fondoBlur.setFitHeight(400);
	        fondoBlur.setPreserveRatio(false);
	        fondoBlur.setEffect(new GaussianBlur(20));

	        
	        ImageView portada = new ImageView();
	        String urlImagen = juego[2];
			portada.setFitWidth(220);
			portada.setFitHeight(220);
			portada.setPreserveRatio(true);

	        // Descargar la imagen en un hilo separado y actualizar en el hilo de JavaFX
	        new Thread(() -> {
	            Image imagen = descargarImagen(urlImagen);
	            Platform.runLater(() -> portada.setImage(imagen));
	            Platform.runLater(() -> fondoBlur.setImage(imagen));
	        }).start();

	        

	     // Rectángulo negro para la información del juego
	        Rectangle rectanguloInfo = new Rectangle(250, 100, Color.BLACK);
	        rectanguloInfo.setTranslateY(150);

	        // Nombre y rating del juego
	        Label nombre = new Label(juego[1]);
	        nombre.getStyleClass().add("label-juego-nombre");

	        Label rating = new Label("Rating: " + juego[3]);
	        rating.getStyleClass().add("label-juego-rating");

	        // Contenedor para la información del juego
	        VBox infoBox = new VBox(5, nombre, rating);
	        infoBox.setAlignment(Pos.CENTER);
	        infoBox.setTranslateY(150);

	        // Añadir elementos al StackPane
	        juegoBox.getChildren().addAll(fondoBlur, portada, rectanguloInfo, infoBox);

	     // Agregar evento de clic al StackPane
	        juegoBox.setOnMouseClicked(event -> {
	            try {
	                // Cargar la pantalla de detalles del juego
	                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Juego.fxml"));
	                Parent root = loader.load();

	                // Obtener el controlador de la pantalla de detalles del juego
	                JuegoControlador juegoControlador = loader.getController();
	                juegoControlador.inicializar(juego[0], usuario); // Pasar la información del juego
	                juegoControlador.setJuego(juego);
	                
	                // Cambiar la escena actual
	                Stage stage = (Stage) juegoBox.getScene().getWindow();
	                stage.setScene(new Scene(root));
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        });

	        return juegoBox;
	    }
	    /**
	     * Descarga una imagen desde la URL proporcionada.
	     * Si la URL no es válida o la imagen no se puede cargar, usa una imagen por defecto.
	     */
	    private Image descargarImagen(String urlImagen) {
	        if (urlImagen == null || urlImagen.isEmpty() || !urlImagen.startsWith("http")) {
	            System.out.println("URL de imagen inválida: " + urlImagen);
	            return cargarImagenLocal("/images/logo.png");
	        }

	        try {
	            HttpURLConnection connection = (HttpURLConnection) new URL(urlImagen).openConnection();
	            connection.setRequestMethod("GET");
	            connection.setConnectTimeout(5000); // 5 segundos de espera
	            connection.setReadTimeout(5000);
	            connection.setDoInput(true);
	            connection.connect();

	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                InputStream inputStream = connection.getInputStream();
	                return new Image(inputStream);
	            } else {
	                System.out.println("No se pudo descargar la imagen: " + urlImagen);
	            }
	        } catch (Exception e) {
	            System.out.println("Error al descargar la imagen: " + urlImagen);
	        }

	        // En caso de error, devolver la imagen por defecto
	        return cargarImagenLocal("/images/logo.png");
	    }

	    /**
	     * Carga una imagen local como respaldo en caso de fallo con la URL.
	     */
	    private Image cargarImagenLocal(String path) {
	        InputStream is = getClass().getResourceAsStream(path);
	        if (is != null) {
	            return new Image(is);
	        } else {
	            System.out.println("Imagen local no encontrada: " + path);
	            return null;
	        }
	    }
	
	}