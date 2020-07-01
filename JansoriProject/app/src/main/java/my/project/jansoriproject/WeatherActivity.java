package my.project.jansoriproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

import my.project.jansoriproject.weather.DayWeatherAdapter;
import my.project.jansoriproject.weather.DayWeatherItem;
import my.project.jansoriproject.weather.TimeWeatherAdapter;
import my.project.jansoriproject.weather.TimeWeatherItem;

public class WeatherActivity extends AppCompatActivity {
    TextView locationNameText;
    ImageView weatherImage;
    TextView currentTempText;
    TextView weatherName;
    TextView minMaxFeel;
    TextView currentTime;

    RecyclerView timeWeatherRecycler;
    RecyclerView dayWeatherRecycler;

    HashMap currentWeatherMap;
    HashMap forecastMap;
    HashMap dayMap;

    String address;
    double latitude;
    double longitude;

    /**
     * 이 액티비티의 위젯 초기
     */
    private void initWidgets () {
        locationNameText = findViewById(R.id.locationName);
        weatherImage = findViewById(R.id.weatherImage);
        currentTempText = findViewById(R.id.currentTemp);
        weatherName = findViewById(R.id.weatherName);
        minMaxFeel = findViewById(R.id.min_max_feel);
        currentTime = findViewById(R.id.currentTime);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initWidgets();
        MainActivity.LocationInfo locationInfo = ((MainActivity)MainActivity.mainContext).getLocationInfo();
        setLocationInfo(locationInfo);
        // 경위도, 주소 메인액티비티에서 받아오기
        //Log.d("DDD", "" + currentWeatherMap.toString());

        WeatherMaps weatherMaps = ((MainActivity)MainActivity.mainContext).getWeatherMaps();

        currentWeatherMap = weatherMaps.currentMap;
        forecastMap = weatherMaps.hourlyMap;
        dayMap = weatherMaps.dailyMap;

        setLocationNameText(address);

        // 현재 날씨 정보 세팅
        setCurrentWeatherItem(currentWeatherMap);

        // 예보 날씨 세팅
        setTimeWeatherItems(forecastMap);

        // 일별 날씨 세팅
        setDayWeatherItems(dayMap);
    }

    private void setLocationInfo(MainActivity.LocationInfo locationInfo) {
        address = locationInfo.address;
        latitude = locationInfo.latitude;
        longitude = locationInfo.longitude;
    }

    private void setLocationNameText(String address) {
        locationNameText.setText(address);
    }



    private void setCurrentWeatherItem(HashMap<String, String> currentMap) {
        int imageID = getResources().getIdentifier("@drawable/image_" + currentMap.get("icon"), "drawable", this.getPackageName());
        currentTempText.setText(getString(R.string.weatherActivity_currentTemp, currentMap.get("tc") + "°"));
        weatherImage.setImageResource(imageID);
        weatherName.setText("" + currentMap.get("name"));
        minMaxFeel.setText("체감 온도 " + currentMap.get("feel") + "°");
        currentTime.setText(""+currentMap.get("time"));
    }

    private void setDayWeatherItems(HashMap<String, String> dayMap) {
        dayWeatherRecycler = findViewById(R.id.dayWeatherRecycler);
        dayWeatherRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DayWeatherItem item;
        ArrayList<DayWeatherItem> itemArrayList = new ArrayList<>();
        for(int i = 0; i < 7; i++) {
            item = new DayWeatherItem();
            item.setTime(dayMap.get("time"+i));
            item.setSkyCode(dayMap.get("icon"+i));
            item.setTemp(dayMap.get("tc"+i));
            itemArrayList.add(item);
        }

        DayWeatherAdapter dayWeatherAdapter = new DayWeatherAdapter(itemArrayList);
        dayWeatherRecycler.setAdapter(dayWeatherAdapter);
    }

    private void setTimeWeatherItems(HashMap<String, String> forecastMap) {
        timeWeatherRecycler = findViewById(R.id.timeWeatherRecycler);
        timeWeatherRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        TimeWeatherItem item;
        ArrayList<TimeWeatherItem> itemArrayList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            item = new TimeWeatherItem();
            item.setTime(forecastMap.get("time"+i));
            item.setSkyCode(forecastMap.get("icon"+i));
            item.setTemp(forecastMap.get("temp"+i));
            itemArrayList.add(item);
        }

        TimeWeatherAdapter timeWeatherAdapter = new TimeWeatherAdapter(itemArrayList);
        timeWeatherRecycler.setAdapter(timeWeatherAdapter);
    }
}
