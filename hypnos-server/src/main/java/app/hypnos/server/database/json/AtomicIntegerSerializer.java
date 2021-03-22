package app.hypnos.server.database.json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicIntegerSerializer implements JsonDeserializer<AtomicInteger>, JsonSerializer<AtomicInteger> {
    @Override
    public AtomicInteger deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new AtomicInteger(jsonElement.getAsInt());
    }

    @Override
    public JsonElement serialize(AtomicInteger atomicInteger, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(atomicInteger.get());
    }
}
