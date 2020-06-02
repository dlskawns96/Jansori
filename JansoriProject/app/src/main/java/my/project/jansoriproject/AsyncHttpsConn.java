package my.project.jansoriproject;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static java.util.TimeZone.getTimeZone;


class WeatherMaps {
    HashMap<String, String> currentMap;
    HashMap<String, String> hourlyMap;
    HashMap<String, String> dailyMap;
}

public class AsyncHttpsConn extends AsyncTask<String, Void, WeatherMaps> {


    private Date date;
    private SimpleDateFormat sdf;

    private final String weatherKey = BuildConfig.WeatherApiKey;
    private String str, receiveMsg;

    @Override
    protected WeatherMaps doInBackground(String... strings) {
        WeatherMaps maps = new WeatherMaps();
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/onecall?lat="+strings[0]+"&lon="+strings[1]+"&units=metric&lang=kr&appid="+weatherKey);
            HttpsURLConnection httpsURLConnection;
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();

            Log.d("DDD", "Connection Response code : " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                InputStream responseBody = httpsURLConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(responseBodyReader);
                StringBuffer stringBuffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                receiveMsg = stringBuffer.toString();
                maps.currentMap = currentJsonParser(receiveMsg);
                maps.hourlyMap = hourlyJsonParser(receiveMsg);
                maps.dailyMap = dailyJsonParser(receiveMsg);
                return maps;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    private HashMap<String, String> hourlyJsonParser(String jsonString) {
        String temp;
        String icon;
        String time;

        HashMap map = new HashMap<String, String>();
        sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
        sdf.setTimeZone(getTimeZone("Asia/Seoul"));
        try {
            JSONObject oriObject = new JSONObject(jsonString);
            JSONArray jsonArray = oriObject.getJSONArray("hourly");
            JSONObject tmp;
            for(int i = 0; i < 10; i++) {
                tmp = jsonArray.getJSONObject(i);
                time = tmp.getString("dt");
                date = new Date(Integer.parseInt(time) * 1000L);
                Log.d("time:", date.toString());

                time = sdf.format(date);
                Log.d("time:", time);
                temp = tmp.getString("temp");
                temp = temp.substring(0, 2);
                icon = tmp.getJSONArray("weather").getJSONObject(0).getString("icon");
                map.put("time"+i, time);
                map.put("temp"+i, temp);
                map.put("icon"+i, icon);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public HashMap<String, String> currentJsonParser(String jsonString) {
        String code;
        String name;
        String tc;
        String feel;
        String time;

        sdf = new SimpleDateFormat("MM월 dd일 HH시 mm분", Locale.KOREA);
        sdf.setTimeZone(getTimeZone("Asia/Seoul"));

        HashMap map = new HashMap<String, String>();
        try {
            JSONObject oriObject = new JSONObject(jsonString);
            JSONObject tmp = oriObject.getJSONObject("current");
            JSONObject main = tmp.getJSONArray("weather").getJSONObject(0);
            time = tmp.getString("dt");
            date = new Date(Integer.parseInt(time) * 1000L);
            time = sdf.format(date);
            name = main.getString("description");
            tc = tmp.getString("temp");
            tc = tc.substring(0, 2);
            code = main.getString("icon");
            feel = tmp.getString("feels_like");
            feel = feel.substring(0, 2);
            map.put("name", name);
            map.put("tc", tc);
            map.put("icon", code);
            map.put("feel", feel);
            map.put("time", time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private HashMap<String, String> dailyJsonParser(String jsonString) {
        String time;
        String tc;
        String tmax;
        String tmin;
        String icon;
        sdf = new SimpleDateFormat("MM-dd", Locale.KOREA);
        sdf.setTimeZone(getTimeZone("Etc/UTC"));
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            JSONObject object = new JSONObject(jsonString);
            JSONArray dailyArray = object.getJSONArray("daily");
            JSONObject tmp;

            for(int i = 0; i < 7; i++) {
                tmp = dailyArray.getJSONObject(i);
                time = tmp.getString("dt");
                date = new Date(Integer.parseInt(time) * 1000L);
                time = sdf.format(date);

                tc = tmp.getJSONObject("temp").getString("day");
                tc = tc.substring(0, 2);
                tmax = tmp.getJSONObject("temp").getString("max");
                tmin = tmp.getJSONObject("temp").getString("min");
                icon = tmp.getJSONArray("weather").getJSONObject(0).getString("icon");

                map.put("time"+i, time);
                map.put("tc"+i, tc);
                map.put("tmax"+i, tmax);
                map.put("tmin"+i, tmin);
                map.put("icon"+i, icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
