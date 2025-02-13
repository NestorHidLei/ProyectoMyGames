package controllers;

import conexiones.ConexionBD;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import model.Usuario;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Controlador para la sección de juegos deseados.
 */
public class DeseadosControlador extends Navegacion {

    private Usuario usuario; // Usuario autenticado
    private ConexionBD conexionBD = new ConexionBD();

    @FXML
    private VBox contenedorResultadosBusqueda;

    @FXML
    private HBox contenedorJuegosBusqueda;

    @FXML
    private Label mensajeNoJuegos;

    @FXML
    private Button inicio, biblioteca, user;

    @FXML
    public void initialize() {
        mensajeNoJuegos.setVisible(true);
        mensajeNoJuegos.setText("Cargando juegos deseados...");
        contenedorResultadosBusqueda.setVisible(false);
        
        inicio.setOnAction(event -> abrirInicio(event, usuario));
        biblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
        user.setOnAction(event -> abrirUser(event, usuario));
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("ERROR: Usuario es null en DeseadosControlador.");
            mensajeNoJuegos.setText("Error al cargar los juegos.");
            return;
        }
        this.usuario = usuario;
        cargarJuegosDeseados();
    }

    private void cargarJuegosDeseados() {
        if (usuario == null || usuario.getJuegosDeseados() == null) {
            mostrarMensajeSinJuegos();
            return;
        }

        List<String> idsJuegosDeseados = usuario.getJuegosDeseados();

        if (idsJuegosDeseados.isEmpty()) {
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
                        return conexionBD.obtenerJuegosPorIds(idsJuegosDeseados);
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

