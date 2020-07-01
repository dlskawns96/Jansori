package com.a.term_timetable;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class CourseInput extends Activity {

    Button allow;
    Button deny;
    EditText course;
    EditText prof;
    EditText start_time;
    EditText end_time;

    CheckBox mon;
    CheckBox tue;
    CheckBox wed;
    CheckBox thu;
    CheckBox fri;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_input);;

        allow = findViewById(R.id.allow_button);
        deny = findViewById(R.id.cancel_button);
        course = findViewById(R.id.input_course);
        prof = findViewById(R.id.input_prof);
        start_time = findViewById(R.id.input_time);
        end_time = findViewById(R.id.input_end);
        mon = findViewById(R.id.mon_check);
        tue = findViewById(R.id.tue_check);
        wed = findViewById(R.id.wed_check);
        thu = findViewById(R.id.thu_check);
        fri = findViewById(R.id.fri_check);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.9);
        int height = (int) (display.getHeight() * 0.5);

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(essential_check()){
                    case 3:
                        Toast.makeText(getApplicationContext(), "Course title and time must be essential", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "At least 1 day must be essential", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        if (time_check()){
                            Intent intent = getIntent();
                            Bundle new_bundle = new Bundle();
                            new_bundle.putString("course", course.getText().toString());
                            new_bundle.putString("prof", prof.getText().toString());
                            new_bundle.putString("start", start_time.getText().toString());
                            new_bundle.putString("end", end_time.getText().toString());
                            String day = day_to_int();
                            Toast.makeText(getApplicationContext(), day, Toast.LENGTH_LONG).show();
                            new_bundle.putString("day",day);

                            intent.putExtras(new_bundle);
                            setResult(55, intent);
                            finish();
                        }
                }
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(9999);
                finish();
            }
        });
    }
    public String day_to_int(){
        String day = "0,";

        if (mon.isChecked())
            day = day + "1,";
        if (tue.isChecked())
            day = day + "2,";
        if (wed.isChecked())
            day = day + "3,";
        if (thu.isChecked())
            day = day + "4,";
        if (fri.isChecked())
            day = day + "5";
        day = day.substring(2);



        return day;
    }


    public boolean time_check(){
        String srt_temp = start_time.getText().toString();
        int srt_split = srt_temp.indexOf(":");

        if (srt_split == -1){
            Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
            return false;
        }
        String srt_hour = srt_temp.substring(0, srt_split);
        String srt_min = srt_temp.substring(srt_split + 1);

        for(int i = 0;i<srt_hour.length();i++){
            if(srt_hour.charAt(i) < 48 || srt_hour.charAt(i) > 58){
                Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        for(int i = 0;i<srt_min.length();i++){
            if(srt_min.charAt(i) < 48 || srt_min.charAt(i) > 58){
                Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        int srt = Integer.parseInt(srt_hour);

        String end_temp = end_time.getText().toString();
        int end_split = end_temp.indexOf(":");
        if(end_split == -1){
            Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
            return false;
        }
        String end_hour = end_temp.substring(0, end_split);
        String end_min = end_temp.substring(end_split + 1);

        for(int i = 0;i<end_hour.length();i++){
            if(end_hour.charAt(i) < 48 || end_hour.charAt(i) > 58){
                Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        for(int i = 0;i<end_min.length();i++){
            if(end_min.charAt(i) < 48 || end_min.charAt(i) > 58){
                Toast.makeText(getApplicationContext(), "시간 포맷에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        int end = Integer.parseInt(end_hour);

        if (srt < 12)
            if (srt < 8)
                srt+= 12;
        if (end < 12)
            if (end < 9)
            end += 12;
        if (srt > 19 || end > 19 || srt<9 ||end <9){
            Toast.makeText(getApplicationContext(), "시간표 시간에 맞게 입력해주세요. \"##:##\"", Toast.LENGTH_LONG).show();
            return false;
        }

        if (srt >= end){
            if(srt > end){
                Toast.makeText(getApplicationContext(), "시작시간이 더 앞이어야 합니다.", Toast.LENGTH_LONG).show();
                return false;
            }
            else{
                if(Integer.parseInt(srt_min) >= Integer.parseInt(end_min)){
                    Toast.makeText(getApplicationContext(), "시작시간이 더 앞이어야 합니다.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        if (Integer.parseInt(srt_min)<0 || Integer.parseInt(srt_min) > 60 || Integer.parseInt(end_min) < 0 || Integer.parseInt(end_min) > 60){
            Toast.makeText(getApplicationContext(), "정확한 분을 입력해주세요.", Toast.LENGTH_LONG).show();
            return false;
        }

        start_time.setText(srt + ":" + srt_min);
        end_time.setText(end + ":" + end_min);


        return true;
    }

    public int essential_check(){
      if (TextUtils.isEmpty(start_time.getText()) || TextUtils.isEmpty(end_time.getText()) || TextUtils.isEmpty(course.getText())){
            return 3;
      }
      else {
              if(mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked())
                  return 1;
              else
                  return 2;
      }
    }
}

