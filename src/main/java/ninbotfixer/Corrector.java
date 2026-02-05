package ninbotfixer;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ninjabrainbot.Main;

import java.util.*;
import java.util.prefs.Preferences;

import static java.lang.Math.abs;

class Corrector {
    public static final double EPSILON = 0.0000001;
    public static final float EPSILON_F = 0.0000001f;

    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }
        String action = args[0];
        Adjustments adjustments = new Gson().fromJson(args[1], Adjustments.class);
        String[] fixFilter = new String[]{};
        if (args.length >= 3) {
            fixFilter = new Gson().fromJson(args[2], String[].class);
        }
        Preferences prefs = Main.getPreferences();
        switch (action) {
            case "get":
                String[] breakingFix = get(prefs, adjustments.breaking).toArray(new String[0]);
                String[] recommendFix = get(prefs, adjustments.recommend, breakingFix).toArray(new String[0]);
                JsonObject obj = new JsonObject();
                obj.add("breaking", new Gson().toJsonTree(breakingFix));
                obj.add("recommend", new Gson().toJsonTree(recommendFix));
                System.out.println(new Gson().toJson(obj));
                break;
            case "fix-all":
                fix(prefs, adjustments.breaking, fixFilter);
                fix(prefs, adjustments.recommend, fixFilter);
                break;
            case "fix-breaking":
                fix(prefs, adjustments.breaking, fixFilter);
                break;
            default:
                printUsage();
                break;
        }
    }

    public static List<String> get(Preferences prefs, List<Adjuster> adjusters) {
        return get(prefs, adjusters, new String[]{});
    }

    public static List<String> get(Preferences prefs, List<Adjuster> adjusters, String[] dontRepeat) {
        List<String> result = new ArrayList<>();
        List<String> noRepeat = Arrays.asList(dontRepeat);
        for (Adjuster adjuster : adjusters) {
            if (noRepeat.contains(adjuster.id)) continue;
            Adjuster.JavaValue adjustment = adjuster.adjustment;
            JsonElement defaultValue = adjuster.defaultValue == null ? null : adjuster.defaultValue.value;

            switch (adjustment.type) {
                case "int": {
                    int defValue = defaultValue == null ? 0 : defaultValue.getAsInt();
                    int allowedError = adjuster.allowedError == null ? 0 : adjuster.allowedError.intValue();
                    if (abs(prefs.getInt(adjuster.id, defValue) - adjustment.value.getAsInt()) > allowedError) {
                        result.add(adjuster.id);
                    }
                    break;
                }
                case "double": {
                    double defValue = defaultValue == null ? 0 : defaultValue.getAsDouble();
                    double allowedError = adjuster.allowedError == null ? EPSILON : adjuster.allowedError;
                    if (abs(prefs.getDouble(adjuster.id, defValue) - adjustment.value.getAsDouble()) > allowedError) {
                        result.add(adjuster.id);
                    }
                    break;
                }
                case "float": {
                    float defValue = defaultValue == null ? 0 : defaultValue.getAsFloat();
                    float allowedError = adjuster.allowedError == null ? EPSILON_F : adjuster.allowedError.floatValue();
                    if (abs(prefs.getDouble(adjuster.id, defValue) - adjustment.value.getAsFloat()) > allowedError) {
                        result.add(adjuster.id);
                    }
                    break;
                }
                case "boolean": {
                    boolean defValue = defaultValue != null && defaultValue.getAsBoolean();
                    if (prefs.getBoolean(adjuster.id, defValue) != adjustment.value.getAsBoolean()) {
                        result.add(adjuster.id);
                    }
                    break;
                }
                case "string": {
                    String defValue = defaultValue == null ? "" : defaultValue.getAsString();
                    if (!adjustment.value.getAsString().equals(prefs.get(adjuster.id, defValue))) {
                        result.add(adjuster.id);
                    }
                    break;
                }
            }
        }
        return result;
    }

    public static void fix(Preferences prefs, List<Adjuster> adjusters, String[] fixFilter) {
        Collection<String> filter = Arrays.asList(fixFilter);
        for (Adjuster adjuster : adjusters) {
            if (!filter.isEmpty() && !filter.contains(adjuster.id)) continue;
            Adjuster.JavaValue adjustment = adjuster.adjustment;
            JsonElement defaultValue = adjuster.defaultValue == null ? null : adjuster.defaultValue.value;

            switch (adjustment.type) {
                case "int": {
                    int defValue = defaultValue == null ? 0 : defaultValue.getAsInt();
                    int allowedError = adjuster.allowedError == null ? 0 : adjuster.allowedError.intValue();
                    if (abs(prefs.getInt(adjuster.id, defValue) - adjustment.value.getAsInt()) > allowedError) {
                        prefs.putInt(adjuster.id, adjustment.value.getAsInt());
                    }
                    break;
                }
                case "double": {
                    double defValue = defaultValue == null ? 0 : defaultValue.getAsDouble();
                    double allowedError = adjuster.allowedError == null ? EPSILON : adjuster.allowedError;
                    if (abs(prefs.getDouble(adjuster.id, defValue) - adjustment.value.getAsDouble()) > allowedError) {
                        prefs.putDouble(adjuster.id, adjustment.value.getAsDouble());
                    }
                    break;
                }
                case "float": {
                    float defValue = defaultValue == null ? 0 : defaultValue.getAsFloat();
                    float allowedError = adjuster.allowedError == null ? EPSILON_F : adjuster.allowedError.floatValue();
                    if (abs(prefs.getDouble(adjuster.id, defValue) - adjustment.value.getAsFloat()) > allowedError) {
                        prefs.putFloat(adjuster.id, adjustment.value.getAsFloat());
                    }
                    break;
                }
                case "boolean": {
                    boolean defValue = defaultValue != null && defaultValue.getAsBoolean();
                    if (prefs.getBoolean(adjuster.id, defValue) != adjustment.value.getAsBoolean()) {
                        prefs.putBoolean(adjuster.id, adjustment.value.getAsBoolean());
                    }
                    break;
                }
                case "string": {
                    String defValue = defaultValue == null ? "" : defaultValue.getAsString();
                    if (!adjustment.value.getAsString().equals(prefs.get(adjuster.id, defValue))) {
                        prefs.put(adjuster.id, adjustment.value.getAsString());
                    }
                    break;
                }
            }
        }
    }

    private static void printUsage() {
        System.err.println("Usage: <get|fix-breaking|fix-all> <adjustments> [fixes-to-apply]");
        System.exit(1);
    }
}