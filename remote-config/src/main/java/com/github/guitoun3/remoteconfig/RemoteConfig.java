package com.github.guitoun3.remoteconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
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
    public static final String PREFIX = "remote_config_";

    public interface Config {
        @GET
        Call<Map<String, Object>> configEntries(@Url String configFile);
    }

    private boolean mDebug = false;
    private Context mContext;
    private String mBaseUrl;
    private String mConfigFile;
    private String mLocalDefaultConfigFile;
    private String mPreferencePrefix = PREFIX;
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
                Log.d(TAG, "Remote config: \n\t" + entries.toString());
            }

            storeConfig(entries, false);
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
                Log.d(TAG, "Init default config file: \n\t" + data.toString());
            }
        }
    }

    private void loadDefaultConfig() {
        storeConfig(mDefaultConfig, true);
    }

    private void storeConfig(Map<String, Object> entries, boolean defaultConfig) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();

        Iterator<Map.Entry<String, Object>> it = entries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> item = it.next();
            String key = item.getKey();
            Object value = item.getValue();

            if (entries.equals(mDefaultConfig)) {
                it.remove();
            } else {
                mDefaultConfig.remove(key);
            }

            if (defaultConfig && mSharedPrefs.contains(mPreferencePrefix + key)) {
                continue;
            }

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

            if (mDebug) {
                Log.d(TAG, "Store " + key + " -> " + value);
            }
        }

        editor.apply();
    }
}
