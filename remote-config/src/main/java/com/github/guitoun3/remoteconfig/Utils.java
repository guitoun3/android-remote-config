package com.github.guitoun3.remoteconfig;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class Utils {

    public static Map<String,Object> loadJSONFromAsset(Context context, String defaultConfigFile) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(defaultConfigFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Gson gson = new Gson();
        Map<String, Object> result = gson.fromJson(json, mapType);

        return result;
    }
}
