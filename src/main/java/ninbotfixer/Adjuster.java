package ninbotfixer;

import com.google.gson.JsonElement;

public class Adjuster {

    public String id;
    public JavaValue adjustment;

    public JavaValue defaultValue;
    public Double allowedError;

    public static class JavaValue {
        public String type;
        public JsonElement value;
    }
}
