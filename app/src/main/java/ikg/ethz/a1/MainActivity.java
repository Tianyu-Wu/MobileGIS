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
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    // Specify static variables
    private static final int USER_ID = 14;
    private static final double PROXIMITY = 100;
    private static final String TRAJECTORIES_FILENAME = "trajectories.csv";
    private static final String TRACKS_FILENAME = "tracks.csv";

    // Declare variables
    private RelativeLayout mainLayout;
    private TextView showRotation;
    private TextView temperature;
    private TextView speed;
    private TextView distance;
    private TextView direction;

    private ImageView compass;
    private ImageView arrow;
    private List<POI> POIs;
    private List<Trajectory> Trajectories;
    private List<Track> Tracks;

    private LocationManager mLocationManager;
    private SensorManager mSensorManager;

    // record the compass picture angle turned
    private double currentDegree = 0f;
    // target POI angle reference to current location
    private double angle = 0;
    private float[] Gravity = new float[3];
    private float[] Rotation = new float[16];
    private float[] Inclination = new float[16];
    private float[] Magnetic = new float[3];
    private float[] Orientation = new float[3];
    private float north_azimuth = 0f;
    private float target_azimuth = 0f;
    private float currentAzimuth = 0f;
    private float currentTargetAzimuth = 0f;

    // Define the status of app
    // 1 -- initialized; 2 -- tracking;
    private int appStatus = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get variables
        mainLayout  = findViewById(R.id.mainLayout);
        temperature = findViewById(R.id.temperatureValue);
        speed = findViewById(R.id.speedValue);
        distance = findViewById(R.id.distanceValue);
        direction = findViewById(R.id.directionValue);

        showRotation = findViewById(R.id.text);
        compass = findViewById(R.id.img_compass);
        arrow = findViewById(R.id.img_arrow);

        // Initialize variables
        POIs = new ArrayList<>();
        Trajectories = new ArrayList<>();
        Tracks = new ArrayList<>();

        checkLocationPermission();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // load POIs from the Pois.csv file
        loadPOI(POIs);
        POIs.add(new POI("Hexagon", "POI6", 8.507212, 47.408039));
        Log.d("MainActivity", "ended loading " + POIs.size() + " POIs");


    }

    @Override
    protected void onStart() {
        if (checkLocationPermission()) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
            //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
        }
        Log.d("onStart", "permission checked: "+ checkLocationPermission()+"; "+checkWritePermission());

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
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {

        mLocationManager.removeUpdates(this);
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8f;

        //final float alpha = 0.97f;

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temperature.setText(Float.toString(event.values[0]));
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            Gravity[0] = alpha*Gravity[0] + (1-alpha)*event.values[0];
            Gravity[1] = alpha*Gravity[1] + (1-alpha)*event.values[1];
            Gravity[2] = alpha*Gravity[2] + (1-alpha)*event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Magnetic[0] = alpha*Magnetic[0] + (1-alpha)*event.values[0];
            Magnetic[1] = alpha*Magnetic[1] + (1-alpha)*event.values[1];
            Magnetic[2] = alpha*Magnetic[2] + (1-alpha)*event.values[2];
        }

        SensorManager.getRotationMatrix(Rotation, Inclination, Gravity, Magnetic);
        SensorManager.getOrientation(Rotation, Orientation);
        // get the azimuth relative to north
        north_azimuth =  (float) Math.toDegrees(Orientation[0]);
        // add the relative rotation from target POI to current location
        //if (north_azimuth * angle < 0) target_azimuth = north_azimuth - (float) angle;
        //else target_azimuth = (float) angle - north_azimuth;
        target_azimuth = north_azimuth + (float) angle;

        north_azimuth = (360+north_azimuth)%360;
        target_azimuth = (360+target_azimuth)%360;

        showRotation.setText("north: "+north_azimuth + "degree; " + "target: "+target_azimuth);


        //showRotation.setText("Heading: " + Double.toString(north_azimuth) + " degrees ; " + Double.toString(target_azimuth) + " degrees to " + POIs.get(index).getName());

        // TODO Rotation of the compass
        compassRotation((float) -currentAzimuth, (float) -north_azimuth, compass);

        // TODO Rotation of the arrow pointing to target POI
        compassRotation((float) -currentTargetAzimuth, (float) -target_azimuth, arrow);

        // set currentAzimuth to current angle
        currentAzimuth = north_azimuth;
        currentTargetAzimuth = target_azimuth;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d("locationChange", "onLocation changed: "+location.getLatitude()+", "+location.getLongitude());
        speed.setText(Double.toString(location.getSpeed()));
        if (appStatus == 1){
            // TODO Calculate whether within the zone of POI
            Log.d("LocationChange", "number of POIs: " + POIs.size());
            double minDistance = location.distanceTo(POIs.get(0).getLocation());
            double currentDistance = 0;
            int index = 0;
            for (int i = 0; i < POIs.size(); i++) {
                if ((currentDistance = location.distanceTo(POIs.get(i).getLocation())) < minDistance) {
                    minDistance = currentDistance;
                    index = i;
                }
            }
            Log.d("locationChange", "closest poi" + index + "; distance: " + minDistance);
            distance.setText(Double.toString(minDistance));
            direction.setText(Double.toString(angle));

            // if within the PROXIMITY, ask if start tracking with this POI as the origin
            if (minDistance < PROXIMITY) {
                // TODO Angle = 0
                angle = 0;
                // TODO Button enabled and ask for tracking
                final POI origin = POIs.get(index);
                Log.d("locationChange", "within the proximity of " + origin.getName());
                //TODO Change the time that snackbar disappears -- when leaving the building
                Snackbar notification = Snackbar.make(mainLayout, "You have enterred the " + POIs.get(index).getName(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Start Tracking", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                appStatus = 2;
                                Log.d("Snackbar", "Tracking started! current appStatus = " + appStatus);
                                // create a new entry in trajectories as the origin
                                Trajectories.add(new Trajectory(USER_ID, Tracks.size()+1, new Timestamp(location.getTime()), location.getLongitude(), location.getLatitude(), location.getAltitude(), Float.valueOf(temperature.getText().toString())));

                                // create a new entry in tracks
                                Track newTrack = new Track();
                                newTrack.setUser_id(USER_ID);
                                newTrack.setTrack_id(Tracks.size()+1);
                                newTrack.setStartPOI(origin);
                                newTrack.setStartTime(new Timestamp(location.getTime()));
                                Tracks.add(newTrack);
                                // TODO Notify users that they are in Tracking mode now
                                Snackbar snackbar = Snackbar.make(mainLayout, "You are now in tracking mode from " + origin.getName(), Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();

                            /*
                            Intent startTracking = new Intent(MainActivity.this, Tracking.class);
                            startTracking.putExtra("name", POIs.get(index).getName());
                            startTracking.putExtra("index", index);
                            startActivity(startTracking);
                            */
                            }
                        });
                notification.show();
            } else {
                // TODO notify user that he is not close to any POIs
                Snackbar notification = Snackbar.make(mainLayout, "You are not close to any of the POIs. Please follow the direction shown by compass to the closest POI " + POIs.get(index).getName(), Snackbar.LENGTH_INDEFINITE);
                notification.show();
                // TODO Angle to the closest POI
                angle = location.bearingTo(POIs.get(index).getLocation());
                Log.d("locationChange", "angle to the closest POI " + angle);
            }

        } else if (appStatus == 2){
            // TODO Tracking mode implementation
            // TODO Notify user that he is in TRACKING MODE, and ends tracking when he clicks on the stop button on the snackbar
            Trajectory newPoint = new Trajectory(USER_ID, Tracks.size(), new Timestamp(location.getTime()), location.getLongitude(), location.getLatitude(), location.getAltitude(),Float.valueOf(temperature.getText().toString()));
            Trajectories.add(newPoint);
            newPoint.outputFiles();

            // TODO Find the target POI
            POI origin = Tracks.get(Tracks.size()-1).getStartPOI();
            // initialize the index and minimum distance for target POI finding
            int index = POIs.indexOf(origin);
            double minDistance;
            if ( POIs.indexOf(origin) == 0) {
                minDistance = location.distanceTo(POIs.get(1).getLocation());
                index = 1;
            } else {
                minDistance = location.distanceTo(POIs.get(index-1).getLocation());
                index = index-1;
            }
            double currentDistance = 0;
            for (int i = 0; i < POIs.size(); i++) {
                if (POIs.indexOf(origin) == i) continue;
                if ((currentDistance = location.distanceTo(POIs.get(i).getLocation()))< minDistance) {
                    minDistance = currentDistance;
                    index = i;
                }
            }

            Log.d("locationChange", "Target POI index is " + index + "current minDistance is " + minDistance);
            distance.setText(Double.toString(minDistance));

            // TODO Tracking mode display
            // TODO If entered, stop tracking, update current track with endPOI and endTime
            if (minDistance < PROXIMITY) {
                final POI end = POIs.get(index);
                int lastTrack = Tracks.size()-1;
                Tracks.get(lastTrack).setEndPOI(end);
                Tracks.get(lastTrack).setEndTime(new Timestamp(location.getTime()));

                // Save this track
                Tracks.get(lastTrack).outputFiles();

                // TODO Summarize trip, back to initial stage
                final Snackbar summary = Snackbar.make(mainLayout,
                        "Finished tracking from" + origin.getName() + " to " + end.getName() + "! Totoal duration: " + Tracks.get(lastTrack).duration() + ". Do you want to start a new track with this POI as the origin?", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                appStatus = 2;
                                Log.d("Snackbar", "Tracking started! current appStatus = " + appStatus);
                                // create a new entry in trajectories as the origin
                                Trajectories.add(new Trajectory(USER_ID, Tracks.size()+1, new Timestamp(location.getTime()), location.getLongitude(), location.getLatitude(), location.getAltitude(), Float.valueOf(temperature.getText().toString())));

                                // create a new entry in tracks
                                Track newTrack = new Track();
                                newTrack.setUser_id(USER_ID);
                                newTrack.setTrack_id(Tracks.size()+1);
                                newTrack.setStartPOI(end);
                                newTrack.setStartTime(new Timestamp(location.getTime()));
                                Tracks.add(newTrack);
                                // TODO Notify users that they are in Tracking mode now
                                Snackbar snackbar = Snackbar.make(mainLayout, "You are now in tracking mode from " + end.getName(), Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                            }
                        });
                summary.show();
                appStatus = 1;
            } else {
                // TODO Calculate the angle
                // the POI with the minimal distance is the target POI, the index of the target POI is recorded by index
                // get the relative angle to the target POI and update the value of angle
                //angle = relativeAngle(location, index);
                angle = location.bearingTo(POIs.get(index).getLocation());
                Log.d("locationChange", "angle to target " + POIs.get(index).getName() + "; bearing to target " + angle);
            }

            direction.setText(Double.toString(angle));

            // TODO If click on the stop button on the snackbar, end tracking

        }

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
    private boolean checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            return true;
        }
    }

    private boolean checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
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

                POI poi = new POI(parse[0], parse[1], Double.valueOf(parse[3]), Double.valueOf(parse[2]));
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

    private double relativeAngle (Location currentLocation, int index) {
        // calculate the arctangent of two locations and
        double angle = Math.atan2(POIs.get(index).getLatitude() - currentLocation.getLatitude(), POIs.get(index).getLongitude() - currentLocation.getLongitude());
        if (angle > 0) {
            angle = angle / Math.PI * 180 - 90;
        } else {
            angle = 270 + angle/Math.PI*180;
        }
        return angle;
    }

    private void compassRotation(float from, float to, ImageView img) {
        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        ra.setRepeatCount(0);
        // Start the animation
        img.startAnimation(ra);
    }

}
