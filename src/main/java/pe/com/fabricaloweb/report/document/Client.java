package pe.com.fabricaloweb.report.document;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "client")
public class Client extends PanacheMongoEntity {

  /**
   * Identificador de cliente.
   */
  private String clientId;

  /**
   * Nombre de cliente.
   */
  private String clientName;

  /**
   * API Key cifrada.
   */
  private String hashedApiKey;

  /**
   * Indicador de usuario activo.
   */
  private boolean active;

  /**
   * Fecha de creación.
   */
  private ZonedDateTime createdAt;

  /**
   * Fecha de último uso.
   */
  private ZonedDateTime lastUsed;

  /**
   * Cantidad de solicitudes realizadas.
   */
  private int requestCount;

}
