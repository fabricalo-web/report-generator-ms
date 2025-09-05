package pe.com.fabricaloweb.report.expose.bean;

import java.util.Map;

/**
 * Estructura de entrada para solicitar una generación de reporte.
 *
 * @param projectName  Nombre de proyecto.
 * @param templateName Nombre de plantilla.
 * @param data         Datos a inyectar en plantilla.
 * @param filename     Nombre de archivo a generar.
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
public record GenerateReportRequest(
    String projectName,
    String templateName,
    Map<String, Object> data,
    String filename
) {
}
