package pe.com.fabricaloweb.report.business.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import pe.com.fabricaloweb.report.business.ReportService;
import pe.com.fabricaloweb.report.exception.WebServiceException;
import pe.com.fabricaloweb.report.expose.bean.GenerateReportRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * Clase que implementa los métodos de {@link ReportService}.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ApplicationScoped
public class ReportServiceImpl implements ReportService {

  /**
   * Sintaxis de ruta de archivo de reportes.
   */
  private static final String TEMPLATE_FILE_SYNTAX = "/reports/%s/%s.jasper";

  /**
   * Clave para {@code Content-Disposition}.
   */
  private static final String CONTENT_DISPOSITION_KEY = "Content-Disposition";

  /**
   * Valor para {@code Content-Disposition}.
   */
  private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=%s";

  @Override
  public Response generateReport(GenerateReportRequest request) {
    String template = TEMPLATE_FILE_SYNTAX.formatted(request.projectName(), request.templateName());
    log.debug("Template to look for: {}", template);
    try (InputStream jasperStream = this.getClass().getResourceAsStream(template)) {
      Optional<InputStream> stream = Optional.ofNullable(jasperStream);
      if (stream.isPresent()) {
        JRDataSource dataSource = new JREmptyDataSource();
        JasperPrint print = JasperFillManager.fillReport(stream.get(), request.data(), dataSource);
        byte[] pdf = JasperExportManager.exportReportToPdf(print);
        log.info("Datasource: {}", dataSource);
        log.info("Print: {}", print);
        log.info("PDF length: {}b ", pdf.length);
        return Response.ok(pdf)
            .type(MediaType.APPLICATION_OCTET_STREAM)
            .header(CONTENT_DISPOSITION_KEY, CONTENT_DISPOSITION_VALUE.formatted(request.filename()))
            .build();
      } else {
        new FileNotFoundException(request.templateName() + " no encontrado.");
      }
    } catch (IOException e) {
      throw new WebServiceException("Hubo un error al leer el archivo del reporte.", e);
    } catch (JRException e) {
      throw new WebServiceException("Hubo un error al generar el reporte.", e);
    }
    return Response.noContent().build();
  }

}
