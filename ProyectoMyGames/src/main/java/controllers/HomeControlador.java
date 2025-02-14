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

public class HomeControlador extends Navegacion{

    private Usuario usuario; // Usuario autenticado

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
    private ScrollPane scrollPaneCategorias;

    @FXML
    private ComboBox<String> filtroComboBoxGenero;
    
    @FXML
    private ComboBox<String> filtroComboBoxPlataforma;

    @FXML
    private Button botonBuscar;

    @FXML
    private Button botonFiltrar;
    
    @FXML
    private Button botonInicio;
    
    @FXML
    private Button botonBiblioteca;
   
    @FXML
    private Button botonDeseados;
    
    @FXML
    private Button botonCuenta;
    
    private ConexionAPI conexionAPI = new ConexionAPI();

    @FXML
    public void initialize() {
        ObservableList<String> opcionesFiltroPlataforma = FXCollections.observableArrayList(
                "PC", "Xbox", "PlayStation", "Nintendo", "Atari", "Sega", "3DO", "Neo Geo","Web"
        );
        ObservableList<String> opcionesFiltroGenero = FXCollections.observableArrayList(
        	    "Acción", "Aventura", "Plataformas", "RPG", "Shooter", "Deportes",
        	    "Estrategia", "Simulación", "Puzle", "Arcade", "Carreras",
        	    "Lucha", "Familia", "Tablero", "Educación", "Tarjeta", "Indie", "Música"
        	);

        filtroComboBoxGenero.setItems(opcionesFiltroGenero);
        filtroComboBoxPlataforma.setItems(opcionesFiltroPlataforma);


        // Cargar juegos en segundo plano
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerMejoresRatings(), contenedorJuegosPopulares);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMasNuevos(), contenedorJuegosNuevos);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosMultiplayer(), contenedorJuegosMultiplayer);
        cargarJuegosEnSegundoPlano(conexionAPI.obtenerJuegosSingleplayer(), contenedorJuegosSingleplayer);
        
        botonInicio.setOnAction(event -> abrirInicio(event, usuario));
        botonBiblioteca.setOnAction(event -> abrirBiblioteca(event, usuario));
        botonDeseados.setOnAction(event -> abrirDeseados(event, usuario));
        botonCuenta.setOnAction(event -> abrirUser(event, usuario));
        
    }

    private void cargarJuegosEnSegundoPlano(List<String[]> juegos, HBox contenedor) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
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

    @FXML
    private void aplicarFiltro(ActionEvent event) {
        String filtroSeleccionadoGenero = filtroComboBoxGenero.getValue() != null ? filtroComboBoxGenero.getValue().toString() : "";
        String filtroSeleccionadoPlataforma = filtroComboBoxPlataforma.getValue() != null ? filtroComboBoxPlataforma.getValue() : "";
        final String query = campoBusqueda.getText().trim();

        // Si al menos uno de los filtros es válido, se ejecuta la búsqueda
        if (filtroSeleccionadoGenero != null || filtroSeleccionadoPlataforma != null) {
            Service<List<String[]>> service = new Service<>() {
                @Override
                protected Task<List<String[]>> createTask() {
                    return new Task<>() {
                        @Override
                        protected List<String[]> call() {
                            if (filtroSeleccionadoGenero != null && esGenero(filtroSeleccionadoGenero) &&
                                filtroSeleccionadoPlataforma != null && esPlataforma(filtroSeleccionadoPlataforma)) {
                                return conexionAPI.buscarJuegosPorGeneroYPlataforma(query, filtroSeleccionadoGenero.toLowerCase(), filtroSeleccionadoPlataforma);
                            } else if (filtroSeleccionadoGenero != null && esGenero(filtroSeleccionadoGenero)) {
                                return conexionAPI.buscarJuegosPorGenero(query, filtroSeleccionadoGenero.toLowerCase());
                            } else if (filtroSeleccionadoPlataforma != null && esPlataforma(filtroSeleccionadoPlataforma) ) {
                                return conexionAPI.buscarJuegosPorPlataforma(query, filtroSeleccionadoPlataforma);
                            } else {
                                return conexionAPI.buscarJuegosPorNombre(query);
                            }
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
                } else {
                    contenedorResultadosBusqueda.setVisible(false);
                }
            });

            service.setOnFailed(event2 -> {
                Throwable error = service.getException();
                System.err.println("Error al cargar los juegos: " + error.getMessage());
                contenedorResultadosBusqueda.setVisible(false);
            });

            service.start();
        } else {
            contenedorResultadosBusqueda.setVisible(false);
        }
    }

    private boolean esGenero(String filtro) {
        return List.of("Acción", "Aventura", "Plataformas", "RPG", "Shooter", "Estrategia").contains(filtro);
    }

    private boolean esPlataforma(String filtro) {
        return List.of("PC", "Xbox", "PlayStation", "Nintendo").contains(filtro);
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
        fondoBlur.setEffect(new GaussianBlur(20));

        // Portada del juego
        ImageView portada = new ImageView();
        portada.setFitWidth(220);
        portada.setFitHeight(220);
        portada.setPreserveRatio(true);

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
        }

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

        juegoBox.getChildren().addAll(fondoBlur, portada, rectanguloInfo, infoBox);

        // Agregar evento de clic al StackPane
        juegoBox.setOnMouseClicked(event -> {
            try {
                // Cargar la pantalla de detalles del juego
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/Resena.fxml"));
                Parent root = loader.load();

                // Obtener el controlador de la pantalla de detalles del juego
                resena juegoControlador = loader.getController();
                juegoControlador.setJuego(juego); // Pasar la información del juego

                // Cambiar la escena actual
                Stage stage = (Stage) juegoBox.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return juegoBox;
    }
    
    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            System.out.println("ERROR: Usuario es null en DeseadosControlador.");
            return;}
        this.usuario = usuario;        
    }

}
