package ikg.ethz.a1;

import java.sql.Time;
import java.sql.Timestamp;

public class Trajectory {

    private int user_id;
    private int track_id;
    private Timestamp time;
    private double longitude;
    private double latitude;
    private float temperature;

    public Trajectory(int user_id, int track_id, Timestamp time, double longitude, double latitude, float temperature) {
        this.user_id = user_id;
        this.track_id = track_id;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.temperature = temperature;
    }

    public Timestamp getTime() {
        return time;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public float getTemperature() {
        return temperature;
    }

}
