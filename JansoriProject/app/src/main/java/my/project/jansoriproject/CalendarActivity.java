package my.project.jansoriproject;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import my.project.jansoriproject.calendar.EventDecorator;
import my.project.jansoriproject.calendar.EventItem;
import my.project.jansoriproject.calendar.OneDayDecorator;
import my.project.jansoriproject.calendar.SaturdayDecorator;
import my.project.jansoriproject.calendar.SundayDecorator;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class CalendarActivity extends AppCompatActivity {

    String time, kcal, menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    MaterialCalendarView materialCalendarView;
    private final static String TAG = "CalendarActivity";

    EditText title, location, description, year, month, day, startHour, startMinue, endHour, endMinute;
    RadioGroup start,end;
    Boolean startAm, endAm = true;
    EventItem eventItem;
    ArrayList<String> result = new ArrayList<>();
    HashMap<String, ArrayList<EventItem>> day_event = new HashMap<>();
    RecyclerView eventRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.calendar_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String eventId = ""+Year+Month+Day;
                materialCalendarView.clearSelection();

                if (!day_event.isEmpty()) {
                    if (day_event.containsKey(eventId)) {
                        showEvent(eventId);
                    } else {
                        Toast.makeText(CalendarActivity.this, "Schedule Empty!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CalendarActivity.this, "Schedule Empty!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 메뉴 버튼 추가하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    // 툴바 뒤로가기 버튼 & 메뉴 버튼 활성화
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getTitle().toString().equals("일정 추가")){
            addEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 일정 추가
    public void addEvent() {

        final View dialogView = getLayoutInflater().inflate(R.layout.new_event_dialog, null);

        title = (EditText) dialogView.findViewById(R.id.editText_title);
        location = (EditText) dialogView.findViewById(R.id.editText_location);
        description = (EditText) dialogView.findViewById(R.id.editText_decsription);
        year = (EditText) dialogView.findViewById(R.id.editText_year);
        month = (EditText) dialogView.findViewById(R.id.editText_month);
        day = (EditText) dialogView.findViewById(R.id.editText_day);
        startHour = (EditText) dialogView.findViewById(R.id.editText_startHour);
        startMinue = (EditText) dialogView.findViewById(R.id.editText_startMinute);
        endHour = (EditText) dialogView.findViewById(R.id.editText_endHour);
        endMinute = (EditText) dialogView.findViewById(R.id.editText_endMinute);

        start = (RadioGroup) dialogView.findViewById(R.id.radiogroup_start);
        end = (RadioGroup) dialogView.findViewById(R.id.radiogroup_end);

        start.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton_startAm) {
                    startAm = true;
                } else {
                    startAm = false;
                }
            }
        });

        end.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton_endAm) {
                    endAm = true;
                } else {
                    endAm = false;
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        setEventItem();

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            Calendar calendar = Calendar.getInstance();

                            ArrayList<CalendarDay> dates = new ArrayList<>();

                            /*특정날짜 달력에 점표시해주는곳*/
                            /*월은 0이 1월 년,일은 그대로*/
                            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
                            for (int i = 0; i < result.size(); i++) {
                                String[] time = result.get(i).split(",");
                                int year = Integer.parseInt(time[0]);
                                int month = Integer.parseInt(time[1]);
                                int dayy = Integer.parseInt(time[2]);

                                calendar.set(year, month - 1, dayy);
                                CalendarDay day = CalendarDay.from(calendar);
                                dates.add(day);
                            }

                            materialCalendarView.addDecorator(new EventDecorator(Color.RED, dates, CalendarActivity.this));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //일정 등록
    public void setEventItem() {
        eventItem = new EventItem();

        eventItem.setTitle(title.getText().toString().trim());
        eventItem.setLocation(location.getText().toString().trim());
        eventItem.setDescription(description.getText().toString().trim());
        eventItem.setYear(Integer.parseInt(year.getText().toString()));
        eventItem.setMonth(Integer.parseInt(month.getText().toString()));
        eventItem.setDay(Integer.parseInt(day.getText().toString()));

        if(startAm) {
            eventItem.setStartHour(Integer.parseInt(startHour.getText().toString()));
        } else {
            eventItem.setStartHour(Integer.parseInt(startHour.getText().toString())+12);
        }
        eventItem.setStartMinute(Integer.parseInt(startMinue.getText().toString()));

        if(endAm) {
            eventItem.setEndHour(Integer.parseInt(endHour.getText().toString()));
        } else {
            eventItem.setEndHour(Integer.parseInt(endHour.getText().toString())+12);
        }
        eventItem.setEndMinute(Integer.parseInt(endMinute.getText().toString()));

        String eventId = ""+eventItem.getYear()+eventItem.getMonth()+eventItem.getDay();

        if (day_event.isEmpty()) {
            ArrayList<EventItem> items = new ArrayList<>();
            items.add(eventItem);
            day_event.put(eventId, items);
            result.add(eventItem.getYear()+","+eventItem.getMonth()+","+eventItem.getDay());
        } else {
            if (day_event.containsKey(eventId)) {
                ArrayList<EventItem> items = day_event.get(eventId);
                items.add(eventItem);
                day_event.put(eventId, items);
            } else {
                ArrayList<EventItem> items = new ArrayList<>();
                items.add(eventItem);
                day_event.put(eventId, items);
                result.add(eventItem.getYear()+","+eventItem.getMonth()+","+eventItem.getDay());
            }
        }
    }

    // 그 날 일정 보여주기
    public void showEvent(final String eventId) {
        View dialogView = getLayoutInflater().inflate(R.layout.day_event, null);

        eventRecycler = (RecyclerView) dialogView.findViewById(R.id.dayEvent);
        eventRecycler.setLayoutManager(new LinearLayoutManager(dialogView.getContext()));
        CalendarEventAdapter adapter = new CalendarEventAdapter(dialogView.getContext(), day_event.get(eventId));

        eventRecycler.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(dialogView)
                .setPositiveButton(R.string.out, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
