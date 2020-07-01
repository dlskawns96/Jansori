package my.project.jansoriproject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class StationRoute extends Fragment {

    RecyclerView stationRouteRecycler;
    Context context;
    ViewGroup stationrouteView;

    private ODsayService odsayService;
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    Integer pathType;
    JSONObject info;
    Integer totalTime;
    Integer totalWalk;
    Integer payment;
    Integer totalTransitCount;
    String startStation;
    String endStation;
    JSONArray subPath;
    Integer trafficType;
    Integer walkSectionTime;
    Integer subwaySectionTime;
    Integer busSectionTime;
    JSONArray lane;
    String subwayName;
    String busNo;
    String startName;
    String way;
    String startId;
    String endName;
    HashMap<String,ArrayList<String>> transferInfoMap = new HashMap<>();
    ArrayList<String> transferInfo = new ArrayList<>();
    ArrayList<Integer> listResId;

    StationRoute.OnDataPass1 dataPasser;

    Date currentTime = Calendar.getInstance().getTime();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        stationrouteView = (ViewGroup) inflater.inflate(R.layout.fragment_station_route, container, false);
        context = stationrouteView.getContext();
        init();

        String startLongitude = getArguments().getString("startLongitude");
        String startLatitude = getArguments().getString("startLatitude");
        String destLongitude = getArguments().getString("destLongitude");
        String destLatitude = getArguments().getString("destLatitude");
        Log.d("DDD",startLongitude+", "+startLatitude+"\n"+destLongitude+", "+destLatitude);
        listResId = getArguments().getIntegerArrayList("resId");
        odsayService.requestSearchPubTransPath(startLongitude, startLatitude, destLongitude, destLatitude, "0", "0", "0", onResultCallbackListener);

//        Log.d("DDD",""+currentTime.getTime());
        return stationrouteView;
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

    private void setStationRoute(final ArrayList<RouteListItem> itemArrayList) {
        stationRouteRecycler = (RecyclerView) stationrouteView.findViewById(R.id.stationRoute);
        stationRouteRecycler.setLayoutManager(new LinearLayoutManager(context));

        RouteRecyclerAdapter stationRouteAdapter = new RouteRecyclerAdapter(context, itemArrayList);

        stationRouteAdapter.setOnItemClickListener(new RouteRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                RouteListItem item = itemArrayList.get(position);
                ArrayList<TransferInfoItem> data = new ArrayList<>();
                data = item.getItem();
                passData(data);
            }
        }) ;

        stationRouteRecycler.setAdapter(stationRouteAdapter);
    }

    //성공적으로 불렸는지 안불렸는지 확인
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson(); //json으로 받아온 데이터 저장
            try {
                jsonObject = oDsayData.getJson().getJSONObject("result");
                jsonArray = (JSONArray)jsonObject.get("path");
                RouteListItem item;
                TransferInfoItem item2;
                final ArrayList<RouteListItem> itemArrayList = new ArrayList<>();
                ArrayList<TransferInfoItem> itemArrayList2;
                String strTotalTime;
                for(int i=0; i<jsonArray.length(); i++) {
                    itemArrayList2 = new ArrayList<>();
                    JSONObject tmp = jsonArray.getJSONObject(i);
                    pathType = tmp.getInt("pathType");
                    info = tmp.getJSONObject("info");
                    totalTime = info.getInt("totalTime");
                    totalWalk = info.getInt("totalWalk");
                    payment = info.getInt("payment");
                    totalTransitCount = info.getInt("subwayTransitCount")+info.getInt("busTransitCount")-1;
                    startStation = info.getString("firstStartStation");
                    endStation = info.getString("lastEndStation");
                    subPath = (JSONArray)tmp.get("subPath");
                    for(int j=0; j<subPath.length(); j++) {
                        JSONObject tmp2 = subPath.getJSONObject(j);
                        trafficType = tmp2.getInt("trafficType");
                        item2 = new TransferInfoItem();
                        if(trafficType == 1) {
                            lane = (JSONArray)tmp2.get("lane");
                            subwayName = lane.getJSONObject(0).getString("name");
                            startName = tmp2.getString("startName");
                            startId = tmp2.getString("startID");
                            endName = tmp2.getString("endName");
                            way = tmp2.getString("way");
                            subwaySectionTime = tmp2.getInt("sectionTime");
                            item2.setLane(subwayName);
                            item2.setPathType("Subway");
                            item2.setSectionTime(subwaySectionTime);
                            item2.setTransferStartStation(startName);
                            item2.setTransferEndStation(endName);
                            item2.setTransferStartStationId(startId);
                        }
                        else if(trafficType == 2) {
                            lane = (JSONArray)tmp2.get("lane");
                            busNo="";
                            for(int k=0; k<lane.length(); k++) {
                                JSONObject tmp3 = lane.getJSONObject(k);
                                if(k==lane.length()-1) {
                                    busNo = busNo+tmp3.getString("busNo");
                                }
                                else {
                                    busNo = busNo+tmp3.getString("busNo")+"\n";
                                }
                            }
                            startName = tmp2.getString("startName");
                            startId = tmp2.getString("startID");
                            endName = tmp2.getString("endName");
                            busSectionTime = tmp2.getInt("sectionTime");
                            item2.setLane(busNo);
                            item2.setPathType("Bus");
                            item2.setSectionTime(busSectionTime);
                            item2.setTransferStartStation(startName);
                            item2.setTransferEndStation(endName);
                            item2.setTransferStartStationId(startId);
                        }
                        else {
                            walkSectionTime = tmp2.getInt("sectionTime");
                            item2.setLane("도보");
                            item2.setPathType("Walk");
                            item2.setSectionTime(walkSectionTime);
                        }
                        itemArrayList2.add(item2);
                    }
                    item = new RouteListItem();
                    if(pathType == 1) {
                        item.setResId(listResId.get(0));
                    } else if (pathType == 2) {
                        item.setResId(listResId.get(1));
                    }
                    else {
                        item.setResId(listResId.get(2));
                    }
                    int hour, minute;
                    hour = totalTime/60;
                    minute = totalTime%60;
                    if(totalTime < 60)
                        strTotalTime = minute+" 분 소요";
                    else
                        strTotalTime = hour+"시간 "+minute+"분 소요";
                    item.setTotalTime(strTotalTime);
//                  item.setArrivalTime();
                    item.setFirstStartStation(startStation);
                    item.setLastEndStation(endStation);
                    item.setPayment(payment);
                    item.setTotalWalk(totalWalk);
                    item.setTransitCount(totalTransitCount);
                    item.setItem(itemArrayList2);
                    itemArrayList.add(item);
                }
                setStationRoute(itemArrayList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(int i, String errorMessage, API api) {
            Toast.makeText(context,"API : " + api.name() + "\n" + errorMessage, Toast.LENGTH_SHORT);
        }
    };

    public interface OnDataPass1 {
        public void onDataPass1 (ArrayList<TransferInfoItem> data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (StationRoute.OnDataPass1) context;
    }

    public void passData(ArrayList<TransferInfoItem> data) {
        dataPasser.onDataPass1(data);
    }
}
