package ikg.ethz.a1;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.sql.Timestamp;

public class Trajectory {

    private int user_id;
    private int track_id;
    private Timestamp time;
    private double longitude;
    private double latitude;
    private double altitude;
    private float temperature;

    public Trajectory(int user_id, int track_id, Timestamp time, double longitude, double latitude, double altitude, float temperature) {
        this.user_id = user_id;
        this.track_id = track_id;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.temperature = temperature;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTrack_id() {
        return track_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void outputFiles() {
        // Saving users input to a CSV file
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try{
                Log.d("FileLog", "start writing trajectory ");

                File directory = Environment.getExternalStorageDirectory();

                File file = new File (directory, "trajectories.csv");

                FileOutputStream outputStream = new FileOutputStream(file, true);
                PrintWriter writer = new PrintWriter(outputStream);

                Log.d("FileLog", "start writing trajectory");
                writer.print(user_id + ",");
                writer.print(track_id + ",");
                writer.print(time + ",");
                writer.print(longitude + ",");
                writer.print(latitude + ",");
                writer.print(altitude + ",");
                writer.println(temperature);

                writer.close();
                outputStream.close();
                Log.e("FileLog", "trajectories.csv Saved :  " + file.getPath());

            }catch(IOException e){
                Log.e("FileLog", "File to write trajectories");
            }
        }else{
            Log.e("FileLog", "SD card not mounted");
        }
    }
}
