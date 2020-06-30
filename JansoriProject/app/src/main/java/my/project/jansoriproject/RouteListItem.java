package my.project.jansoriproject;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteListItem {

    String totalTime;
    String arrivalTime;
    Integer payment;
    Integer totalWalk;
    Integer transitCount;
    String firstStartStation;
    String lastEndStation;
    Integer resId;
    ArrayList<TransferInfoItem> item;
//    String laneName;

//    HashMap<String, ArrayList<String>> transferInfo;

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getPayment() {
        return payment;
    }

    public void setPayment(Integer payment) {
        this.payment = payment;
    }

    public Integer getTotalWalk() {
        return totalWalk;
    }

    public void setTotalWalk(Integer totalWalk) {
        this.totalWalk = totalWalk;
    }

    public Integer getTransitCount() {
        return transitCount;
    }

    public void setTransitCount(Integer transitCount) {
        this.transitCount = transitCount;
    }

    public String getFirstStartStation() {
        return firstStartStation;
    }

    public void setFirstStartStation(String firstStartStation) {
        this.firstStartStation = firstStartStation;
    }

    public String getLastEndStation() {
        return lastEndStation;
    }

    public void setLastEndStation(String lastEndStation) {
        this.lastEndStation = lastEndStation;
    }

//    public String getLaneName() {
//        return laneName;
//    }
//
//    public void setLaneName(String laneName) {
//        this.laneName = laneName;
//    }

//    public HashMap<String, ArrayList<String>> getTransferInfo() {
//        return transferInfo;
//    }
//
//    public void setTransferInfo(HashMap<String, ArrayList<String>> transferInfo) {
//        this.transferInfo = transferInfo;
//    }

    public Integer getResId() {
        return resId;
    }

    public void setResId(Integer resId) {
        this.resId = resId;
    }

    public ArrayList<TransferInfoItem> getItem() {
        return item;
    }

    public void setItem(ArrayList<TransferInfoItem> item) {
        this.item = item;
    }
}
