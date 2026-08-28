package civgame.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class JsonSerializer {
    
    private final Gson gson;
    
    public JsonSerializer() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    public String serialize(Object object) throws NetworkException {
        if (object == null) {
            throw new NetworkException("Cannot serialize null object");
        }
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            throw new NetworkException("Failed to serialize object: " + e.getMessage(), e);
        }
    }
    
    public <T> T deserialize(String json, Class<T> clazz) throws NetworkException {
        if (json == null || json.isEmpty()) {
            throw new NetworkException("Cannot deserialize null or empty JSON");
        }
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new NetworkException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }
    
    public <T> T deserialize(String json, java.lang.reflect.Type type) throws NetworkException {
        if (json == null || json.isEmpty()) {
            throw new NetworkException("Cannot deserialize null or empty JSON");
        }
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            throw new NetworkException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }
}