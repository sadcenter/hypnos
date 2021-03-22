package app.hypnos.server.database.impl;

import app.hypnos.server.Server;
import app.hypnos.server.data.User;
import app.hypnos.server.database.Converter;
import app.hypnos.server.database.ConverterCodec;
import org.bson.Document;

public final class UserConverterCodec extends ConverterCodec<User> {

    public UserConverterCodec() {
        super(new Converter<>() {
            @Override
            public User parse(Document document) {
                return Server.GSON.fromJson(document.toJson(), User.class);
            }

            @Override
            public Document parse(User user) {
                return Document.parse(Server.GSON.toJson(user));
            }
        }, User.class);
    }
}
