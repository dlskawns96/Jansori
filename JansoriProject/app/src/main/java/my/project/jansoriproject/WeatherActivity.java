package my.project.jansoriproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    TextView locationNameText;
    ImageView weatherImage;
    TextView currentTempText;
    TextView weatherName;
    TextView minMaxFeel;
    TextView currentTime;

    RecyclerView timeWeatherRecycler;
    RecyclerView dayWeatherRecycler;

    HashMap valueMap = new HashMap<String, String>();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initWidgets();

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        locationNameText.setText(address);
        WeatherMaps weatherMaps = getWeatherData(latitude, longitude);
        HashMap currentWeatherMap = weatherMaps.currentMap;
        HashMap forecastMap = weatherMaps.hourlyMap;
        HashMap dayMap = weatherMaps.dailyMap;
        Log.d("DDD", "" + currentWeatherMap.toString());

        // 현재 날씨 정보 세팅
        currentTempText.setText(getString(R.string.weatherActivity_currentTemp, currentWeatherMap.get("tc") + "°"));
        int imageID = getResources().getIdentifier("@drawable/image_" + currentWeatherMap.get("icon"), "drawable", this.getPackageName());
        weatherImage.setImageResource(imageID);
        weatherName.setText("" + currentWeatherMap.get("name"));
        minMaxFeel.setText("체감 온도 " + currentWeatherMap.get("feel") + "°");
        currentTime.setText(""+currentWeatherMap.get("time"));
        Log.d("DDD", "" + imageID);

        // 예보 날씨 세팅
        setTimeWeatherItems(forecastMap);

        // 일별 날씨 세팅
        setDayWeatherItems(dayMap);
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

    private WeatherMaps getWeatherData(Double lat, Double lon) {
        String[] strings = new String[2];
        strings[0] = Double.toString(lat);
        strings[1] = Double.toString(lon);

        AsyncHttpsConn asyncHttpsConn = new AsyncHttpsConn();
        try {
            return asyncHttpsConn.execute(strings).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

        void checkRunTimePermission(){

            //런타임 퍼미션 처리
            // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                // 2. 이미 퍼미션을 가지고 있다면
                // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


                // 3.  위치 값을 가져올 수 있음


            } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

                // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                    Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);


                } else {
                    // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

        }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        Log.d("Got", address.getSubLocality());
        return address.getSubLocality();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
