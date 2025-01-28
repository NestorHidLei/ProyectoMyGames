package controllers;

import java.util.List;
import conexiones.ConexionAPI;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    public void initialize() {
        ConexionAPI conexionAPI = new ConexionAPI();
        cargarYMostrarJuegos(conexionAPI.obtenerMejoresRatings(), contenedorJuegosPopulares);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosMasNuevos(), contenedorJuegosNuevos);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosMultiplayer(), contenedorJuegosMultiplayer);
        cargarYMostrarJuegos(conexionAPI.obtenerJuegosSingleplayer(), contenedorJuegosSingleplayer);
    }

    private void cargarYMostrarJuegos(List<String[]> juegos, HBox contenedor) {
        contenedor.getChildren().clear();
        for (String[] juego : juegos) {
            VBox juegoBox = crearClipJuego(juego);
            contenedor.getChildren().add(juegoBox);
        }
    }

    private VBox crearClipJuego(String[] juego) {
        VBox juegoBox = new VBox(10);
        juegoBox.setStyle("-fx-background-color: #1e1e1e; -fx-border-radius: 15; -fx-background-radius: 15; -fx-padding: 15; -fx-alignment: center;");
        juegoBox.setPrefSize(250, 400);

        ImageView portada = new ImageView();
        portada.setFitWidth(220);
        portada.setFitHeight(220);
        portada.setPreserveRatio(true);
        portada.setImage(new Image(juego[2]));

        Label nombre = new Label(juego[1]);
        nombre.getStyleClass().add("label-juego-nombre");

        Label rating = new Label("Rating: " + juego[3]);
        rating.getStyleClass().add("label-juego-rating");

        juegoBox.getChildren().addAll(portada, nombre, rating);
        return juegoBox;
    }
}


