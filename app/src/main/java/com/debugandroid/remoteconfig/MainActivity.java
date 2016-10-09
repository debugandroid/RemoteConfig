package com.debugandroid.remoteconfig;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseRemoteConfig mRemoteConfig;
    private TextView txt_remote_config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing the RemoteConfig instance
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();
 // Define default configuration values. It can be used in case where
        // config not fetched due to any issue
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("remote_test_parameter", 10);

        // Apply the configuration settings and default values of remote config.
        mRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mRemoteConfig.setDefaults(defaultConfigMap);
        txt_remote_config= (TextView) findViewById(R.id.txt_remote_config);
        //calling the loadConfig Method to fetch the remote configuration
        loadConfig();

    }

    private void loadConfig() {

        long cacheExpiration = 3600; // we set here 1 hours in seconds

        // If developer mode is enabled we need to reduce cacheExpiration to 0 so that
        // every time our app fetch the config from remote server.
        // remove the below line of if condition code in release version
        if (mRemoteConfig.getInfo().getConfigSettings()
                .isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRemoteConfig.activateFetched();
                        //calling the ApplyConfig method to apply the fetch configuration
                        applyConfig();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("RemoteConfig", "Error fetching config: " +
                                e.getMessage());
                        // On error we can apply different method or we can use the same as well
                        // As we have already set the default value
                        applyOnFailure();
                    }
                });
    }

    private void applyOnFailure() {
        String remote_value=mRemoteConfig.getString("remote_test_parameter");
        txt_remote_config.setText("Remote Config fetch failed, Setting Default value is:" + remote_value);

    }

    private void applyConfig() {
        String remote_value=mRemoteConfig.getString("remote_test_parameter");

        txt_remote_config.setText("Remote Config fetch, value is:" + remote_value);

    }


}
