package controllers;

import conexiones.ConexionAPI;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controlador para la sección de juegos deseados.
 */
public class DeseadosControlador {

    private Usuario usuario; // Usuario autenticado

    @FXML
    private VBox contenedorResultadosBusqueda; // Ajustado para coincidir con el FXML

    @FXML
    private HBox contenedorJuegosBusqueda;

    @FXML
    private Label mensajeNoJuegos;

    @FXML
    public void initialize() {
        mensajeNoJuegos.setVisible(true);
        mensajeNoJuegos.setText("Cargando juegos en Biblioteca...");
        contenedorResultadosBusqueda.setVisible(false);
    }

    /**
     * Método para recibir el usuario autenticado y cargar sus juegos deseados.
     */
    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("ERROR: Usuario es null en BibliotecaControlador.");
            mensajeNoJuegos.setText("Error al cargar los juegos.");
            return;
        }
        this.usuario = usuario;
        cargarJuegosDeseados();
    }

    /**
     * Carga los juegos deseados del usuario y los muestra en el contenedor.
     */
    private void cargarJuegosDeseados() {
        if (usuario == null || usuario.getJuegosDeseados() == null) {
            mostrarMensajeSinJuegos();
            return;
        }

        // Convertir la cadena de IDs a una lista
        List<String> idsJuegosDeseados = usuario.getJuegosDeseados();

        if (idsJuegosDeseados.isEmpty()) {
            mostrarMensajeSinJuegos();
            return;
        }

        ConexionAPI conexionAPI = new ConexionAPI();

        // Cargar juegos en segundo plano
        Service<List<String[]>> service = new Service<>() {
            @Override
            protected Task<List<String[]>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<String[]> call() {
                        List<String[]> juegos = new ArrayList<>();
                        for (String id : idsJuegosDeseados) {
                            String[] juego = conexionAPI.buscarJuegoPorId(id.trim());
                            if (juego != null) {
                                juegos.add(juego);
                            }
                        }
                        return juegos;
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
        mensajeNoJuegos.setText("No se encontraron juegos en biblioteca");
        mensajeNoJuegos.setVisible(true);
        contenedorResultadosBusqueda.setVisible(false);
        contenedorJuegosBusqueda.getChildren().clear();
    }

    /**
     * Muestra los juegos obtenidos en el contenedor.
     */
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

        // Portada del juego (imagen sin desenfoque)
        ImageView portada = new ImageView();
        portada.setFitWidth(220);
        portada.setFitHeight(220);
        portada.setPreserveRatio(true);

        // Verifica si la URL de la imagen es válida
        String urlImagen = juego[2];
        if (urlImagen != null && !urlImagen.isEmpty() && urlImagen.startsWith("http")) {
            try {
                Image imagen = new Image(urlImagen);
                portada.setImage(imagen);
                fondoBlur.setImage(imagen);
            } catch (IllegalArgumentException e) {
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                portada.setImage(imagenPredeterminada);
                fondoBlur.setImage(imagenPredeterminada);
            }
        } else {
            Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
            portada.setImage(imagenPredeterminada);
            fondoBlur.setImage(imagenPredeterminada);
        }

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
}
