package conexiones;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Clase para la conexión y comunicación con la API de juegos RAWG.
 * 
 * Esta clase permite buscar información detallada de juegos por su ID, obtener
 * listas de juegos ordenados por diferentes criterios y filtrados por
 * etiquetas, utilizando peticiones HTTP.
 */
public class ConexionAPI {

	/**
	 * URL base de la API.
	 */
	private static final String URL_API = "https://api.rawg.io/api/games";

	/**
	 * Clave de acceso a la API.
	 */
	private static final String CLAVE_API = "77f5da4789314613a016bbbef469d1e7";

	/**
	 * Cliente HTTP para realizar las peticiones.
	 */
	private OkHttpClient cliente = new OkHttpClient();

	/**
	 * Busca un juego por su ID y devuelve sus detalles.
	 * 
	 * @param id ID del juego a buscar.
	 * @return Información del juego (id, nombre, portada, rating) o null si falla
	 *         la solicitud.
	 */
	public String[] buscarJuegoPorId(String id) {
		String url = URL_API + "/" + id + "?key=" + CLAVE_API;
		Request solicitud = new Request.Builder().url(url).build();

		try (Response respuesta = cliente.newCall(solicitud).execute()) {
			if (respuesta.isSuccessful()) {
				String datosJson = respuesta.body().string();
				JsonObject juego = JsonParser.parseString(datosJson).getAsJsonObject();

				String nombre = juego.get("name").getAsString();
				String portada = juego.has("background_image") ? juego.get("background_image").getAsString()
						: "No disponible";
				String rating = juego.has("rating") ? String.valueOf(juego.get("rating").getAsDouble()) : "0.0";

				return new String[] { id, nombre, portada, rating };
			} else {
				System.err.println("Error en la solicitud: " + respuesta.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Obtiene juegos ordenados por un criterio y devuelve sus detalles.
	 * 
	 * @param criterio Criterio de ordenación (por ejemplo, "rating", "released").
	 * @return Lista de información de juegos (id, nombre, portada, rating).
	 */
	private List<String[]> obtenerJuegosPorCriterio(String criterio) {
		String url = URL_API + "?key=" + CLAVE_API + "&ordering=" + criterio;
		return obtenerListaDeJuegos(url);
	}

	/**
	 * Obtiene juegos filtrados por etiqueta y devuelve sus detalles.
	 * 
	 * @param etiqueta Etiqueta del juego (por ejemplo, "multiplayer",
	 *                 "singleplayer").
	 * @return Lista de información de juegos (id, nombre, portada, rating).
	 */
	private List<String[]> obtenerJuegosPorEtiqueta(String etiqueta) {
		String url = URL_API + "?key=" + CLAVE_API + "&tags=" + etiqueta;
		return obtenerListaDeJuegos(url);
	}

	/**
	 * Realiza una solicitud HTTP para obtener juegos y devuelve una lista con sus
	 * detalles.
	 * 
	 * @param url URL de la API.
	 * @return Lista de información de juegos (id, nombre, portada, rating).
	 */
	private List<String[]> obtenerListaDeJuegos(String url) {
		List<String[]> juegos = new ArrayList<>();
		Request solicitud = new Request.Builder().url(url).build();

		try (Response respuesta = cliente.newCall(solicitud).execute()) {
			if (respuesta.isSuccessful()) {
				String datosJson = respuesta.body().string();
				JsonObject objetoJson = JsonParser.parseString(datosJson).getAsJsonObject();
				JsonArray resultados = objetoJson.getAsJsonArray("results");

				for (int i = 0; i < resultados.size(); i++) {
					JsonObject juego = resultados.get(i).getAsJsonObject();

					String id = juego.get("id").getAsString();
					String nombre = juego.get("name").getAsString();
					String portada = juego.has("background_image") ? juego.get("background_image").getAsString()
							: "No disponible";
					String rating = juego.has("rating") ? String.valueOf(juego.get("rating").getAsDouble()) : "0.0";

					juegos.add(new String[] { id, nombre, portada, rating });
				}
			} else {
				System.err.println("Error en la solicitud: " + respuesta.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return juegos;
	}

	/**
	 * Obtiene los juegos con los mejores ratings.
	 * 
	 * @return Lista de información de juegos con los mejores ratings.
	 */
	public List<String[]> obtenerMejoresRatings() {
		return obtenerJuegosPorCriterio("-rating");
	}

	/**
	 * Obtiene los juegos más nuevos.
	 * 
	 * @return Lista de información de juegos más nuevos.
	 */
	public List<String[]> obtenerJuegosMasNuevos() {
		return obtenerJuegosPorCriterio("released");
	}

	/**
	 * Obtiene los juegos multiplayer.
	 * 
	 * @return Lista de información de juegos multiplayer.
	 */
	public List<String[]> obtenerJuegosMultiplayer() {
		return obtenerJuegosPorEtiqueta("multiplayer");
	}

	/**
	 * Obtiene los juegos singleplayer.
	 * 
	 * @return Lista de información de juegos singleplayer.
	 */
	public List<String[]> obtenerJuegosSingleplayer() {
		return obtenerJuegosPorEtiqueta("singleplayer");
	}
}
