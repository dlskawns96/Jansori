package my.project.jansoriproject;

import android.content.ClipData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

public class StationArrival extends Fragment {

    ViewGroup stationarrivalView;
    RecyclerView arrivalRecycler;
    Context mContext;
    public String Subway_Service_Key = "6a5775637269616d35337657654763";
    public String SERVICE_KEY = "apGij%2Fyjacg1MxhC6bP9HyWS%2FbdwotCa1EfpeR%2B%2BgTQEij6e2oUgOAgxwnNhRwneqofPrDCn2DpgK3Ivsf2SWg%3D%3D";
    public String BUS_API_KEY;
    public String SUBWAY_API_KEY;
    String pathType;
    String arsId;
    String[] busName;
    ArrayList<ArrivalItem> list = null;
    ArrivalItem item = null;
    ArrayList<String> stationInfo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        stationarrivalView = (ViewGroup) inflater.inflate(R.layout.fragment_station_arrival, container, false);
        mContext = stationarrivalView.getContext();

        ApplicationInfo appInfo = null;
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle aBundle = appInfo.metaData;
        BUS_API_KEY = aBundle.getString("com.bus.AppKey");
        SUBWAY_API_KEY = aBundle.getString("com.subway.AppKey");

        pathType = getArguments().getString("pathType");
        if(pathType.contentEquals("Bus")) {
            arsId = getArguments().getString("arsID");
            busName = getArguments().getStringArray("busName");
        } else {
            stationInfo = getArguments().getStringArrayList("stationInfo");
        }

        arrivalRecycler = (RecyclerView) stationarrivalView.findViewById(R.id.stationArrival);
        arrivalRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();

        return stationarrivalView;
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {

                String Url = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey="+BUS_API_KEY+"&arsId="+arsId;
                if (pathType.contentEquals("Subway")) {
                    Url = "http://swopenAPI.seoul.go.kr/api/subway/"+SUBWAY_API_KEY+"/xml/realtimeStationArrival/0/5/"+stationInfo.get(0);
                }

                boolean b_arrmsg1 = false;
                boolean b_arrmsg2 = false;
                boolean b_rtNm = false;
                boolean b_traTime1 = false;
                boolean b_traTime2 = false;

                URL url = new URL(Url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent", "Jansori-app");
                InputStream is = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));

                int eventType = parser.getEventType();

                if (pathType.contentEquals("Bus")) {
                    for (int i=0; eventType != XmlPullParser.END_DOCUMENT; i++) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                list = new ArrayList<ArrivalItem>();
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("itemList") && item.getPathType() != null) {
                                    list.add(item);
                                }
                                break;
                            case XmlPullParser.START_TAG:
                                if (parser.getName().equals("itemList")) {
                                    item = new ArrivalItem();
                                }
                                if (parser.getName().equals("arrmsg1")) b_arrmsg1 = true;
                                if (parser.getName().equals("arrmsg2")) b_arrmsg2 = true;
                                if (parser.getName().equals("rtNm")) b_rtNm = true;
                                if (parser.getName().equals("traTime1")) b_traTime1 = true;
                                if (parser.getName().equals("traTime2")) b_traTime2 = true;
                                break;
                            case XmlPullParser.TEXT:
                                if (b_arrmsg1) {
                                    item.setArrmsg1(parser.getText());
                                    b_arrmsg1 = false;
                                } else if (b_arrmsg2) {
                                    item.setArrmsg2(parser.getText());
                                    b_arrmsg2 = false;
                                } else if (b_rtNm && Arrays.asList(busName).contains(parser.getText())) {
                                    item.setLaneName(parser.getText());
                                    item.setPathType("Bus");
                                    b_rtNm = false;
                                } else if (b_traTime1) {
                                    item.setTraTime1(Integer.parseInt(parser.getText()));
                                    b_traTime1 = false;
                                } else if (b_traTime2) {
                                    item.setTraTime2(Integer.parseInt(parser.getText()));
                                    b_traTime2 = false;
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                } else {
                    for (int i=0; eventType != XmlPullParser.END_DOCUMENT; i++) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                list = new ArrayList<>();
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("row") && item.getPathType() != null) {
                                    list.add(item);
                                }
                                break;
                            case XmlPullParser.START_TAG:
                                if (parser.getName().equals("row")) {
                                    item = new ArrivalItem();
                                }
                                if (parser.getName().equals("trainLineNm")) b_rtNm = true;
                                if (parser.getName().equals("barrvIDt")) b_traTime1 = true;
                                if (parser.getName().equals("arvlMsg2")) b_arrmsg1 = true;
                                if (parser.getName().equals("arvlMsg3")) b_arrmsg2 = true;
                                break;
                            case XmlPullParser.TEXT:
                                if (b_rtNm) {
                                    item.setLaneName(parser.getText());
                                    item.setPathType("Subway");
                                    b_rtNm = false;
                                } else if (b_traTime1) {
                                    item.setTraTime1(Integer.parseInt(parser.getText()));
                                    b_traTime1 = false;
                                } else if (b_arrmsg1) {
                                    item.setArrmsg1(parser.getText());
                                    b_arrmsg1 = false;
                                } else if (b_arrmsg2) {
                                    item.setArrmsg2(parser.getText());
                                    b_arrmsg2 = false;
                                }
                                break;
                        }
                        eventType = parser.next();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //어답터 연결
            ArrivalRecyclerAdapter adapter = new ArrivalRecyclerAdapter(mContext, list);
            arrivalRecycler.setAdapter(adapter);
        }
    }
}