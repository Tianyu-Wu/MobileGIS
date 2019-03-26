package ikg.ethz.a1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    // Specify static variables
    private static final int USER_ID = 14;
    private static final String TRAJECTORIES_FILENAME = "trajectories.csv";
    private static final String TRACKS_FILENAME = "tracks.csv";

    // Declare variables
    private List<POI> POIs;

    private LocationManager mLocationManager;
    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load POIs from the Pois.csv file
        POIs = new ArrayList<>();
        loadPOI(POIs);
        Log.d("MainActivity", "ended loading " + POIs.size() + " POIs");

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onStart() {
        if (checkPermission()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check the avaiability of sensors and register for senser event listener
        Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor tsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (gsensor != null) {
            mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (msensor != null) {
            mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
        }

        if (tsensor != null) {
            mSensorManager.registerListener(this, tsensor, SensorManager.SENSOR_DELAY_GAME);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {

        // TODO Calculate whether within the zone of POI
        // TODO Create pop-up for start tracking
        // TODO Tracking mode implementation

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


// HELPER FUNCTIONS

    /**
     * Check for permission. If permission is not granted, a pop up shows to ask for it
     *
     * @return True if permission granted
     *         False if no permissions granted
     */
    private boolean checkPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // if request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // start the location update
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
                }
            }
        }
    }


    private boolean loadPOI(List<POI> pois) {
        try{
            String line = "";
            InputStream inputStream = getResources().openRawResource(R.raw.pois);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int i = 0;
            while((line = reader.readLine()) != null) {
                if (i == 0) {
                    ++i;
                    continue;
                }
                String[] parse = line.split(";");
                Log.d("MainActivity", parse[0]);
                Log.d("MainActivity", parse[1]);
                Log.d("MainActivity", parse[2]);
                Log.d("MainActivity", parse[3]);

                POI poi = new POI(parse[0], parse[1], Double.valueOf(parse[2]), Double.valueOf(parse[3]));
                Log.d("MainActivity", "Successfully loaded " + parse[1]);
                pois.add(poi);
                ++i;
            }
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
