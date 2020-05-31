package my.project.jansoriproject;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    TextView locationNameText;
    ImageView weatherImage;
    TextView currentTempText;
    TextView rainChanceText;

    RecyclerView timeWeatherRecycler;

    HashMap valueMap = new HashMap<String, String>();
    GeocodeUtil geocodeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initWidgets();
        geocodeUtil = new GeocodeUtil(this);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        AsyncHttpsConn asyncHttpsConn = new AsyncHttpsConn();
        try {
            String[] results = getLocation(locationManager);
            valueMap = asyncHttpsConn.execute(results).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("태","" + valueMap.keySet());
        currentTempText.setText("" + valueMap.get("tc"));
        rainChanceText.setText("" + valueMap.get("sinceOntime"));
    }

    /**
     * 이 액티비티의 위젯 초기
     */
    private void initWidgets() {
        locationNameText = findViewById(R.id.locationName);
        weatherImage = findViewById(R.id.weatherImage);
        currentTempText = findViewById(R.id.currentTemp);
        rainChanceText = findViewById(R.id.rainChance);
//        timeWeatherRecycler = findViewById(R.id.timeWeatherRecycler);
//        timeWeatherRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)) ;
    }

    /**
     * 사용자 현재위치 받아오
     * @param locationManager
     */
    private String[] getLocation(LocationManager locationManager) throws Exception {

        String[] result = new String[2];
        //위치 정보 제공 동의 받
        if ( Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 0 );
            return getLocation(locationManager);
        }  else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
//            String provider = location.getProvider();
//            double altitude = location.getAltitude();

            GeocodeUtil.GeoLocation geoLocation = new GeocodeUtil.GeoLocation(latitude, longitude);
            Log.d("DDD", geocodeUtil.getAddressListUsingGeolocation(geoLocation).get(0).getSubLocality());

            ///////주소 한글로 변환하고, 리사이클러뷰 값 표시

            result[0] = "" + latitude;
            result[1] = "" + longitude;

            Log.d("DDD", "" + latitude);
            Log.d("DDD", "" + longitude);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

            return result;
        }
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
