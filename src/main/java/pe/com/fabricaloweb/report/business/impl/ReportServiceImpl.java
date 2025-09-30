package pe.com.fabricaloweb.report.business.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import pe.com.fabricaloweb.report.business.ReportService;
import pe.com.fabricaloweb.report.exception.WebServiceException;
import pe.com.fabricaloweb.report.expose.bean.FileType;
import pe.com.fabricaloweb.report.expose.bean.GenerateReportRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
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
   * Clave para {@code Content-Length}.
   */
  private static final String CONTENT_LENGTH_KEY = "Content-Length";

  /**
   * Valor para {@code Content-Disposition}.
   */
  private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=%s";

  /**
   * Clave {@code REPORT_CURRENCY}.
   */
  private static final String REPORT_CURRENCY_KEY = "REPORT_CURRENCY";

  @Override
  public Response generateReport(GenerateReportRequest request) {
    String template = TEMPLATE_FILE_SYNTAX.formatted(request.projectName(), request.templateName());
    log.debug("Template to look for: {}", template);
    try (InputStream jasperStream = this.getClass().getResourceAsStream(template)) {
      Optional<InputStream> stream = Optional.ofNullable(jasperStream);
      if (stream.isPresent()) {
        JRDataSource dataSource = new JREmptyDataSource();
        this.parseData(request.data());
        JasperPrint print = JasperFillManager.fillReport(stream.get(), request.data(), dataSource);
        return this.generateFile(print, request.filename(), request.type());
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

  private void parseData(Map<String, Object> data) {
    Optional<Map<String, Object>> dataOpt = Optional.of(data);
    // para REPORT_LOCALE
    dataOpt.map(map -> map.get(JRParameter.REPORT_LOCALE))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .ifPresent(locale -> {
          Locale.Builder builder = new Locale.Builder();
          String[] parts = locale.split("[-_]");
          if (parts.length == 1) {
            builder.setLanguage(parts[0]);
          } else if (parts.length >= 2) {
            builder.setLanguage(parts[0]);
            builder.setRegion(parts[1]);
          }
          Locale newLocale = builder.build();
          data.put(JRParameter.REPORT_LOCALE, newLocale);
          data.put("EXCEL_LOCALE", newLocale);
        });
    // para REPORT_CURRENCY
    dataOpt.map(map -> map.get(REPORT_CURRENCY_KEY))
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .ifPresent(currency -> data.put(REPORT_CURRENCY_KEY, Currency.getInstance(currency)));
  }


  private Response generateFile(JasperPrint print, String filename, FileType fileType) throws JRException {
    byte[] file = switch (fileType) {
      case PDF -> JasperExportManager.exportReportToPdf(print);
      case XLSX -> {
        ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();
        JRXlsxExporter exporter = getJrXlsxExporter(print, xlsReport);
        exporter.exportReport();
        yield xlsReport.toByteArray();
      }
    };
    return Response.ok(file)
        .type(MediaType.APPLICATION_OCTET_STREAM)
        .header(CONTENT_DISPOSITION_KEY, CONTENT_DISPOSITION_VALUE.formatted(filename))
        .header(CONTENT_LENGTH_KEY, file.length)
        .build();
  }

  private static JRXlsxExporter getJrXlsxExporter(JasperPrint print, ByteArrayOutputStream xlsReport) {
    JRXlsxExporter exporter = new JRXlsxExporter();
    exporter.setExporterInput(new SimpleExporterInput(print));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(xlsReport));
    SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
    configuration.setOnePagePerSheet(false);
    configuration.setRemoveEmptySpaceBetweenRows(true);
    configuration.setRemoveEmptySpaceBetweenColumns(true);
    configuration.setWhitePageBackground(false);
    configuration.setDetectCellType(true);
    exporter.setConfiguration(configuration);
    return exporter;
  }

}
