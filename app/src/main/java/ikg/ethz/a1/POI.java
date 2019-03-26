package ikg.ethz.a1;

import android.location.Location;

public class POI {
    private String name;
    private String id;
    private double longitude;
    private double latitude;

    public POI(String name, String id, double longitude, double latitude) {
        this.name = name;
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Location getLocation() {
        Location poi = new Location("");
        poi.setLatitude(latitude);
        poi.setLongitude(longitude);
        return poi;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

}
