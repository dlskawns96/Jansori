package com.a.term_timetable;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class CourseFragment extends Fragment {
    private TextView mon[] = new TextView[11];
    private TextView tue[] = new TextView[11];
    private TextView wed[] = new TextView[11];
    private TextView thu[] = new TextView[11];
    private TextView fri[] = new TextView[11];


    private Button addButton;

    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (View) inflater.inflate(R.layout.fragmaent_course, container, false);

        helper = new MySQLiteOpenHelper(getContext(), "courseInput.db", null, 1);
        addButton = rootView.findViewById(R.id.course_add);
        mon[0] = rootView.findViewById(R.id.mon1);
        mon[1] = rootView.findViewById(R.id.mon2);
        mon[2] = rootView.findViewById(R.id.mon3);
        mon[3] = rootView.findViewById(R.id.mon4);
        mon[4] = rootView.findViewById(R.id.mon5);
        mon[5] = rootView.findViewById(R.id.mon6);
        mon[6] = rootView.findViewById(R.id.mon7);
        mon[7] = rootView.findViewById(R.id.mon8);
        mon[8] = rootView.findViewById(R.id.mon9);
        mon[9] = rootView.findViewById(R.id.mon10);
        mon[10] = rootView.findViewById(R.id.mon11);

        tue[0] = rootView.findViewById(R.id.tue1);
        tue[1] = rootView.findViewById(R.id.tue2);
        tue[2] = rootView.findViewById(R.id.tue3);
        tue[3] = rootView.findViewById(R.id.tue4);
        tue[4] = rootView.findViewById(R.id.tue5);
        tue[5] = rootView.findViewById(R.id.tue6);
        tue[6] = rootView.findViewById(R.id.tue7);
        tue[7] = rootView.findViewById(R.id.tue8);
        tue[8] = rootView.findViewById(R.id.tue9);
        tue[9] = rootView.findViewById(R.id.tue10);
        tue[10] = rootView.findViewById(R.id.tue11);

        wed[0] = rootView.findViewById(R.id.wed1);
        wed[1] = rootView.findViewById(R.id.wed2);
        wed[2] = rootView.findViewById(R.id.wed3);
        wed[3] = rootView.findViewById(R.id.wed4);
        wed[4] = rootView.findViewById(R.id.wed5);
        wed[5] = rootView.findViewById(R.id.wed6);
        wed[6] = rootView.findViewById(R.id.wed7);
        wed[7] = rootView.findViewById(R.id.wed8);
        wed[8] = rootView.findViewById(R.id.wed9);
        wed[9] = rootView.findViewById(R.id.wed10);
        wed[10] = rootView.findViewById(R.id.wed11);

        thu[0] = rootView.findViewById(R.id.thu1);
        thu[1] = rootView.findViewById(R.id.thu2);
        thu[2] = rootView.findViewById(R.id.thu3);
        thu[3] = rootView.findViewById(R.id.thu4);
        thu[4] = rootView.findViewById(R.id.thu5);
        thu[5] = rootView.findViewById(R.id.thu6);
        thu[6] = rootView.findViewById(R.id.thu7);
        thu[7] = rootView.findViewById(R.id.thu8);
        thu[8] = rootView.findViewById(R.id.thu9);
        thu[9] = rootView.findViewById(R.id.thu10);
        thu[10] = rootView.findViewById(R.id.thu11);

        fri[0] = rootView.findViewById(R.id.fri1);
        fri[1] = rootView.findViewById(R.id.fri2);
        fri[2] = rootView.findViewById(R.id.fri3);
        fri[3] = rootView.findViewById(R.id.fri4);
        fri[4] = rootView.findViewById(R.id.fri5);
        fri[5] = rootView.findViewById(R.id.fri6);
        fri[6] = rootView.findViewById(R.id.fri7);
        fri[7] = rootView.findViewById(R.id.fri8);
        fri[8] = rootView.findViewById(R.id.fri9);
        fri[9] = rootView.findViewById(R.id.fri10);
        fri[10] = rootView.findViewById(R.id.fri11);

        table_log();
        table_reset();
        table_read();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("timetable", "Checking");
                Intent i = new Intent(getActivity(), CourseInput.class);
                i.putExtra("REQUEST_CODE", 1111);
                startActivityForResult(i,1111);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 55) {
            Bundle course_info = data.getExtras();

            if (dupl_check(course_info)) {
                String course_title;
                String course_prof;
                String start_time;
                String end_time;
                String day;


                course_title = course_info.getString("course");
                course_prof = course_info.getString("prof");
                start_time = course_info.getString("start");
                end_time = course_info.getString("end");
                day = course_info.getString("day");
                String[] day_temp = day.split(",");

                Toast.makeText(getContext(), course_title + " " + course_prof + " " + start_time + " " + end_time, Toast.LENGTH_LONG).show();


                for (int i = 0; i < day_temp.length; i++) {
                    insert(course_title, course_prof, start_time, end_time, day_temp[i]);
                }
            }
            table_log();
        }
        else if(resultCode == 9999){

        }
    }

    public void insert(String course_title, String course_prof, String start_time, String end_time, String day_str){ // Insert Function is to put data row to DB put (key, values) and insert (table, null avail, all values)
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("title", course_title);
        values.put("prof", course_prof);
        values.put("startTime", start_time);
        values.put("endTime", end_time);
        values.put("day", Integer.parseInt(day_str));
        Log.i("dbCheck", "in insert: " + course_title + " " + course_prof  + " " + start_time  + " " + end_time  + " " + day_str);

        db.insert("course", null, values);

        table_read();
    }

    public void delete(String name){ // Delete Function is to delete data row in DB , delete(table, find in name key, the value we got)
        db = helper.getWritableDatabase();
        db.delete("course", "title=?", new String[]{name});
        Log.i("db2", name + "정상적으로 삭제 되었습니다.");
    }


    public void table_log() {
        db = helper.getWritableDatabase();
        Cursor c = db.query("course", null, null, null, null, null, null);

        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("title"));
            String prof = c.getString(c.getColumnIndex("prof"));
            String start = c.getString(c.getColumnIndex("startTime"));
            String end = c.getString(c.getColumnIndex("endTime"));
            int day = c.getInt(c.getColumnIndex("day"));

            Log.i("dbCheck", "title: " + title + " prof: " + prof + " start : " + start + " end: " + end + " Day: " + day);
        }
    }

    public void table_read() {
        db = helper.getWritableDatabase();
        Cursor c = db.query("course", null, null, null, null, null, null);

        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex("title"));
            String prof = c.getString(c.getColumnIndex("prof"));
            String start = c.getString(c.getColumnIndex("startTime"));
            String end = c.getString(c.getColumnIndex("endTime"));
            int day = c.getInt(c.getColumnIndex("day"));

            table_input(title, prof, start, end, day);
        }
    }


    public boolean dupl_check(Bundle b){

        String course_title;
        String course_prof;
        String start_time;
        String end_time;
        String day;


        course_title = b.getString("course");
        course_prof = b.getString("prof");
        start_time = b.getString("start");
        end_time = b.getString("end");
        day = b.getString("day");
        String[] day_temp = day.split(",");

        int start_input = Integer.parseInt(start_time.substring(0, start_time.indexOf(":")));
        int end_input = Integer.parseInt(end_time.substring(0, end_time.indexOf(":")));


        db = helper.getWritableDatabase();

        for (int i=0;i<day_temp.length;i++){
            Cursor c = db.query("course", null, null, null, null, null, null);

            while(c.moveToNext()){
                if(day_temp[i].equals(c.getString(c.getColumnIndex("day")))){
                    String srt_tmp = c.getString(c.getColumnIndex("startTime"));
                    int start = Integer.parseInt(srt_tmp.substring(0, srt_tmp.indexOf(":")));
                    String end_tmp = c.getString(c.getColumnIndex("endTime"));
                    int end = Integer.parseInt(end_tmp.substring(0, end_tmp.indexOf(":")));

                    if(start_input < end || end_input > start){
                        Toast.makeText(getActivity(), "겹치는 강의가 있습니다.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void table_reset(){
        for(int i=0;i<11;i++){
            mon[i].setText("가나다라마바사아");
            tue[i].setText("가나다라마바사아");
            wed[i].setText("가나다라마바사아");
            thu[i].setText("가나다라마바사아");
            fri[i].setText("가나다라마바사아");
        }
    }

    public void table_input(String title, String prof, String start_time, String end_time, int day) {
        int srt_hour = Integer.parseInt(start_time.substring(0, start_time.indexOf(":")));
        int end_hour = Integer.parseInt(end_time.substring(0, end_time.indexOf(":")));
        Log.i("dbCheck", "Start Hour: " + srt_hour + "End Hour: " + end_hour + title + " " + prof);
        int i = srt_hour;


        switch (day) {
            case 1:
                for (int j = 0; j < 11; j++) {
                    if (i == j + 9 && i <= end_hour) {
                        mon[j].setText(title + "\n" + prof);
                        mon[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                        i += 1;
                    }

                    else
                        mon[j].setText("가나다라마바사아");
                }
                break;
            case 2:
                for (int j = 0; j < 11; j++) {
                    if (i == j + 9 && i <= end_hour) {
                        tue[j].setText(title + "\n" + prof);
                        tue[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));

                        Log.i("dbCheck", tue[j].getText().toString());
                        i += 1;
                    }

                    else
                        tue[j].setText("가나다라마바사아");
                }
                break;
            case 3:
                for (int j = 0; j < 11; j++) {
                    if (i == j + 9 && i <= end_hour) {
                        wed[j].setText(title + "\n" + prof);
                        wed[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        i += 1;
                    }

                    else
                        wed[j].setText("가나다라마바사아");
                }
                break;
            case 4:
                for (int j = 0; j < 11; j++) {
                    if (i == j + 9 && i <= end_hour) {
                        thu[j].setText(title + "\n" + prof);
                        thu[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        i += 1;
                    }
                    else
                        thu[j].setText("가나다라마바사아");
                }
                break;
            case 5:
                for (int j = 0; j < 11; j++) {
                    if (i == j + 9 && i <= end_hour) {
                        fri[j].setText(title + "\n" + prof);
                        fri[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
                        i += 1;
                    }

                    else
                        fri[j].setText("가나다라마바사아");

                }
                break;
            default:
                break;
        }




//        do{
//            switch (day) {
//                case 1:
//                    for(int j=0;j<11;j++){
//                        if(i == j+9){
//                            mon[j].setText(title + "\n" + prof);
//                            mon[j].setBackgroundColor(getActivity().getResources().getColor(R.color.colorAccent));
//                        }
//                        i+= 1;
//                        if (i>end_hour)
//                            break;
//                    }
//                case 2:
//
//
//
//                    switch (i) {
//                        case 9:
//                            mon[0].setText(title + "\n" + prof);
//                            mon[0].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[0].resizeText();
//                            break;
//                        case 10:
//                            mon[1].setText(title + "\n" + prof);
//                            mon[1].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[1].resizeText();
//                            break;
//                        case 11:
//                            mon[2].setText(title + "\n" + prof);
//                            mon[2].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[2].resizeText();
//                            break;
//                        case 12:
//                            mon[3].setText(title + "\n" + prof);
//                            mon[3].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[3].resizeText();
//                            break;
//                        case 13:
//                            mon[4].setText(title + "\n" + prof);
//                            mon[4].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[4].resizeText();
//                            break;
//                        case 14:
//                            mon[5].setText(title + "\n" + prof);
//                            mon[5].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[5].resizeText();
//                            break;
//                        case 15:
//                            mon[6].setText(title + "\n" + prof);
//                            mon[6].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[6].resizeText();
//                            break;
//                        case 16:
//                            mon[7].setText(title + "\n" + prof);
//                            mon[7].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[7].resizeText();
//                            break;
//                        case 17:
//                            mon[8].setText(title + "\n" + prof);
//                            mon[8].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[8].resizeText();
//                            break;
//                        case 18:
//                            mon[9].setText(title + "\n" + prof);
//                            mon[9].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            mon[9].resizeText();
//                            break;
//                        case 19:
//                            mon[10].setText(title + "\n" + prof);
//                            mon[10].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//                    break;
//                case 2:
//                    switch (i) {
//                        case 9:
//                            tue[0].setText(title + "\n" + prof);
//                            tue[0].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 10:
//                            tue[1].setText(title + "\n" + prof);
//                            tue[1].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            Log.i("dbcheck", "case check");
//                            break;
//                        case 11:
//                            tue[2].setText(title + "\n" + prof);
//                            tue[2].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 12:
//                            tue[3].setText(title + "\n" + prof);
//                            tue[3].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 13:
//                            tue[4].setText(title + "\n" + prof);
//                            tue[4].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 14:
//                            tue[5].setText(title + "\n" + prof);
//                            tue[5].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 15:
//                            tue[6].setText(title + "\n" + prof);
//                            tue[6].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 16:
//                            tue[7].setText(title + "\n" + prof);
//                            tue[7].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 17:
//                            tue[8].setText(title + "\n" + prof);
//                            tue[8].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 18:
//                            tue[9].setText(title + "\n" + prof);
//                            tue[9].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 19:
//                            tue[10].setText(title + "\n" + prof);
//                            tue[10].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//                    break;
//                case 3:
//                    switch (i) {
//                        case 9:
//                            wed[0].setText(title + "\n" + prof);
//                            wed[0].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 10:
//                            wed[1].setText(title + "\n" + prof);
//                            wed[1].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 11:
//                            wed[2].setText(title + "\n" + prof);
//                            wed[2].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 12:
//                            wed[3].setText(title + "\n" + prof);
//                            wed[3].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 13:
//                            wed[4].setText(title + "\n" + prof);
//                            wed[4].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 14:
//                            wed[5].setText(title + "\n" + prof);
//                            wed[5].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 15:
//                            wed[6].setText(title + "\n" + prof);
//                            wed[6].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 16:
//                            wed[7].setText(title + "\n" + prof);
//                            wed[7].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 17:
//                            wed[8].setText(title + "\n" + prof);
//                            wed[8].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 18:
//                            wed[9].setText(title + "\n" + prof);
//                            wed[9].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 19:
//                            wed[10].setText(title + "\n" + prof);
//                            wed[10].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//                    break;
//                case 4:
//                    switch (i) {
//                        case 9:
//                            thu[0].setText(title + "\n" + prof);
//                            thu[0].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 10:
//                            thu[1].setText(title + "\n" + prof);
//                            thu[1].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 11:
//                            thu[2].setText(title + "\n" + prof);
//                            thu[2].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 12:
//                            thu[3].setText(title + "\n" + prof);
//                            thu[3].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 13:
//                            thu[4].setText(title + "\n" + prof);
//                            thu[4].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 14:
//                            thu[5].setText(title + "\n" + prof);
//                            thu[5].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 15:
//                            thu[6].setText(title + "\n" + prof);
//                            thu[6].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 16:
//                            thu[7].setText(title + "\n" + prof);
//                            thu[7].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 17:
//                            thu[8].setText(title + "\n" + prof);
//                            thu[8].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 18:
//                            thu[9].setText(title + "\n" + prof);
//                            thu[9].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 19:
//                            thu[10].setText(title + "\n" + prof);
//                            thu[10].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//                    break;
//                case 5:
//                    switch (i) {
//                        case 9:
//                            fri[0].setText(title + "\n" + prof);
//                            fri[0].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 10:
//                            fri[1].setText(title + "\n" + prof);
//                            fri[1].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 11:
//                            fri[2].setText(title + "\n" + prof);
//                            fri[2].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 12:
//                            fri[3].setText(title + "\n" + prof);
//                            fri[3].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 13:
//                            fri[4].setText(title + "\n" + prof);
//                            fri[4].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 14:
//                            fri[5].setText(title + "\n" + prof);
//                            fri[5].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 15:
//                            fri[6].setText(title + "\n" + prof);
//                            fri[6].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 16:
//                            fri[7].setText(title + "\n" + prof);
//                            fri[7].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 17:
//                            fri[8].setText(title + "\n" + prof);
//                            fri[8].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 18:
//                            fri[9].setText(title + "\n" + prof);
//                            fri[9].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 19:
//                            fri[10].setText(title + "\n" + prof);
//                            fri[10].setBackgroundColor(getActivity().getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//                    break;
//            }
//            i += 1;
//            Log.i("dbCheck", "day : " + day + " i = " + i);
//        } while (i<=end_hour);
    }
}
