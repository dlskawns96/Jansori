package my.project.jansoriproject;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowNotification extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shownotification);
        context = getBaseContext();

        TextView textView = (TextView) findViewById(R.id.txt_notiview);
        textView.setText("Notification reply view");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.cancelNotification(context, 0);
    }
}
