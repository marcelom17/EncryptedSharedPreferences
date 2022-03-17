package com.example.encryptedprefs;

import static com.example.encryptedprefs.MainActivity.JUST_PREFS;
import static com.example.encryptedprefs.MainActivity.SECRET_PREFS;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.util.Map;

public class PreferenceManager {
    private static PreferenceManager sInstance;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private PreferenceManager(Context ctx) {
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String masterKeyAlias;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            mPrefs = EncryptedSharedPreferences.create(
                    SECRET_PREFS,
                    masterKeyAlias,
                    ctx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            mEditor = mPrefs.edit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PreferenceManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceManager(context);
        }
        return sInstance;
    }

    public void migrateToEncryptedSharedPreferences(Context context){
        //non-secured shared pref
        SharedPreferences nonSecuredPreference = context.getSharedPreferences(JUST_PREFS, context.MODE_PRIVATE);
        if (!nonSecuredPreference.getAll().isEmpty()) {
            copyTo(nonSecuredPreference);
        }
    }

    private void copyTo(SharedPreferences nonSecuredPreference) {
        Map<String, ?> keys = nonSecuredPreference.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Log.d("map values", key + ": " + value.toString());
            if (value instanceof Integer) {
                mEditor.putInt(key, (Integer) value).commit();
                nonSecuredPreference.edit().remove(key).apply();
            } else if (value instanceof Boolean) {
                mEditor.putBoolean(key, (Boolean) value).commit();
                nonSecuredPreference.edit().remove(key).apply();
            } else if (value instanceof Long) {
                mEditor.putLong(key, (Long) value).commit();
                nonSecuredPreference.edit().remove(key).apply();
            } else if (value instanceof Float) {
                mEditor.putFloat(key, (Float) value).commit();
                nonSecuredPreference.edit().remove(key).apply();
            } else {
                mEditor.putString(key, (String) value).commit();
                nonSecuredPreference.edit().remove(key).apply();
            }
        }
    }

}
