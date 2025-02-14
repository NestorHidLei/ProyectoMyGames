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

				// Información básica
				String nombre = juego.has("name") && !juego.get("name").isJsonNull() ? juego.get("name").getAsString()
						: "Nombre no disponible";
				String portada = juego.has("background_image") && !juego.get("background_image").isJsonNull()
						? juego.get("background_image").getAsString()
						: "No disponible";
				String rating = juego.has("rating") && !juego.get("rating").isJsonNull()
						? String.valueOf(juego.get("rating").getAsDouble())
						: "0.0";
				String fechaLanzamiento = juego.has("released") && !juego.get("released").isJsonNull()
						? juego.get("released").getAsString()
						: "Fecha no disponible";

				// Descripción
				String descripcion = juego.has("description") && !juego.get("description").isJsonNull()
						? juego.get("description").getAsString()
						: "Descripción no disponible";

				// Plataformas
				JsonArray plataformasArray = juego.has("platforms") && !juego.get("platforms").isJsonNull()
						? juego.getAsJsonArray("platforms")
						: new JsonArray();
				StringBuilder plataformasBuilder = new StringBuilder();
				for (int i = 0; i < plataformasArray.size(); i++) {
					JsonObject plataforma = plataformasArray.get(i).getAsJsonObject();
					String nombrePlataforma = plataforma.getAsJsonObject("platform").has("name")
							? plataforma.getAsJsonObject("platform").get("name").getAsString()
							: null;
					if (nombrePlataforma != null) {
						plataformasBuilder.append(nombrePlataforma).append(", ");
					}
				}
				String plataformas = plataformasBuilder.length() > 0
						? plataformasBuilder.substring(0, plataformasBuilder.length() - 2)
						: "Plataformas no disponibles";

				// Géneros
				JsonArray generosArray = juego.has("genres") && !juego.get("genres").isJsonNull()
						? juego.getAsJsonArray("genres")
						: new JsonArray();
				StringBuilder generosBuilder = new StringBuilder();
				for (int i = 0; i < generosArray.size(); i++) {
					JsonObject genero = generosArray.get(i).getAsJsonObject();
					String nombreGenero = genero.has("name") ? genero.get("name").getAsString() : null;
					if (nombreGenero != null) {
						generosBuilder.append(nombreGenero).append(", ");
					}
				}
				String generos = generosBuilder.length() > 0 ? generosBuilder.substring(0, generosBuilder.length() - 2)
						: "Géneros no disponibles";

				// Metacritic score
				String metacriticScore = juego.has("metacritic") && !juego.get("metacritic").isJsonNull()
						? String.valueOf(juego.get("metacritic").getAsInt())
						: "No disponible";

				// Requisitos mínimos y recomendados (solo para PC)
				String requisitosMinimos = "No disponible";
				String requisitosRecomendados = "No disponible";
				for (int i = 0; i < plataformasArray.size(); i++) {
					JsonObject plataforma = plataformasArray.get(i).getAsJsonObject();
					if (plataforma.getAsJsonObject("platform").get("name").getAsString().equals("PC")) {
						JsonObject requisitos = plataforma.has("requirements_en")
								? plataforma.getAsJsonObject("requirements_en")
								: null;
						if (requisitos != null) {
							requisitosMinimos = requisitos.has("minimum") ? requisitos.get("minimum").getAsString()
									: "No disponible";
							requisitosRecomendados = requisitos.has("recommended")
									? requisitos.get("recommended").getAsString()
									: "No disponible";
						}
						break;
					}
				}

				// Screenshots
				JsonArray screenshotsArray = juego.has("short_screenshots")
						&& !juego.get("short_screenshots").isJsonNull() ? juego.getAsJsonArray("short_screenshots")
								: new JsonArray();
				StringBuilder screenshotsBuilder = new StringBuilder();
				for (int i = 0; i < screenshotsArray.size(); i++) {
					JsonObject screenshot = screenshotsArray.get(i).getAsJsonObject();
					String screenshotUrl = screenshot.has("image") ? screenshot.get("image").getAsString() : null;
					if (screenshotUrl != null) {
						screenshotsBuilder.append(screenshotUrl).append(",");
					}
				}
				String screenshots = screenshotsBuilder.length() > 0
						? screenshotsBuilder.substring(0, screenshotsBuilder.length() - 1)
						: "";

				// Devolver un array con toda la información
				return new String[] { id, // 0: ID
						nombre, // 1: Nombre
						portada, // 2: Imagen de fondo
						rating, // 3: Rating
						fechaLanzamiento, // 4: Fecha de lanzamiento
						plataformas, // 5: Plataformas
						generos, // 6: Géneros
						metacriticScore, // 7: Metacritic score
						descripcion, // 8: Descripción
						requisitosMinimos, // 9: Requisitos mínimos
						requisitosRecomendados, // 10: Requisitos recomendados
						screenshots // 11: Screenshots
				};
			} else {
				System.err.println("Error en la solicitud: " + respuesta.code());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Busca juegos por nombre.
	 * 
	 * @param nombre Nombre del juego a buscar.
	 * @return Lista de información de juegos (id, nombre, portada, rating).
	 */
	public List<String[]> buscarJuegosPorNombre(String nombre) {
		String url = URL_API + "?key=" + CLAVE_API + "&search=" + nombre;
		return obtenerListaDeJuegos(url);
	}

	public List<String[]> buscarJuegosPorGenero(String nombre, String genero) {
		String generoID = switch (genero) {
		case "acción" -> "4";
		case "aventura" -> "3";
		case "plataformas" -> "83";
		case "rpg" -> "5";
		case "shooter" -> "2";
		case "estrategia" -> "10";
		case "simulación" -> "14";
		case "puzle" -> "7";
		case "arcade" -> "11";
		case "carreras" -> "1";
		case "deportes" -> "15";
		case "lucha" -> "6";
		case "familia" -> "19";
		case "tablero" -> "28";
		case "educación" -> "34";
		case "tarjeta" -> "17";
		case "indie" -> "51";
		case "música" -> "12";
		case "casual" -> "40";
		default -> "";
		};
		String url = URL_API + "?key=" + CLAVE_API + "&search=" + nombre + "&genres=" + generoID;
		System.out.println(url);
		return obtenerListaDeJuegos(url);
	}

	public List<String[]> buscarJuegosPorPlataforma(String nombre, String plataforma) {
		String plataformasID = switch (plataforma) {
		case "PC" -> "4,5,6"; // PC, macOS, Linux
		case "Xbox" -> "1,14,186"; // Xbox One, Xbox 360, Xbox Series S/X
		case "PlayStation" -> "187,18,16,17,19"; // PS5, PS4, PS3, PSP, PS Vita
		case "Nintendo" -> "7,8,9,10,11,13"; // Switch, 3DS, DS, Wii U, Wii, DSi
		case "Atari" -> "23,24,25,26,27,28,29,30,31"; // Various Atari consoles
		case "Sega" -> "52,77,106,107,117,119,167"; // Sega Master System, Game Gear, Dreamcast, Saturn, 32X, Sega CD,
													// Genesis
		case "3DO" -> "111"; // 3DO console
		case "Neo Geo" -> "12"; // Neo Geo
		case "Web" -> "171"; // Web-based platforms
		default -> "";
		};
		String url = URL_API + "?key=" + CLAVE_API + "&search=" + nombre + "&platforms=" + plataformasID;
		System.out.println(url);

		return obtenerListaDeJuegos(url);
	}

	public List<String[]> buscarJuegosPorGeneroYPlataforma(String nombre, String genero, String plataforma) {
		String generoID = switch (genero) {
		case "acción" -> "4";
		case "aventura" -> "3";
		case "plataformas" -> "83";
		case "rpg" -> "5";
		case "shooter" -> "2";
		case "estrategia" -> "10";
		case "simulación" -> "14";
		case "puzle" -> "7";
		case "arcade" -> "11";
		case "carreras" -> "1";
		case "deportes" -> "15";
		case "lucha" -> "6";
		case "familia" -> "19";
		case "tablero" -> "28";
		case "educación" -> "34";
		case "tarjeta" -> "17";
		case "indie" -> "51";
		case "música" -> "12";
		case "casual" -> "40";
		default -> "";
		};
		String plataformasID = switch (plataforma) {
		case "PC" -> "4,5,6"; // PC, macOS, Linux
		case "Xbox" -> "1,14,186"; // Xbox One, Xbox 360, Xbox Series S/X
		case "PlayStation" -> "187,18,16,17,19"; // PS5, PS4, PS3, PSP, PS Vita
		case "Nintendo" -> "7,8,9,10,11,13"; // Switch, 3DS, DS, Wii U, Wii, DSi
		case "Atari" -> "23,24,25,26,27,28,29,30,31"; // Various Atari consoles
		case "Sega" -> "52,77,106,107,117,119,167"; // Sega Master System, Game Gear, Dreamcast, Saturn, 32X, Sega CD,
													// Genesis
		case "3DO" -> "111"; // 3DO console
		case "Neo Geo" -> "12"; // Neo Geo
		case "Web" -> "171"; // Web-based platforms
		default -> "";
		};
		String url = URL_API + "?key=" + CLAVE_API + "&search=" + nombre + "&platforms=" + plataformasID + "&genres="
				+ generoID;
		System.out.println(url);

		return obtenerListaDeJuegos(url);
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

				// Limitar a 15 juegos
				int limite = Math.min(resultados.size(), 15); // Tomar máximo 15 juegos
				for (int i = 0; i < limite; i++) {
					JsonObject juego = resultados.get(i).getAsJsonObject();

					String id = juego.has("id") && !juego.get("id").isJsonNull() ? juego.get("id").getAsString()
							: "ID no disponible";
					String nombre = juego.has("name") && !juego.get("name").isJsonNull()
							? juego.get("name").getAsString()
							: "Nombre no disponible";
					String portada = juego.has("background_image") && !juego.get("background_image").isJsonNull()
							? juego.get("background_image").getAsString()
							: "No disponible";
					String rating = juego.has("rating") && !juego.get("rating").isJsonNull()
							? String.valueOf(juego.get("rating").getAsDouble())
							: "0.0";

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