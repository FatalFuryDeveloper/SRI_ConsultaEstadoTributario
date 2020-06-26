package com.logicstudio.sri.logic.sri.resources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class SriConsultas {
	/** Inicializacion Logger */
	private static final Logger logger = LogManager.getLogger(SriConsultas.class);
	/** URL del Servicio Rest de Consulta de Identificacion del SRI */
	private static final String URL_SERVICIO_CONSULTA_IDENTIFICACION = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/Persona/obtenerPorTipoIdentificacion?numeroIdentificacion=";
	/** URL del Servicio Rest de Consulta de Permiso Facturacion del SRI */
	private static final String URL_SERVICIO_PERMISO_FACTURACION = "https://srienlinea.sri.gob.ec/sri-estado-tributario-internet/rest/permiso-facturacion/consulta/persona/";
	/** URL del Servicio Rest de Consulta de Estado Tributario del SRI */
	private static final String URL_SERVICIO_ESTADO_TRIBUTARIO = "https://srienlinea.sri.gob.ec/sri-estado-tributario-internet/rest/estado-tributario/consulta/persona/";
	/** Parametro del Servicio Rest de Consulta de Identificacion del SRI */
	private static final String PARAM_TIPO = "&tipoIdentificacion=R";
	/** Tipo de Ejecucion de Rest, Method GET en Ejecucion de Servicio Rest */
	private static final String REQUEST_METHOD_GET = "GET";
	/** Key Accept en Ejecucion de Servicio Rest */
	private static final String REQUEST_ACCEPT_KEY = "Accept";
	/** Value Accept en Ejecucion de Servicio Rest */
	private static final String REQUEST_ACCEPT_VALUE = "application/json";
	/** Key Authorization en Ejecucion de Servicio Rest */
	private static final String REQUEST_AUTENTICATION_KEY = "Authorization";
	/** Directorio del archivo generado de las consultas */
	private static final String PATH_FILE_GENERATOR = "init/consultasEstadoTributario.json";
	/** Directorio del archivo insumo de los Rucs */
	private static final String PATH_FILE_INIT_RUC = "init/rucs.txt";
	/** Directorio del archivo insumo de authorizacion */
	private static final String PATH_FILE_INIT_AUTHORIZATION = "init/authorization.txt";
	/** Contador del Total de Ejecuciones realizadas */
	private static int count = 0;
	/** Entrada de texto por consola para gestion de authorizacion */
	static Scanner scanner = new Scanner(System.in);
	/** Entrada de texto por consola para gestion de authorizacion */
	private static String authorization;

	public static void main(String[] args) {
		String data = "";
		List<String> list = loadFileRuc(PATH_FILE_INIT_RUC);
		reloadAuthorization();
		long start = System.currentTimeMillis();
		String datosIdentificacion = "";
		RucDTO datosIdentificacionJson = null;
		Gson gson = new Gson();
		for (String ruc : list) {
			datosIdentificacion = queryRuc(ruc);
			datosIdentificacionJson = gson.fromJson(datosIdentificacion, RucDTO.class);
			data = String.format("{\"ruc\":%s, \"facturacion\":%s, \"estadoTributario\":%s},%n", datosIdentificacion,
					queryPermisoFacturacion(datosIdentificacionJson.getCodigoPersona()),
					queryEstadoTributrario(datosIdentificacionJson.getCodigoPersona()));
			writeFile(data, PATH_FILE_GENERATOR);
			count++;
		}
		long end = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Time Execution: %s ms., Services Execute: %s", (end - start), count));
		}

	}

	/**
	 * 
	 * @author mrivera
	 * @param ruc - Ruc del contribuyente
	 * @return void
	 * @see Construye url del servicio rest Obtener Por Tipo Identificacion del SRI
	 */
	public static String buildUrlServiceRuc(String ruc) {
		return URL_SERVICIO_CONSULTA_IDENTIFICACION + ruc + PARAM_TIPO;
	}

	/**
	 * 
	 * @author mrivera
	 * @param codigo - Codigo de Empresa o Persona
	 * @return String - Url del servicio rest
	 * @see Construye url del servicio rest Permiso Facturacion del SRI
	 */
	public static String buildUrlServicePermisoFacturacion(int codigo) {
		return URL_SERVICIO_PERMISO_FACTURACION + codigo;
	}

	/**
	 * 
	 * @author mrivera
	 * @param codigo - Codigo de Empresa o Persona
	 * @return String - Url del servicio rest
	 * @see Construye url del servicio rest Estado Tributario del SRI
	 */
	public static String buildUrlServiceEstadoTributario(int codigo) {
		return URL_SERVICIO_ESTADO_TRIBUTARIO + codigo;
	}

	/**
	 * 
	 * @author mrivera
	 * @return void
	 * @see Recarga Authorization desde archivo authorization
	 */
	public static void reloadAuthorization() {
		authorization = loadFileAuthorization(PATH_FILE_INIT_AUTHORIZATION);
	}

	/**
	 * 
	 * @author mrivera
	 * @return void
	 * @see consulta servicio RUC del SRI
	 */
	public static String queryRuc(String ruc) {
		return executionHttp(buildUrlServiceRuc(ruc), null);
	}

	/**
	 * 
	 * @author mrivera
	 * @param authorization - Parametro authorizacion del servicio rest
	 * @return void
	 * @see consulta servicio Permiso Facturacion del SRI
	 */
	public static String queryPermisoFacturacion(int codigo) {
		Map<String, String> listParams = new HashMap<>();
		listParams.put(REQUEST_AUTENTICATION_KEY, authorization);
		return executionHttp(buildUrlServicePermisoFacturacion(codigo), listParams);
	}

	/**
	 * 
	 * @author mrivera
	 * @param authorization - Parametro authorizacion del servicio rest
	 * @return void
	 * @see consulta servicio Estado Tributario del SRI
	 */
	public static String queryEstadoTributrario(int codigo) {
		Map<String, String> listParams = new HashMap<>();
		listParams.put(REQUEST_AUTENTICATION_KEY, authorization);
		return executionHttp(buildUrlServiceEstadoTributario(codigo), listParams);
	}

	/**
	 * 
	 * @author mrivera
	 * @param urlService - Url del servicio rest a ejecutar
	 * @param params     - Parametros del servicio rest a ejecutar
	 * @return String - Datos de la ejecucion del servicio rest
	 * @see Ejecucion de servicio rest
	 */
	public static String executionHttp(String urlService, Map<String, String> params) {
		StringBuilder data = new StringBuilder();
		try {
			URL url = new URL(urlService);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(REQUEST_METHOD_GET);
			conn.setRequestProperty(REQUEST_ACCEPT_KEY, REQUEST_ACCEPT_VALUE);

			/** Si hay parametros se setean en conexion http */
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			/**
			 * Si el codigo de respuesta es diferente a 200 (OK) se genera un error, caso
			 * contrario se escribe en archivo resultado de conexion
			 */
			if (conn.getResponseCode() != 200) {
				String entrada = "";
				logger.info(
						"Authorizacion expiro, renueve manualmente el archivo /authorization.txt, Presione la tecla Enter para continuar...");
				do {
					entrada = scanner.nextLine();
					logger.info(entrada);
				} while (!entrada.equals(""));
				reloadAuthorization();
				if (params != null) {
					params.clear();
					params.put(REQUEST_AUTENTICATION_KEY, authorization);
				}
				executionHttp(urlService, params);
			} else {
				InputStreamReader in = new InputStreamReader(conn.getInputStream());
				data.append(buildResponse(in));
				conn.disconnect();
			}
		} catch (Exception e) {
			logger.error(String.format("Error en Rest: %s ", e));
		}
		return data.toString();
	}

	/**
	 * 
	 * @author mrivera
	 * @param input - Entrada de stream
	 * @return String - Texto completo de consulta rest
	 * @see Lee consulta rest
	 */
	public static String buildResponse(InputStreamReader input) {
		StringBuilder text = new StringBuilder();
		BufferedReader br = new BufferedReader(input);
		String output;
		try {
			while ((output = br.readLine()) != null) {
				text.append(output);
			}
		} catch (Exception e) {
			logger.error(String.format("Error en lectura input: %s ", e));
		}
		return text.toString();
	}

	/**
	 * 
	 * @author mrivera
	 * @param filename - Directorio de archivo a cargar
	 * @return list - Lista de Rucs
	 * @see Lee archivo rucs y devuelve en lista los rucs
	 */
	public static List<String> loadFileRuc(String filename) {
		ArrayList<String> list = new ArrayList<>();
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
		} catch (Exception e) {
			logger.error("Error Load File Ruc: ", e);
		}
		return list;
	}

	/**
	 * 
	 * @author mrivera
	 * @param filename - Directorio de archivo a cargar
	 * @return list - Lista de Rucs
	 * @see Lee archivo rucs y devuelve en lista los rucs
	 */
	public static String loadFileAuthorization(String filename) {
		StringBuilder text = new StringBuilder();
		String line;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			while ((line = br.readLine()) != null) {
				text.append(line);
			}
		} catch (Exception e) {
			logger.error("Error Load File Authorization: ", e);
		}
		return text.toString();
	}

	/**
	 * @author mrivera
	 * @param text     - Texto a escribir en archivo
	 * @param filePath - Directorio a generar archivo
	 * @see Genera archivo en formato JSON
	 */
	public static void writeFile(String text, String filePath) {
		try (FileWriter fileWriter = new FileWriter(filePath, true)) {
			fileWriter.write(text);
		} catch (Exception e) {
			logger.error(String.format("Error Created File: %s ", e));
		}
	}
}
