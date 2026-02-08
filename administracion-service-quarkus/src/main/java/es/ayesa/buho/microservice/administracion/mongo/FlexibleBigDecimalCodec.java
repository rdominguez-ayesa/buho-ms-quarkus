package es.ayesa.buho.microservice.administracion.mongo;

import java.math.BigDecimal;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

public final class FlexibleBigDecimalCodec implements Codec<BigDecimal> {

    @Override
    public BigDecimal decode(BsonReader reader, DecoderContext decoderContext) {
        BsonType type = reader.getCurrentBsonType();
        if (type == null) {
            type = reader.readBsonType();
        }

        return switch (type) {
            case NULL -> {
                reader.readNull();
                yield null;
            }
            case DECIMAL128 -> reader.readDecimal128().bigDecimalValue();
            case DOUBLE -> BigDecimal.valueOf(reader.readDouble());
            case INT32 -> BigDecimal.valueOf(reader.readInt32());
            case INT64 -> BigDecimal.valueOf(reader.readInt64());
            case STRING -> parse(reader.readString());
            default -> throw new BsonInvalidOperationException(
                    "No se puede decodificar BigDecimal desde BSONType=" + type);
        };
    }

    @Override
    public void encode(BsonWriter writer, BigDecimal value, EncoderContext encoderContext) {
        if (value == null) {
            writer.writeNull();
            return;
        }
        writer.writeDecimal128(new Decimal128(value));
    }

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }

    private static BigDecimal parse(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        // Soporta formatos simples "1,23" -> "1.23".
        if (trimmed.indexOf(',') >= 0 && trimmed.indexOf('.') < 0) {
            trimmed = trimmed.replace(',', '.');
        }
        return new BigDecimal(trimmed);
    }
}
