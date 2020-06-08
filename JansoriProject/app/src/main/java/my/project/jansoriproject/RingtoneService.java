package my.project.jansoriproject;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class RingtoneService extends Service {
    private static final String TAG = RingtoneService.class.getSimpleName();
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서) 데이터를 주고받을 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.i(TAG, "RingtoneService Started");

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.createNotification("알람시작","알람음이 재생됩니다.");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand startID === "+startId); // 계속 증가되는 값

        String getState = intent.getExtras().getString("state");
        Log.e("TAG","onStartCommand getState : " + getState);

        switch (getState) {
            case "ALARM_ON":
                if(mediaPlayer == null){
                    mediaPlayer = MediaPlayer.create(this, R.raw.cla);
                    mediaPlayer.start();

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.e(TAG, "mediaPlayer Completed!");
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                            mediaPlayer.release();
                        }
                    });
                }
                break;
            case "ALARM_OFF":
                Log.e(TAG, "onStartCommand Stoped!");

                if (mediaPlayer != null) {
                    if(mediaPlayer.isPlaying() == true){
                        mediaPlayer.stop();
                        mediaPlayer.release(); // 자원 반환
                        mediaPlayer = null;
                    }
                }
                stopSelf();
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Action Service Ended");
        super.onDestroy();
        stopForeground(true);
    }
}
