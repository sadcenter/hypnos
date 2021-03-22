package app.hypnos.server.database;

import org.bson.Document;

public interface Converter<T> {

    T parse(Document document);

    Document parse(T t);
}
