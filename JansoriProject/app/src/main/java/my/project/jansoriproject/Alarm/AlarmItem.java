package my.project.jansoriproject.Alarm;

import java.util.ArrayList;

class AlarmItem {
    public int hour;
    public int minute;

    public ArrayList<String> activeDays;

    public AlarmItem(int hour, int minute, ArrayList<String> activeDays) {
        this.hour = hour;
        this.minute = minute;
        this.activeDays = activeDays;
    }
}
