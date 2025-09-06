package pe.com.fabricaloweb.report.config;

import com.mongodb.MongoClientSettings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

/**
 * Clase que configura los codecs declarados para persistir tipos de datos que MongoDB no soporta de manera nativa.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
@ApplicationScoped
public class CodecConfig {

  /**
   * Proporciona una lista de códecs para los siguientes tipos de datos:
   * <ul>
   *   <li>{@link java.time.ZonedDateTime}</li>
   *   <li>{@link java.math.BigDecimal}</li>
   * </ul>
   *
   * @return Una lista de códecs personalizados.
   */
  @Produces
  @Singleton
  public CodecProvider codecProvider() {
    return CodecRegistries.fromProviders(
        new ZonedDateTimeCodecProvider()
    );
  }

  /**
   * Une los proveedores de códecs por defecto y los personalizados en un solo conjunto.
   *
   * @return Un registro completo de códecs.
   */
  public CodecRegistry codecRegistry() {
    return CodecRegistries.fromRegistries(
        CodecRegistries.fromProviders(this.codecProvider()),
        MongoClientSettings.getDefaultCodecRegistry()
    );
  }

}
