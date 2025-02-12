package controllers;

import java.util.List;
import conexiones.ConexionAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import model.Usuario;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class HomeControlador {

    private Usuario usuario; // Almacena el usuario autenticado

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
    private ScrollPane scrollPaneCategorias;
    
    @FXML
    private ComboBox<String> filtroComboBox; 
    
    @FXML
    public void initialize() {
        ConexionAPI conexionAPI = new ConexionAPI();
        ObservableList<String> opcionesFiltro = FXCollections.observableArrayList(
                "Plataforma", "Género", "Rating"
            );
        filtroComboBox.setItems(opcionesFiltro);
        
        // Cargar juegos en segundo plano
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerMejoresRatings(), contenedorJuegosPopulares);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMasNuevos(), contenedorJuegosNuevos);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMultiplayer(), contenedorJuegosMultiplayer);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosSingleplayer(), contenedorJuegosSingleplayer);
    }

    private void cargarJuegosEnSegundoPlano(List<String[]> juegos, HBox contenedor) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        // Simular una carga larga
                        Thread.sleep(1000);
                        return null;
                    }
                };
            }
        };

        service.setOnSucceeded(event -> {
            cargarYMostrarJuegos(juegos, contenedor);
        });

        service.start();
    }

    @FXML
    private void buscarJuegos() {
        String query = campoBusqueda.getText().trim();
        if (!query.isEmpty()) {
            ConexionAPI conexionAPI = new ConexionAPI();
            Service<List<String[]>> service = new Service<List<String[]>>() {
                @Override
                protected Task<List<String[]>> createTask() {
                    return new Task<List<String[]>>() {
                        @Override
                        protected List<String[]> call() throws Exception {
                            return conexionAPI.buscarJuegosPorNombre(query);
                        }
                    };
                }
            };

            service.setOnSucceeded(event -> {
                List<String[]> resultados = service.getValue();
                contenedorJuegosBusqueda.getChildren().clear();
                cargarYMostrarJuegos(resultados, contenedorJuegosBusqueda);
                contenedorResultadosBusqueda.setVisible(true);
                scrollPaneCategorias.setVisible(false); // Oculta las categorías
            });

            service.start();
        } else {
            contenedorResultadosBusqueda.setVisible(false);
            scrollPaneCategorias.setVisible(true); // Muestra las categorías si no hay búsqueda
        }
    }

    // Método para recibir el usuario autenticado
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

        // Rectángulo negro para la información del juego
        Rectangle rectanguloInfo = new Rectangle(250, 100, Color.BLACK);
        rectanguloInfo.setTranslateY(150); // Posiciona el rectángulo en la parte inferior

        // Nombre y rating del juego
        Label nombre = new Label(juego[1]);
        nombre.getStyleClass().add("label-juego-nombre");

        Label rating = new Label("Rating: " + juego[3]);
        rating.getStyleClass().add("label-juego-rating");

        // Contenedor para la información del juego
        VBox infoBox = new VBox(5, nombre, rating); // Espaciado entre nombre y rating
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setTranslateY(150); // Posiciona el VBox dentro del rectángulo negro

        // Añadir elementos al StackPane (el orden importa)
        juegoBox.getChildren().addAll(fondoBlur, portada, rectanguloInfo, infoBox);

        return juegoBox;
    }
    
    @FXML
    private void irAHome(ActionEvent event) {
        try {
            // Cargar la vista de Home
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Home.fxml"));
            Parent root = loader.load();

            // Obtener la escena actual y cambiarla
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void aplicarFiltro(ActionEvent event) {
        String filtroSeleccionado = filtroComboBox.getValue(); // Obtener el filtro seleccionado
        String query = campoBusqueda.getText().trim(); // Obtener el texto de búsqueda

        if (filtroSeleccionado != null && !query.isEmpty()) {
            ConexionAPI conexionAPI = new ConexionAPI();
            Service<List<String[]>> service = new Service<List<String[]>>() {
                @Override
                protected Task<List<String[]>> createTask() {
                    return new Task<List<String[]>>() {
                        @Override
                        protected List<String[]> call() throws Exception {
                            switch (filtroSeleccionado) {
                                case "Plataforma":
                                    return conexionAPI.buscarJuegosPorPlataforma(query);
                                case "Género":
                                    return conexionAPI.buscarJuegosPorGenero(query);
                                case "Rating":
                                    return conexionAPI.buscarJuegosPorRating(query);
                                default:
                                    return conexionAPI.buscarJuegosPorNombre(query);
                            }
                        }
                    };
                }
            };

            service.setOnSucceeded(event2 -> {
                List<String[]> resultados = service.getValue();
                contenedorJuegosBusqueda.getChildren().clear();
                cargarYMostrarJuegos(resultados, contenedorJuegosBusqueda);
                contenedorResultadosBusqueda.setVisible(true);
            });

            service.start();
        } else {
            contenedorResultadosBusqueda.setVisible(false);
        }
    }
}