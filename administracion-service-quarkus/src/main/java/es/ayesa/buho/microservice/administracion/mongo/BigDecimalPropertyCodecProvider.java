package es.ayesa.buho.microservice.administracion.mongo;

import java.math.BigDecimal;

import org.bson.codecs.Codec;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

import jakarta.inject.Singleton;

@Singleton
public class BigDecimalPropertyCodecProvider implements PropertyCodecProvider {

    private static final FlexibleBigDecimalCodec CODEC = new FlexibleBigDecimalCodec();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry propertyCodecRegistry) {
        if (type == null) {
            return null;
        }
        if (BigDecimal.class.equals(type.getType())) {
            return (Codec<T>) CODEC;
        }
        return null;
    }
}
