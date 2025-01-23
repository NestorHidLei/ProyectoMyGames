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
    public void initialize() {
        // Cargar y mostrar los juegos al inicializar.
        List<String[]> juegosPopulares = cargarJuegos("rating");
        mostrarJuegos(juegosPopulares, contenedorJuegosPopulares);

        List<String[]> juegosNuevos = cargarJuegos("released");
        mostrarJuegos(juegosNuevos, contenedorJuegosNuevos);
    }

    private List<String[]> cargarJuegos(String criterio) {
        ConexionAPI conexionAPI = new ConexionAPI();
        switch (criterio) {
            case "rating":
                return conexionAPI.obtenerMejoresRatings();
            case "released":
                return conexionAPI.obtenerJuegosMasNuevos();
            default:
                return List.of();
        }
    }

    private void mostrarJuegos(List<String[]> juegos, HBox contenedor) {
        contenedor.getChildren().clear();
        for (String[] juego : juegos) {
            VBox juegoBox = new VBox(10);
            juegoBox.setStyle("-fx-alignment: center;");

            ImageView portada = new ImageView();
            portada.setFitWidth(150);
            portada.setFitHeight(200);
            portada.setPreserveRatio(true);
            portada.setImage(new Image(juego[2])); // URL de la imagen.

            Label nombre = new Label(juego[1]);
            nombre.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            Label rating = new Label("Rating: " + juego[3]);
            rating.setStyle("-fx-text-fill: gray; -fx-font-size: 12px;");

            juegoBox.getChildren().addAll(portada, nombre, rating);
            contenedor.getChildren().add(juegoBox);
        }
    }
}
