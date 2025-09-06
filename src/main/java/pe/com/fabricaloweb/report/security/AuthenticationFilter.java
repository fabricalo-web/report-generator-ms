package pe.com.fabricaloweb.report.security;

import com.mongodb.client.model.Filters;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.mindrot.jbcrypt.BCrypt;
import pe.com.fabricaloweb.report.document.Client;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static jakarta.ws.rs.core.Response.Status;

/**
 * Clase que intercepta las solicitudes para una revisión previa de credenciales.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  /**
   * Clave de cabecera que recibe el ID del cliente.
   */
  private static final String CLIENT_ID_HEADER = "X-Client-Id";

  /**
   * Clave de cabecera que recibe la clave API.
   */
  private static final String API_KEY_HEADER = "X-Api-Key";

  @Override
  public void filter(ContainerRequestContext context) throws IOException {
    String clientId = context.getHeaderString(CLIENT_ID_HEADER);
    String apiKey = context.getHeaderString(API_KEY_HEADER);
    boolean validApiKey;
    Optional<Client> clientOpt = this.findClient(clientId);
    if (clientOpt.isPresent()) {
      Client client = clientOpt.get();
      if (client.isActive()) {
        log.debug("Checking API Key");
        validApiKey = BCrypt.checkpw(apiKey, client.getHashedApiKey());
        if (validApiKey) {
          log.debug("API Key valid!");
          int requestCount = client.getRequestCount();
          client.setLastUsed(ZonedDateTime.now());
          client.setRequestCount(++requestCount);
          client.persistOrUpdate();
        } else {
          context.abortWith(Response.status(Status.FORBIDDEN)
              .entity("Credenciales incorrectas")
              .type(MediaType.APPLICATION_JSON)
              .build());
        }
      } else {
        context.abortWith(Response.status(Status.FORBIDDEN)
            .entity("El cliente no se encuentra activo")
            .type(MediaType.APPLICATION_JSON)
            .build());
      }
    } else {
      context.abortWith(Response.status(Status.UNAUTHORIZED)
          .entity("Cliente no existente")
          .type(MediaType.APPLICATION_JSON)
          .build());
    }
  }

  private Optional<Client> findClient(String clientId) {
    Bson query = Filters.eq("clientId", clientId);
    log.debug("Looking for client with id {}", clientId);
    return Client.mongoCollection()
        .find(query, Client.class)
        .into(new ArrayList<>())
        .stream().findFirst();
  }

}
