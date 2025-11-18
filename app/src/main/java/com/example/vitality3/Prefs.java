package com.example.vitality3;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

public class Prefs {
    private static final String FILE_NAME = "vitality_prefs";
    private static final String KEY_EMAIL = "usuarioEmail";

    private SharedPreferences prefs;

    public Prefs(Context context) {
        prefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
