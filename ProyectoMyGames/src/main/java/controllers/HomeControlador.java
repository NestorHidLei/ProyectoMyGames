package controllers;

import java.util.List;
import conexiones.ConexionAPI;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class HomeControlador {

    @FXML
    private HBox contenedorJuegosPopulares;

    @FXML
    private HBox contenedorJuegosNuevos;

    @FXML
    private HBox contenedorJuegosMultiplayer;

    @FXML
    private HBox contenedorJuegosSingleplayer;

    @FXML
    private TextField campoBusqueda;

    @FXML
    private VBox contenedorResultadosBusqueda;

    @FXML
    private HBox contenedorJuegosBusqueda;
    
    @FXML
    private StackPane juegoBox;

    @FXML
    public void initialize() {
        ConexionAPI conexionAPI = new ConexionAPI();
        cargarYMostrarJuegos(conexionAPI.obtenerMejoresRatings(), contenedorJuegosPopulares);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosMasNuevos(), contenedorJuegosNuevos);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosMultiplayer(), contenedorJuegosMultiplayer);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosSingleplayer(), contenedorJuegosSingleplayer);
    }

    @FXML
    private void buscarJuegos() {
        String query = campoBusqueda.getText().trim();
        if (!query.isEmpty()) {
            ConexionAPI conexionAPI = new ConexionAPI();
            List<String[]> resultados = conexionAPI.buscarJuegosPorNombre(query);
            contenedorJuegosBusqueda.getChildren().clear();
            cargarYMostrarJuegos(resultados, contenedorJuegosBusqueda);
            contenedorResultadosBusqueda.setVisible(true);
        } else {
            contenedorResultadosBusqueda.setVisible(false);
        }
    }

    private void cargarYMostrarJuegos(List<String[]> juegos, HBox contenedor) {
        contenedor.getChildren().clear();
        for (String[] juego : juegos) {
            StackPane juegoBox = crearClipJuego(juego);
            contenedor.getChildren().add(juegoBox);
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
        fondoBlur.setEffect(new GaussianBlur(20)); // Aplicar efecto de desenfoque

        // Portada del juego
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
                fondoBlur.setImage(imagen); // Usar la misma imagen para el fondo desenfocado
            } catch (IllegalArgumentException e) {
                // Si la URL es inválida, carga una imagen predeterminada
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                portada.setImage(imagenPredeterminada);
                fondoBlur.setImage(imagenPredeterminada);
            }
        } else {
            // Si no hay URL, carga una imagen predeterminada
            Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
            portada.setImage(imagenPredeterminada);
            fondoBlur.setImage(imagenPredeterminada);
        }

        // Nombre y rating del juego
        Label nombre = new Label(juego[1]);
        nombre.getStyleClass().add("label-juego-nombre");

        Label rating = new Label("Rating: " + juego[3]);
        rating.getStyleClass().add("label-juego-rating");

        // Contenedor para el contenido del juego
        VBox contenido = new VBox(10, portada, nombre, rating);
        contenido.setAlignment(Pos.CENTER);

        // Añadir fondo desenfocado y contenido al StackPane
        juegoBox.getChildren().addAll(fondoBlur, contenido);

        return juegoBox;
    }
}