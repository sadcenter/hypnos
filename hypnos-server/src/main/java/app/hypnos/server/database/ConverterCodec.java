package app.hypnos.server.database;

import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class ConverterCodec<T> implements Codec<T> {

    private final Codec<Document> documentCodec;
    private final Converter<T> converter;
    private final Class<T> type;

    public ConverterCodec(Converter<T> converter, Class<T> type) {
        this.converter = converter;
        this.type = type;
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public T decode(BsonReader reader, DecoderContext context) {
        return converter.parse(documentCodec.decode(reader, context));
    }

    @Override
    public void encode(BsonWriter writer, T object, EncoderContext context) {
        documentCodec.encode(writer, converter.parse(object), context);
    }

    @Override
    public Class<T> getEncoderClass() {
        return type;
    }

}
