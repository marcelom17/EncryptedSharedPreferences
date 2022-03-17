package com.example.encryptedprefs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static String SECRET_PREFS = "secret_shared_prefs";
    public static final String JUST_PREFS = "old_shared_prefs";

    String masterKeyAlias = null;

    public static String INT_KEY = "keyInt";
    public static String STRING_KEY = "keyString";
    public static String BOOL_KEY = "keyBool";
    public static String FLOAT_KEY = "keyFloat";
    public static String LONG_KEY = "keyLong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    // 1) Create Plain Shared Prefs
         oldSharedPrefs();

    // 2) Migrate SharedPrefs to EncryptedSharedPreferences
         PreferenceManager prefsManager = PreferenceManager.getInstance(this);
         prefsManager.migrateToEncryptedSharedPreferences(this);

    // 3) Read Migrated Shared Prefs
        encryptedSharedPrefs();

    }

    public void oldSharedPrefs() {
        SharedPreferences notEncryptedPrefs = getSharedPreferences(JUST_PREFS, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = notEncryptedPrefs.edit();
        editor.putInt(INT_KEY, 12345);
        editor.putString(STRING_KEY, "just a saved string");
        editor.putBoolean(BOOL_KEY, true);
        editor.putFloat(FLOAT_KEY, 0.173F);
        editor.putLong(LONG_KEY, -6723887);
        editor.apply();

        System.out.println("INT_KEY  -  " + notEncryptedPrefs.getInt(INT_KEY, 0) );
        System.out.println("STRING_KEY  -  " + notEncryptedPrefs.getString(STRING_KEY, "empty") );
        System.out.println("BOOL_KEY  -  " + notEncryptedPrefs.getBoolean(BOOL_KEY, false) );
        System.out.println("FLOAT_KEY  -  " + notEncryptedPrefs.getFloat(FLOAT_KEY, 0.0F) );
        System.out.println("LONG_KEY  -  " + notEncryptedPrefs.getLong(LONG_KEY, 0) );

    }

    public void encryptedSharedPrefs() {
        SharedPreferences sharedPreferences = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            Log.i("master key", masterKeyAlias);

            sharedPreferences = EncryptedSharedPreferences.create(
                    SECRET_PREFS,
                    masterKeyAlias,
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        // use the shared preferences and editor as you normally would
        assert sharedPreferences != null;
        SharedPreferences.Editor editor = sharedPreferences.edit();

        System.out.println("\n\nFROM ENCRYPTED SHARED PREFS\n\n");
        System.out.println("INT_KEY  -  " + sharedPreferences.getInt(INT_KEY, 0) );
        System.out.println("STRING_KEY  -  " + sharedPreferences.getString(STRING_KEY, "empty") );
        System.out.println("BOOL_KEY  -  " + sharedPreferences.getBoolean(BOOL_KEY, false) );
        System.out.println("FLOAT_KEY  -  " + sharedPreferences.getFloat(FLOAT_KEY, 0.0F) );
        System.out.println("LONG_KEY  -  " + sharedPreferences.getLong(LONG_KEY, 0) );

        System.out.println("\n\n");
    }



}

