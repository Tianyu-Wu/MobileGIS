package ikg.ethz.a1;

import java.sql.Timestamp;

public class Track {
    private int user_id;
    private int track_id;
    private POI startPOI;
    private POI endPOI;
    private Timestamp startTime;
    private Timestamp endTime;

    public Track(int user_id, int track_id, POI startPOI, POI endPOI, Timestamp startTime, Timestamp endTime) {
        this.user_id = user_id;
        this.track_id = track_id;
        this.startPOI = startPOI;
        this.endPOI = endPOI;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Track() {

    }

    public int getUser_id() {
        return user_id;
    }

    public int getTrack_id() {
        return track_id;
    }

    public POI getStartPOI() {
        return startPOI;
    }

    public POI getEndPOI() {
        return endPOI;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public void setStartPOI(POI startPOI) {
        this.startPOI = startPOI;
    }

    public void setEndPOI(POI endPOI) {
        this.endPOI = endPOI;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public double duration() {
        return endTime.getTime() - startTime.getTime();
    }
}
