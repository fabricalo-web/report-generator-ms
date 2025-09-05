package pe.com.fabricaloweb.report.expose;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.com.fabricaloweb.report.business.ReportService;
import pe.com.fabricaloweb.report.expose.bean.GenerateReportRequest;

@Slf4j
@Path("/report")
@AllArgsConstructor
@ApplicationScoped
public class ReportController {

  @Inject
  private ReportService service;

  @POST
  @Path("/generate")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response generateReport(GenerateReportRequest request) {
    long time = System.currentTimeMillis();
    log.info("Se solicita generar el archivo {}", request.filename());
    log.trace("Datos: {}", request.data());
    Response response = this.service.generateReport(request);
    log.info("Generación de archivo {} exitosa. Tiempo transcurrido: {}ms",
        request.filename(), System.currentTimeMillis() - time);
    return response;
  }

}
