package pe.com.fabricaloweb.report.expose.exceptionmapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.FileNotFoundException;

import static jakarta.ws.rs.core.Response.Status;

/**
 * Interceptor de excepción {@link FileNotFoundException}.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
@Provider
public class FileNotFoundExceptionMapper implements ExceptionMapper<FileNotFoundException> {

  @Override
  public Response toResponse(FileNotFoundException e) {
    return Response.status(Status.NOT_FOUND)
        .entity(e.getMessage())
        .build();
  }

}
