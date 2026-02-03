package ninjabrainbot;

import java.util.prefs.Preferences;

/** This is not the real entrypoint, this is to pretend to be ninjabrain bot */
public class Main {
    public static Preferences getPreferences(){
        return Preferences.userNodeForPackage(Main.class);
    }
}
