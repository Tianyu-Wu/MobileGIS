package ikg.ethz.a1;

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
}
