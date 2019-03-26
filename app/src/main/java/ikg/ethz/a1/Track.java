package ikg.ethz.a1;

public class Track {
    private int user_id;
    private int track_id;
    private POI startPOI;
    private POI endPOI;
    private double duration;

    public Track(int user_id, int track_id, POI startPOI, POI endPOI, double duration) {
        this.user_id = user_id;
        this.track_id = track_id;
        this.startPOI = startPOI;
        this.endPOI = endPOI;
        this.duration = duration;
    }

    public POI getStartPOI() {
        return startPOI;
    }

    public POI getEndPOI() {
        return endPOI;
    }

    public double getDuration() {
        return duration;
    }

}
