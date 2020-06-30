package com.a.term_timetable;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;

public class TimeTableNoti extends Fragment {

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;
    TextView widgetView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragment_tablewidget, container, false);
        helper = new MySQLiteOpenHelper(getContext(), "courseInput.db", null, 1);
        widgetView = rootView.findViewById(R.id.table_widget);
        widget_data();

        widgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(0);
            }
        });


        return rootView;
    }

    public void widget_data(){
        Calendar cal = Calendar.getInstance();
        String widget_temp = null;
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

        switch (dayOfWeek){
            case 1:
                widget_temp = "월요일 수업:";
                break;
            case 2:
                widget_temp = "화요일 수업:";
                break;
            case 3:
                widget_temp = "수요일 수업:";
                break;
            case 4:
                widget_temp = "목요일 수업:";
                break;
            case 5:
                widget_temp = "금요일 수업:";
                break;
            case 0:
            case 6:
                dayOfWeek = 1;
                widget_temp = "월요일 수업:";
                break;
        }


        db = helper.getWritableDatabase();
        Cursor c = db.query("course", null, null, null, null, null, null);
        int i = 1;
        while (c.moveToNext()) {
            int day = c.getInt(c.getColumnIndex("day"));
            if(day == dayOfWeek){
                String title = c.getString(c.getColumnIndex("title"));
                String prof = c.getString(c.getColumnIndex("prof"));
                String start = c.getString(c.getColumnIndex("startTime"));
                String end = c.getString(c.getColumnIndex("endTime"));

                widget_temp = widget_temp + "\n" + i + ". Title: " + title + " , Prof: " + prof +
                        "\n     Time: " + start + " ~ " + end;
                i += 1;
            }
        }
        widgetView.setText(widget_temp);
    }
}
