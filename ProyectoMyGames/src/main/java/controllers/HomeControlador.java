package controllers;


import java.io.IOException;
import java.util.List;
import conexiones.ConexionAPI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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

/**
 * Controlador para la pantalla principal del sistema.
 * Muestra las categorías de juegos y permite realizar búsquedas y filtrados.
 */
public class HomeControlador extends Navegacion {

    private Usuario usuario; // Usuario autenticado

    @FXML
    private HBox contenedorJuegosPopulares, contenedorJuegosNuevos, contenedorJuegosMultiplayer, contenedorJuegosSingleplayer;

    @FXML
    private TextField campoBusqueda;

    @FXML
    private VBox contenedorResultadosBusqueda;

    @FXML
    private HBox contenedorJuegosBusqueda;

    @FXML
    private ScrollPane scrollPaneCategorias;

    @FXML
    private ComboBox<String> filtroComboBoxGenero, filtroComboBoxPlataforma;

    @FXML
    private Button botonBuscar, botonFiltrar, botonInicio, botonBiblioteca, botonDeseados, botonCuenta;

    private ConexionAPI conexionAPI = new ConexionAPI();

    /**
     * Inicializa la vista principal y configura los eventos de los botones de navegación.
     */
    @FXML
    public void initialize() {
        ObservableList<String> opcionesFiltroPlataforma = FXCollections.observableArrayList(
                "PC", "Xbox", "PlayStation", "Nintendo", "Atari", "Sega", "3DO", "Neo Geo", "Web"
        );
        ObservableList<String> opcionesFiltroGenero = FXCollections.observableArrayList(
                "Acción", "Aventura", "Plataformas", "RPG", "Shooter", "Deportes",
                "Estrategia", "Simulación", "Puzle", "Arcade", "Carreras",
                "Lucha", "Familia", "Tablero", "Educación", "Tarjeta", "Indie", "Música"
        );

        filtroComboBoxGenero.setItems(opcionesFiltroGenero);
        filtroComboBoxPlataforma.setItems(opcionesFiltroPlataforma);

        // Cargar juegos en segundo plano por categorías
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerMejoresRatings(), contenedorJuegosPopulares);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMasNuevos(), contenedorJuegosNuevos);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMultiplayer(), contenedorJuegosMultiplayer);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosSingleplayer(), contenedorJuegosSingleplayer);

        botonInicio.setOnAction(event -> abrirInicio(event, usuario));
        botonBiblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
        botonDeseados.setOnAction(event -> abrirDeseados(event, usuario));
        botonCuenta.setOnAction(event -> abrirUser(event, usuario));
    }

    /**
     * Carga juegos en segundo plano y actualiza la interfaz.
     *
     * @param juegos Lista de juegos a cargar.
     * @param contenedor Contenedor en el que se mostrarán los juegos.
     */
    private void cargarJuegosEnSegundoPlano(List<String[]> juegos, HBox contenedor) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(1000); // Simula carga de datos
                        return null;
                    }
                };
            }
        };

        service.setOnSucceeded(event -> cargarYMostrarJuegos(juegos, contenedor));
        service.start();
    }

    /**
     * Realiza una búsqueda de juegos por nombre ingresado en el campo de búsqueda.
     */
    @FXML
    private void buscarJuegos() {
        String query = campoBusqueda.getText().trim();
        if (!query.isEmpty()) {
            Service<List<String[]>> service = new Service<>() {
                @Override
                protected Task<List<String[]>> createTask() {
                    return new Task<>() {
                        @Override
                        protected List<String[]> call() {
                            return conexionAPI.buscarJuegosPorNombre(query);
                        }
                    };
                }
            };

            service.setOnSucceeded(event -> {
                List<String[]> resultados = service.getValue();
                cargarYMostrarJuegos(resultados, contenedorJuegosBusqueda);
                contenedorResultadosBusqueda.setVisible(true);
                scrollPaneCategorias.setVisible(false);
            });

            service.start();
        }
    }

    /**
     * Aplica un filtro de búsqueda basado en género y/o plataforma.
     *
     * @param event Evento de acción del botón de filtro.
     */
    @FXML
    private void aplicarFiltro(ActionEvent event) {
        String filtroGenero = filtroComboBoxGenero.getValue();
        String filtroPlataforma = filtroComboBoxPlataforma.getValue();
        final String query = campoBusqueda.getText().trim();

        if (filtroGenero != null || filtroPlataforma != null) {
            Service<List<String[]>> service = new Service<>() {
                @Override
                protected Task<List<String[]>> createTask() {
                    return new Task<>() {
                        @Override
                        protected List<String[]> call() {
                            return conexionAPI.buscarJuegosPorGeneroYPlataforma(query, filtroGenero, filtroPlataforma);
                        }
                    };
                }
            };

            service.setOnSucceeded(event2 -> {
                List<String[]> resultados = service.getValue();
                if (resultados != null && !resultados.isEmpty()) {
                    cargarYMostrarJuegos(resultados, contenedorJuegosBusqueda);
                    contenedorResultadosBusqueda.setVisible(true);
                    scrollPaneCategorias.setVisible(false);
                }
            });

            service.start();
        }
    }

    /**
     * Carga y muestra juegos en un contenedor específico.
     *
     * @param juegos Lista de juegos a mostrar.
     * @param contenedor Contenedor donde se visualizarán los juegos.
     */
    private void cargarYMostrarJuegos(List<String[]> juegos, HBox contenedor) {
        contenedor.getChildren().clear();
        for (String[] juego : juegos) {
            StackPane juegoBox = crearClipJuego(juego);
            contenedor.getChildren().add(juegoBox);
        }
    }

    /**
     * Crea un componente visual para un juego.
     *
     * @param juego Array con información del juego.
     * @return StackPane representando la tarjeta del juego.
     */
    private StackPane crearClipJuego(String[] juego) {
        StackPane juegoBox = new StackPane();
        juegoBox.getStyleClass().add("juego-box");
        juegoBox.setPrefSize(250, 400);

        ImageView fondoBlur = new ImageView();
        fondoBlur.setFitWidth(250);
        fondoBlur.setFitHeight(400);
        fondoBlur.setPreserveRatio(false);
        fondoBlur.setEffect(new GaussianBlur(20));

        ImageView portada = new ImageView();
        portada.setFitWidth(220);
        portada.setFitHeight(220);
        portada.setPreserveRatio(true);

        // Cargar imagen del juego
        String urlImagen = juego[2];
        if (urlImagen != null && urlImagen.startsWith("http")) {
            try {
                Image imagen = new Image(urlImagen);
                portada.setImage(imagen);
                fondoBlur.setImage(imagen);
            } catch (IllegalArgumentException e) {
                Image imagenPredeterminada = new Image(getClass().getResourceAsStream("/images/logo.png"));
                portada.setImage(imagenPredeterminada);
                fondoBlur.setImage(imagenPredeterminada);
            }
        }

        Rectangle rectanguloInfo = new Rectangle(250, 100, Color.BLACK);
        rectanguloInfo.setTranslateY(150);

        Label nombre = new Label(juego[1]);
        Label rating = new Label("Rating: " + juego[3]);

        VBox infoBox = new VBox(5, nombre, rating);
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setTranslateY(150);

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
     * Asigna el usuario autenticado a la vista.
     *
     * @param usuario Usuario autenticado en sesión.
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
