package my.project.jansoriproject;

public class TransferInfoItem {

    String pathType;
    String lane;
    String transferStart;
    String transferEnd;
    String transferStartStation;
    String transferStartStationId;
    String transferEndStation;
    String sectionTime;

    public String getPathType() {
        return pathType;
    }

    public void setPathType(String pathType) {
        this.pathType = pathType;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getTransferStartStationId() {
        return transferStartStationId;
    }

    public void setTransferStartStationId(String transferStartStationId) {
        this.transferStartStationId = transferStartStationId;
    }

    public String getTransferStart() {
        return transferStart;
    }

    public void setTransferStart(String transferStart) {
        this.transferStart = transferStart;
    }

    public String getTransferEnd() {
        return transferEnd;
    }

    public void setTransferEnd(String transferEnd) {
        this.transferEnd = transferEnd;
    }

    public String getTransferStartStation() {
        return transferStartStation;
    }

    public void setTransferStartStation(String transferStartStation) {
        this.transferStartStation = transferStartStation;
    }

    public String getTransferEndStation() {
        return transferEndStation;
    }

    public void setTransferEndStation(String transferEndStation) {
        this.transferEndStation = transferEndStation;
    }

    public String getSectionTime() {
        return sectionTime;
    }

    public void setSectionTime(Integer sectionTime) {
        Integer hour, minute;
        hour = sectionTime/60;
        minute = sectionTime%60;
        if (sectionTime < 60)
            this.sectionTime = ""+minute+" 분";
        else
            this.sectionTime = ""+hour+" 시간"+minute+" 분";
    }
}
