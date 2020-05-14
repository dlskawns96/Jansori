package my.project.jansoriproject;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class AsyncHttpsConn extends AsyncTask<String, Void, HashMap<String, String>> {

    final String weatherKey = BuildConfig.WeatherApiKey;
    String str, receiveMsg;

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        try {
            Log.d("태", "연결시");
            URL weatherGetUrl = new URL("https://apis.openapi.sk.com/weather/current/minutely? version= 2&lat=" + strings[0] + "&lon=" + strings[1] + "&appKey=" + weatherKey);
            HttpsURLConnection myConnection = (HttpsURLConnection) weatherGetUrl.openConnection();
            myConnection.setRequestProperty("User-Agent", "Jansori");

            if (myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(responseBodyReader);
                StringBuffer stringBuffer = new StringBuffer();
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                receiveMsg = stringBuffer.toString();
                HashMap tmpMap = weatherJsonParser(receiveMsg);
                return tmpMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> weatherJsonParser(String jsonString) {
        String code;
        String name;
        String sinceOntime;
        String tc;
        String tmax;
        String tmin;

        HashMap map = new HashMap<String, String>();
        try {
            JSONObject oriObject = new JSONObject(jsonString);
            oriObject = oriObject.getJSONObject("weather");
            String minutely = oriObject.getString("minutely");
            JSONArray jsonArray = new JSONArray(minutely);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObject tmp = jsonObject.getJSONObject("sky");
            code = tmp.getString("code");
            map.put("code", code);
            name = tmp.getString("name");
            map.put("name", name);

            tmp = jsonObject.getJSONObject("rain");
            sinceOntime = tmp.getString("sinceOntime");
            map.put("sinceOntime", sinceOntime);

            tmp = jsonObject.getJSONObject("temperature");
            tc = tmp.getString("tc");
            map.put("tc", tc);
            tmax = tmp.getString("tmax");
            map.put("tmax", tmax);
            tmin = tmp.getString("tmin");
            map.put("tmin", tmin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
