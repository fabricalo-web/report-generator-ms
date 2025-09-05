package pe.com.fabricaloweb.report.business;

import jakarta.ws.rs.core.Response;
import pe.com.fabricaloweb.report.expose.bean.GenerateReportRequest;

/**
 * Servicio que expone los métodos de generación de reportes.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ReportService {

  /**
   * Genera un reporte a partir de los datos enviados.
   *
   * @param request Datos para generar reportes.
   * @return Archivo final de reporte.
   */
  Response generateReport(GenerateReportRequest request);

}
