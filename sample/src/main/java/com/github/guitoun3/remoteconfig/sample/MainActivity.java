package com.github.guitoun3.remoteconfig.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.guitoun3.remoteconfig.RemoteConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RemoteConfig(this)
                .setBaseUrl("http://meilleurescitations.apprize.fr/v2/")
                .setConfigFile("config.json")
                .setLocalDefaultConfigFile("default_config.json")
                .setDebug(true)
                .getConfig();
    }
}
