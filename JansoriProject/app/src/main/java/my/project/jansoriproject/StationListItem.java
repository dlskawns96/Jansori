package my.project.jansoriproject;

public class StationListItem {
    private String stationName;
    private String arsID;
    private String latitude;
    private String longitude;
    private int stationClass;
    private int resId;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getArsID() {
        return arsID;
    }

    public void setArsID(String arsID) {
        this.arsID = arsID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = Integer.parseInt(resId);
    }
}
