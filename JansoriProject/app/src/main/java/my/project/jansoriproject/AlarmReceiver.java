package my.project.jansoriproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    Context mContext;

    PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;
    SharedPreferences pref;
    String get_state;
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        pref = context.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        get_state = pref.getString("state", "");
        Log.e(TAG, "Alarm state : " + get_state);

        AlarmReceiverChk(context, intent);
    }

    private void AlarmReceiverChk(final Context context, final Intent intent) {
        Log.d(TAG, "Alarm Receiver started!");
        switch (get_state) {
            case "ALARM_ON":
                acquireCPUWakeLock(context, intent);
                // RingtoneService 서비스 intent 생성
                Intent serviceIntent = new Intent(mContext, RingtoneService.class);
                serviceIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                break;
            case "ALARM_OFF": // stopService 가 동작하지 않아서 startService 로 처리하고 나서....
                releaseCpuLock();
                Intent stopIntent = new Intent(context, RingtoneService.class);
                stopIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(stopIntent);
                } else {
                    context.startService(stopIntent);
                }
                break;
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void acquireCPUWakeLock(Context context, Intent intent) {
        // 꺼진 화면 깨우기
        if (wakeLock != null) {
            return;
        }
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire();
        Log.e("PushWakeLock", "Acquire cpu WakeLock = " + wakeLock);
    }

    private void releaseCpuLock() {
        Log.e("PushWakeLock", "Releasing cpu WakeLock = " + wakeLock);

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}