package my.project.jansoriproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.datatype.Duration;


public class StationList extends Fragment {

    RecyclerView stationListRecycler;
    Context context;
    ViewGroup stationlistView;
    private ODsayService odsayService;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    HashMap<String, String> stationMap = new HashMap<>();
    String query;
    String stationTag;
    ArrayList<Integer> listResId;

    OnDataPass dataPasser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        stationlistView = (ViewGroup) inflater.inflate(R.layout.fragment_station_list,container,false);
        context = stationlistView.getContext();
        init();

        stationTag = getArguments().getString("TAG");
        if (stationTag.contentEquals("Default")) {
            String longitude = getArguments().getString("longitude");
            String latitude = getArguments().getString("latitude");
//            Log.d("DDD", latitude+", "+longitude);
            listResId = getArguments().getIntegerArrayList("resId");
            odsayService.requestPointSearch(longitude, latitude, "500", "1:2", onResultCallbackListener);
//            odsayService.requestPointSearch("126.933361407195", "37.3643392278118", "250", "1:2", onResultCallbackListener);
        }
        else {
            query = getArguments().getString("query");
            listResId = getArguments().getIntegerArrayList("resId");
            odsayService.requestSearchStation(query, "1000", "1:2", "10", "1", "127.0363583:37.5113295", onResultCallbackListener);
        }
        return stationlistView;
    }

    private void init() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle aBundle = appInfo.metaData;
        String ODISAY_API_KEY = aBundle.getString("com.odisay.AppKey");

        odsayService = ODsayService.init(context, ODISAY_API_KEY);
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
    }

    private void setStationList() {
        stationListRecycler = (RecyclerView) stationlistView.findViewById(R.id.stationList);
        stationListRecycler.setLayoutManager(new LinearLayoutManager(context));
        StationListItem item;
        final ArrayList<StationListItem> itemArrayList = new ArrayList<>();
        for(int i=0; i < jsonArray.length(); i++) {
            item = new StationListItem();
            item.setStationName(stationMap.get("stationName"+i));
            item.setArsID(stationMap.get("arsID"+i));
            item.setLongitude(stationMap.get("longitude"+i));
            item.setLatitude(stationMap.get("latitude"+i));
            item.setResId(stationMap.get("resId"+i));
            itemArrayList.add(item);
        }
        StationRecyclerAdapter stationListAdapter = new StationRecyclerAdapter(itemArrayList);

        stationListAdapter.setOnItemClickListener(new StationRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                StationListItem item = itemArrayList.get(position);
                ArrayList<String> data = new ArrayList<>();
                data.add(stationTag);
                data.add(item.getStationName());
                data.add(item.getLatitude());
                data.add(item.getLongitude());
                passData(data);
            }
        }) ;

        stationListRecycler.setAdapter(stationListAdapter);
    }

    //성공적으로 불렸는지 안불렸는지 확인
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson(); //json으로 받아온 데이터 저장
            String stationName;
            String arsID;
            String latitude;
            String longitude;
            int stationClass;
            String stationID;
            try {
                jsonObject = oDsayData.getJson().getJSONObject("result");
                jsonArray = (JSONArray)jsonObject.get("station");
                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject tmp = jsonArray.getJSONObject(i);
                    stationName = tmp.getString("stationName");
                    arsID = tmp.getString("arsID");
                    stationID = tmp.getString("ebid");
                    longitude = tmp.getString("x");
                    latitude = tmp.getString("y");
                    stationClass = tmp.getInt("stationClass");
//                    Log.d("DDD", ""+i+""+stationClass);
                    stationMap.put("stationName"+i, stationName);
                    stationMap.put("longitude"+i, longitude);
                    stationMap.put("latitude"+i, latitude);
                    if (stationClass == 2) {
                        stationMap.put("resId"+i, Integer.toString(listResId.get(0)));
                        stationMap.put("arsID"+i, stationID);
                        Log.d("DDD", stationID);
                    }
                    else {
                        stationMap.put("resId"+i, Integer.toString(listResId.get(1)));
                        stationMap.put("arsID"+i, arsID);
                    }
                }
                setStationList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(int i, String errorMessage, API api) {
            Toast.makeText(context,"API : " + api.name() + "\n" + errorMessage, Toast.LENGTH_SHORT);
        }
    };

    public interface OnDataPass {
        public void onDataPass (ArrayList<String> data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public void passData(ArrayList<String> data) {
        dataPasser.onDataPass(data);
    }
}
