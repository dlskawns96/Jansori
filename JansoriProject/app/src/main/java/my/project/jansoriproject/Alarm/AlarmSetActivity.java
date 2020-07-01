package my.project.jansoriproject.Alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

import my.project.jansoriproject.R;

public class AlarmSetActivity extends AppCompatActivity {

    FloatingActionButton saveAlarmFab;
    AlarmManager alarmManager;
    PendingIntent alarmIntent;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        saveAlarmFab = findViewById(R.id.fabSaveAlarm);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        timePicker = findViewById(R.id.timePickerAddAlarm);

        saveAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAlarm();
            }
        });
    }

    /**
     * TODO
     * 세이브버튼 클릭시,
     * 입력한 시간, 분, 반복 요일로 알람 세팅
     */
    private void saveAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);


        returnValues(timePicker.getHour(), timePicker.getMinute());
    }

    private void returnValues(int hour, int minute) {
        Intent intent = new Intent();
        intent.putExtra("Hour", hour);
        intent.putExtra("Minute", minute);

        /**
         * TODO
         * 반복 날짜 입력받고 보내주기
         */
        ArrayList<String> activeDays = new ArrayList<>();
        activeDays.add("Mon");
        intent.putExtra("ActiveDays", activeDays);
        setResult(Activity.RESULT_OK, intent);
        finish();;
    }
}