package com.github.guitoun3.remoteconfig.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.guitoun3.remoteconfig.RemoteConfig;

import java.util.Map;

public class MainActivity extends AppCompatActivity  implements RemoteConfig.RemoteConfigListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RemoteConfig(this)
                .setBaseUrl("http://meilleurescitations.apprize.fr/v2/")
                .setConfigFile("config_test.json")
                .setLocalDefaultConfigFile("default_config.json")
                .addRemoteListener(this)
                .setDebug(true)
                .getConfig();
    }

    @Override
    public void onRemoteConfigLoaded() {
        Log.d("MainActivity", "onRemoteConfigLoaded called");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("MainActivity", entry.getKey() + " : " + entry.getValue().toString());
        }
    }
}
