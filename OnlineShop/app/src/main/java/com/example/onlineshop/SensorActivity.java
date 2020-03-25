package com.example.onlineshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

public class SensorActivity extends AppCompatActivity
{

    private TextView textLight;
    private TextView textTemp;
    private TextView textAccuracy;
    private TextView textLatitude;
    private TextView textLongitude;
    private Sensor lightSensor;
    private Sensor tempSensor;
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private LocationListener listener;
    private Button requestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert sensorManager != null;
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        textTemp = findViewById(R.id.textTemp);
        textLight = findViewById(R.id.textLight);
        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        requestLocation = findViewById(R.id.buttonRequestLocation);
        textAccuracy = findViewById(R.id.textAccuracy);

        listener = new LocationListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(Location location)
            {
                textAccuracy.setText("Accuracy: " + location.getAccuracy() + " meters");
                textLatitude.setText("Latitude: " + location.getLatitude());
                textLongitude.setText("Longitude: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) { }

            @Override
            public void onProviderEnabled(String s) { }

            @Override
            public void onProviderDisabled(String s)
            {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == 10)
        {
            configure_button();
        }
    }

    void configure_button()
    {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            return;
        }

        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        requestLocation.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view)
            {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        sensorManager.registerListener(onSensorChanged, lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(onSensorChanged, tempSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop()
    {
        super.onStop();
        sensorManager.unregisterListener(onSensorChanged);
        sensorManager.unregisterListener(onSensorChanged);
    }

    public SensorEventListener onSensorChanged = new SensorEventListener()
    {
        @Override
        public void onAccuracyChanged(Sensor sensor, int acc)
        {
        }

        public void onSensorChanged(SensorEvent event)
        {
            if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
            {
                textTemp.setText(event.values[0] + " °C");
            }
            if (event.sensor.getType() == Sensor.TYPE_LIGHT)
            {
                textLight.setText(event.values[0] + " lux");
            }
        }
    };
}
