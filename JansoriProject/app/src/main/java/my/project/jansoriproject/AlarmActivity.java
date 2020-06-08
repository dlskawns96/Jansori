package my.project.jansoriproject;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import my.project.jansoriproject.AlarmReceiver;
import my.project.jansoriproject.BackPressHandler;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    /**
     * TODO
     * 시간 안맞추고 종료 클릭시 어플 다운
     * 시간 맞추고 종료 클릭시 시간 안맞는데 알람 울리고 어플 다운
     * 시간 맞추고 맞춘 시간에 알람 울리면 어플 다운
     */
    private static final String TAG = AlarmActivity.class.getSimpleName();
    Context mContext;
    TextView textView;
    TimePicker alarm_timepicker;
    AlarmManager alarm_manager;
    String am_pm;
    int getHourTimePicker = 0;
    int getMinuteTimePicker = 0;

    Intent alarmIntent;
    PendingIntent pendingIntent;
    private static final int REQUEST_CODE = 1111;
    SharedPreferences pref;

    private BackPressHandler backPressHandler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        mContext = AlarmActivity.this;
        backPressHandler = new BackPressHandler(this); // 뒤로 가기 버튼 이벤트

        textView = findViewById(R.id.tv_alarmON);
        textView.setText("");

        // 알람매니저 설정
        alarm_manager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        // 알람매니저가 유용한 이유는, Alarm이 한번 등록되면 어플리케이션의 생명주기와 관계없이
        // 어플리케이션이 종료되어있는 경우에도 지정해놓은 operation에 대해 항상 실행된다는 것

        // 기본 타임피커 설정
        alarm_timepicker = findViewById(R.id.time_picker);
        alarm_timepicker.setIs24HourView(true);

        // 알람 Receiver 인텐트 생성
        alarmIntent = new Intent(mContext, AlarmReceiver.class);

        // Button Alarm ON
        Button alarm_on = findViewById(R.id.btn_alarmStart);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setAlarm(mContext);
            }
        });

        Button alarm_off = findViewById(R.id.btn_alarmFinish);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseAlarm(mContext);
            }
        });

        NotificationHelper notificationHelper = new NotificationHelper(mContext);
        notificationHelper.cancelNotification(mContext,0);
        startForegroundService(alarmIntent);
    }

    private void setAlarm(Context context) {
        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        // calendar에 시간 셋팅
        if (Build.VERSION.SDK_INT < 23) {
            // 시간 가져옴
            getHourTimePicker = alarm_timepicker.getCurrentHour();
            getMinuteTimePicker = alarm_timepicker.getCurrentMinute();
        } else {
            // 시간 가져옴
            getHourTimePicker = alarm_timepicker.getHour();
            getMinuteTimePicker = alarm_timepicker.getMinute();
        }

        // 현재 지정된 시간으로 알람 시간 설정
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, getHourTimePicker);
        calendar.set(Calendar.MINUTE, getMinuteTimePicker);
        calendar.set(Calendar.SECOND, 0);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("set_hour", getHourTimePicker);
        editor.putInt("set_min", getMinuteTimePicker);
        editor.putString("state", "ALARM_ON");
        editor.commit();

        // reveiver에 string 값 넘겨주기
        alarmIntent.putExtra("state", "ALARM_ON");

        // receiver를 동작하게 하기 위해 PendingIntent의 인스턴스를 생성할 때, getBroadcast 라는 메소드를 사용
        // requestCode는 나중에 Alarm을 해제 할때 어떤 Alarm을 해제할지를 식별하는 코드
        pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long currentTime = System.currentTimeMillis(); // 현재 시간
        //long triggerTime = SystemClock.elapsedRealtime() + 1000*60;
        long triggerTime = calendar.getTimeInMillis(); // 알람을 울릴 시간
        long interval = 1000 * 60 * 60 * 24; // 1일치 인터벌(하루 시간)

        while (currentTime > triggerTime) { // 현재 시간보다 작다면
            triggerTime += interval; // 다음날 울리도록 처리
        }
        Log.e(TAG, "set Alarm : " + getHourTimePicker + " 시 " + getMinuteTimePicker + "분");

        // 알림 세팅 : AlarmManager 인스턴스에서 set 메소드를 실행시키는 것은 단발성 Alarm을 생성하는 것
        // RTC_WAKEUP : UTC 표준시간을 기준, 명시적 시간에 intent를 발생, 디바이스를 깨움
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                // 알람셋팅
                alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {  // 23 이상
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            //alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerTime,pendingIntent);
            //알람 매니저를 통한 반복알람 설정
            //alarm_manager.setRepeating(AlarmManager.RTC, triggerTime, interval, pendingIntent);
            // interval : 다음 알람이 울리기까지의 시간 간격
        }

        // Unable to find keycodes for AM and PM.
        if (getHourTimePicker > 12) {
            am_pm = "오후";
            getHourTimePicker = getHourTimePicker - 12;
        } else {
            am_pm = "오전";
        }
        textView.setText("알람 예정 시간 : " + am_pm + " " + getHourTimePicker + "시 " + getMinuteTimePicker + "분");
    }

    public void releaseAlarm(Context context) {
        Log.e(TAG, "unregisterAlarm");

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("state", "ALARM_OFF");
        editor.commit();

        // 알람매니저 취소
        alarm_manager.cancel(pendingIntent);
        alarmIntent.putExtra("state", "ALARM_OFF");

        // 알람 취소
        sendBroadcast(alarmIntent);

        Toast.makeText(this, "Alarm 종료", Toast.LENGTH_SHORT).show();
        textView.setText("");
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }

}
