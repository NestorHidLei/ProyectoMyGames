package controllers;

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
import java.util.List;

/**
 * Controlador para la sección de juegos deseados.
 * Permite visualizar los juegos que el usuario ha marcado como favoritos o deseados.
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

    /**
     * Inicializa la vista y configura los eventos de los botones de navegación.
     */
    @FXML
    public void initialize() {
        mensajeNoJuegos.setVisible(true);
        mensajeNoJuegos.setText("Cargando juegos deseados...");
        contenedorResultadosBusqueda.setVisible(false);

        inicio.setOnAction(event -> abrirInicio(event, usuario));
        biblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
        user.setOnAction(event -> abrirUser(event, usuario));
    }

    /**
     * Asigna el usuario autenticado y carga los juegos deseados desde la base de datos.
     *
     * @param usuario Usuario autenticado en la sesión.
     */
    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("ERROR: Usuario es null en DeseadosControlador.");
            mensajeNoJuegos.setText("Error al cargar los juegos.");
            return;
        }
        this.usuario = usuario;
        cargarJuegosDeseados();
    }

    /**
     * Carga los juegos que el usuario ha marcado como deseados.
     * Si no hay juegos, muestra un mensaje.
     */
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

        // Cargar los juegos desde la base de datos en un hilo secundario
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
                mensajeNoJuegos.setText("Juegos en Deseados");
                contenedorResultadosBusqueda.setVisible(true);
                cargarYMostrarJuegos(juegos);
            }
        });

        service.start();
    }

    /**
     * Muestra un mensaje indicando que no hay juegos en la lista de deseados.
     */
    private void mostrarMensajeSinJuegos() {
        mensajeNoJuegos.setText("No se encontraron juegos en deseados");
        mensajeNoJuegos.setVisible(true);
        contenedorResultadosBusqueda.setVisible(false);
        contenedorJuegosBusqueda.getChildren().clear();
    }

    /**
     * Carga y muestra los juegos deseados en la interfaz de usuario.
     *
     * @param juegos Lista de juegos obtenidos de la base de datos.
     */
    private void cargarYMostrarJuegos(List<String[]> juegos) {
        contenedorJuegosBusqueda.getChildren().clear();
        for (String[] juego : juegos) {
            StackPane juegoBox = crearClipJuego(juego);
            contenedorJuegosBusqueda.getChildren().add(juegoBox);
        }
    }

    /**
     * Crea un elemento visual para mostrar un juego en la lista de deseados.
     *
     * @param juego Array con los datos del juego: ID, nombre, imagen y calificación.
     * @return StackPane que representa el juego en la interfaz.
     */
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

        // Portada del juego
        ImageView portada = new ImageView();
        String urlImagen = juego[2];
        portada.setFitWidth(220);
        portada.setFitHeight(220);
        portada.setPreserveRatio(true);

        // Descargar la imagen en un hilo separado
        new Thread(() -> {
            Image imagen = descargarImagen(urlImagen);
            Platform.runLater(() -> {
                portada.setImage(imagen);
                fondoBlur.setImage(imagen);
            });
        }).start();

        // Información del juego
        Rectangle rectanguloInfo = new Rectangle(250, 100, Color.BLACK);
        rectanguloInfo.setTranslateY(150);

        Label nombre = new Label(juego[1]);
        nombre.getStyleClass().add("label-juego-nombre");

        Label rating = new Label("Rating: " + juego[3]);
        rating.getStyleClass().add("label-juego-rating");

        VBox infoBox = new VBox(5, nombre, rating);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setTranslateY(150);

        // Agregar elementos al StackPane
        juegoBox.getChildren().addAll(fondoBlur, portada, rectanguloInfo, infoBox);

        // Evento de clic para abrir detalles del juego
        juegoBox.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Juego.fxml"));
                Parent root = loader.load();
                JuegoControlador juegoControlador = loader.getController();
                juegoControlador.inicializar(juego[0], usuario);
                juegoControlador.setJuego(juego);

                Stage stage = (Stage) juegoBox.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return juegoBox;
    }

    /**
     * Descarga una imagen desde una URL. Si la imagen no se puede cargar, usa una imagen por defecto.
     *
     * @param urlImagen URL de la imagen del juego.
     * @return Objeto Image con la imagen descargada o la imagen por defecto.
     */
    private Image descargarImagen(String urlImagen) {
        if (urlImagen == null || urlImagen.isEmpty() || !urlImagen.startsWith("http")) {
            return cargarImagenLocal("/images/logo.png");
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlImagen).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return new Image(connection.getInputStream());
            }
        } catch (Exception e) {
            System.out.println("Error al descargar la imagen: " + urlImagen);
        }

        return cargarImagenLocal("/images/logo.png");
    }

    /**
     * Carga una imagen local en caso de error al descargar una imagen.
     *
     * @param path Ruta del archivo de imagen local.
     * @return Objeto Image con la imagen cargada.
     */
    private Image cargarImagenLocal(String path) {
        InputStream is = getClass().getResourceAsStream(path);
        return (is != null) ? new Image(is) : null;
    }
}
