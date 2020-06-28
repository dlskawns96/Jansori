package my.project.jansoriproject.Alarm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import my.project.jansoriproject.R;

public class AlarmMainActivity extends AppCompatActivity {
    FloatingActionButton addAlarmFab;

    private RecyclerView alarmListRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<AlarmItem> alarmItemArrayList;
    AlarmRecyclerAdapter alarmRecyclerAdapter;
    private int alarmCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_main);

        alarmListRecyclerView = findViewById(R.id.AlarmListRecycler);
        alarmListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        alarmListRecyclerView.setLayoutManager(layoutManager);

        addAlarmFab = findViewById(R.id.fabAddAlarm);
        addAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmSetActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            int hour = data.getIntExtra("Hour", 99);
            int minute = data.getIntExtra("Minute", 99);
            ArrayList<String> activeDays = data.getStringArrayListExtra("ActiveDays");
            Log.i("TAG", "Received from Intent : " + data.getIntExtra("Hour", 99) + data.getIntExtra("Minute", 99));
            if (alarmCount == 0) {
                alarmItemArrayList = new ArrayList<>();
                AlarmItem alarmItem = new AlarmItem(hour, minute, activeDays);
                alarmItemArrayList.add(alarmItem);
                alarmRecyclerAdapter = new AlarmRecyclerAdapter(alarmItemArrayList);
                alarmListRecyclerView.setAdapter(alarmRecyclerAdapter);
            }
            alarmRecyclerAdapter.notifyDataSetChanged();
        }
    }
}