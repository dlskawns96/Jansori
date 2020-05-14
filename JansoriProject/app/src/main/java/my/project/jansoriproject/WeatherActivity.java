package my.project.jansoriproject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class WeatherActivity extends AppCompatActivity {

    TextView currentTempText;
    TextView minTempText;
    TextView maxTempText;
    TextView rainChanceText;

    HashMap valueMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        initWidgets();

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String[] results = getLocation(locationManager);

        AsyncHttpsConn asyncHttpsConn = new AsyncHttpsConn();
        try {
            valueMap = asyncHttpsConn.execute(results).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("태","" + valueMap.keySet());
        currentTempText.setText("" + valueMap.get("tc"));
        maxTempText.setText("" + valueMap.get("tmax"));
        minTempText.setText("" + valueMap.get("tmin"));
        rainChanceText.setText("" + valueMap.get("sinceOntime"));
    }

    /**
     * 이 액티비티의 위젯 초기
     */
    private void initWidgets() {
        currentTempText = findViewById(R.id.currentTemp);
        maxTempText = findViewById(R.id.maxTemp);
        minTempText = findViewById(R.id.minTemp);
        rainChanceText = findViewById(R.id.rainChance);
    }

    /**
     * 사용자 현재위치 받아오
     * @param locationManager
     */
    private String[] getLocation(LocationManager locationManager) {

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





//    public String getWeatherApiKey() {
//        Bundle bundle;
//
//        try {
//            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
//            bundle = applicationInfo.metaData;
//            return bundle.getString("weatherPlanetKey");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
