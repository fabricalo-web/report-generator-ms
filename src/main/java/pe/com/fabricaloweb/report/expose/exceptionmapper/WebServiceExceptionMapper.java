package pe.com.fabricaloweb.report.expose.exceptionmapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import pe.com.fabricaloweb.report.exception.WebServiceException;

import static jakarta.ws.rs.core.Response.Status;

/**
 * Interceptor de excepción {@link WebServiceException}.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Provider
public class WebServiceExceptionMapper implements ExceptionMapper<WebServiceException> {

  @Override
  public Response toResponse(WebServiceException e) {
    log.error("Excepción generada", e);
    return Response.status(Status.INTERNAL_SERVER_ERROR)
        .entity("Hubo un error al generar archivo")
        .build();
  }

}
