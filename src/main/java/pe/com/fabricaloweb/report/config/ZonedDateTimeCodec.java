package pe.com.fabricaloweb.report.config;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Clase que implementa la codificación y decodificación en base de datos para campos {@link ZonedDateTime}.
 *
 * @author jose.juarez
 * @version 1.0.0
 * @since 1.0.0
 */
public class ZonedDateTimeCodec implements Codec<ZonedDateTime> {

  @Override
  public ZonedDateTime decode(BsonReader reader, DecoderContext decoderContext) {
    return Instant.ofEpochMilli(reader.readDateTime()).atZone(ZoneId.systemDefault());
  }

  @Override
  public void encode(BsonWriter writer, ZonedDateTime value, EncoderContext encoderContext) {
    writer.writeDateTime(value.toInstant().toEpochMilli());
  }

  @Override
  public Class<ZonedDateTime> getEncoderClass() {
    return ZonedDateTime.class;
  }

}
