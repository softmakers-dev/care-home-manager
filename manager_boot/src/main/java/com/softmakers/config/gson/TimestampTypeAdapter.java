package com.softmakers.config.gson;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// Custom TypeAdapter for java.sql.Timestamp
public class TimestampTypeAdapter implements JsonSerializer<Timestamp>, JsonDeserializer<Timestamp> {
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"); // ISO 8601 with millis and zone

    @Override
    public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            Date date = format.parse(json.getAsString());
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(format.format(new Date(src.getTime())));
    }
}

