package pe.com.fabricaloweb.report.config;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.ZonedDateTime;

/**
 * Proveedor de códec para {@link ZonedDateTime}.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
public class ZonedDateTimeCodecProvider implements CodecProvider {

  @Override
  public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
    if (clazz.equals(ZonedDateTime.class)) {
      return (Codec<T>) new ZonedDateTimeCodec();
    }
    return null;
  }

}
