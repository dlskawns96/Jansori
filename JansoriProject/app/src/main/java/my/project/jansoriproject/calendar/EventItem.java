package my.project.jansoriproject.calendar;

public class EventItem {

    int year;
    int month;
    int day;
    int startHour;
    int startMinute;
    int endHour;
    int endMinute;
    String location;
    String description;
    String title;

    public int getYear() {
        return this.year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return this.month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getDay() {
        return this.day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getStartHour() {
        return this.startHour;
    }
    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }
    public int getStartMinute() {
        return this.startMinute;
    }
    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }
    public int getEndHour() {
        return this.endHour;
    }
    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }
    public int getEndMinute() {
        return this.endMinute;
    }
    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}
