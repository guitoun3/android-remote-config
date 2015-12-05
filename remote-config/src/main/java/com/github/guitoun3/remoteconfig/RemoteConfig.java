package com.github.guitoun3.remoteconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Url;

public class RemoteConfig implements Callback<Map<String, Object>> {

    private static final String TAG = "RemoteConfig";
    public static final String DEFAULT_PREFIX = "remote_config_";

    public interface Config {
        @GET
        Call<Map<String, Object>> configEntries(@Url String configFile);
    }

    private boolean mDebug = false;
    private Context mContext;
    private String mBaseUrl;
    private String mConfigFile;
    private String mLocalDefaultConfigFile;
    private String mPreferencePrefix = DEFAULT_PREFIX;
    private SharedPreferences mSharedPrefs;

    private Map<String, Object> mDefaultConfig;

    public RemoteConfig(Context context) {
        mContext = context;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public RemoteConfig setDebug(boolean debug) {
        mDebug = debug;

        return this;
    }

    public RemoteConfig setBaseUrl(String baseUrl) {
        mBaseUrl = baseUrl;

        return this;
    }

    public RemoteConfig setConfigFile(String configFile) {
        mConfigFile = configFile;

        return this;
    }

    public RemoteConfig setLocalDefaultConfigFile(String defaultConfigFile) {
        mLocalDefaultConfigFile = defaultConfigFile;

        return this;
    }

    public RemoteConfig setPreferencePrefix(String prefix) {
        mPreferencePrefix = prefix;

        return this;
    }

    public void getConfig() {
        initDefaultConfig();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Config config = retrofit.create(Config.class);

        Call<Map<String, Object>> call = config.configEntries(mConfigFile);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Response<Map<String, Object>> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            Map<String, Object> entries = response.body();
            if (mDebug) {
                Log.d(TAG, entries.toString());
            }

            storeConfig(entries);
        }

        loadDefaultConfig();
    }

    @Override
    public void onFailure(Throwable t) {
        loadDefaultConfig();
    }

    private void initDefaultConfig() {
        mDefaultConfig = new HashMap<>();
        Map<String, Object> data = Utils.loadJSONFromAsset(mContext, mLocalDefaultConfigFile);

        if (data != null) {
            mDefaultConfig.putAll(data);
            if (mDebug) {
                Log.d(TAG, "Init default config file: \n" + data.toString());
            }
        }
    }

    private void loadDefaultConfig() {
        if (mDebug) {
            Log.d(TAG, "Loading default config...\n" + mDefaultConfig.toString());
        }
        storeConfig(mDefaultConfig);
    }

    private void storeConfig(Map<String, Object> entries) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            mDefaultConfig.remove(key);

            if (value instanceof String) {
                editor.putString(mPreferencePrefix + key, (String) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(mPreferencePrefix + key, (Boolean) value);
            } else if (value instanceof Integer) {
                editor.putInt(mPreferencePrefix + key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(mPreferencePrefix + key, (Long) value);
            } else if (value instanceof Float) {
                editor.putFloat(mPreferencePrefix + key, (Float) value);
            } else if (value instanceof Double) {
                editor.putString(mPreferencePrefix + key, String.valueOf(value));
            } else if (mDebug) {
                Log.d(TAG, "Invalid type: " + key + " -> " + value.getClass().getName());
            }

        }

        editor.apply();
    }
}
