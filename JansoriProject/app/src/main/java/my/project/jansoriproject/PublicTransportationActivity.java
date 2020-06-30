package my.project.jansoriproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicTransportationActivity extends AppCompatActivity implements StationList.OnDataPass, StationRoute.OnDataPass1 {
    private Spinner sp_api;
    private RadioGroup rg_object_type;
    private RadioButton rb_json, rb_map;
    private Button bt_api_call;
    private TextView tv_data;
    private Context context;
    private String spinnerSelectedName;
    private ODsayService odsayService;
    private JSONObject jsonObject;
    private Map mapObject;

    Button station1_btn;
    Button station2_btn;
    TextView station1_txtview;
    TextView station2_txtview;
    EditText station1_editxt;
    EditText station2_editxt;
    Button swap_btn;
    Button search_btn;
    TextView variousInfo;

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    static String TAG;

    StationList station_list;
    StationRoute station_route;
    StationArrival station_arrival;
    Fragment fragment;

    String currentLatitude;
    String currentLongitude;
    String destLatitude;
    String destLongitude;

    Integer pathType;
    String subwayId, busId;
    String subwayName;
    String[] busName;
    ArrayList<Integer> listResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publictransportation);
        init();

        listResId = new ArrayList<>();
        listResId.add(R.drawable.subway);
        listResId.add(R.drawable.bus);
        listResId.add(R.drawable.hybrid);

        //사용자 위치동의
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.transportation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        gpsTracker = new GpsTracker(PublicTransportationActivity.this);
//        currentLatitude = Double.toString(gpsTracker.getLatitude());
//        currentLongitude = Double.toString(gpsTracker.getLongitude());
//        Log.d("DDD", currentLatitude);
//
//        if (!currentLatitude.equals(0.0)) {
//            //현재위치기반으로 주변 정류소 찾기
//            TAG = "Default";
//            Bundle bundle = new Bundle();
//            bundle.putString("TAG", TAG);
//            bundle.putString("latitude", currentLatitude);
//            bundle.putString("longitude", currentLongitude);
//            bundle.putIntegerArrayList("resId", listResId);
////        Log.d("DDD", currentLatitude+", "+currentLongitude);
//            station_list.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().add(R.id.fragment, station_list).commit();
//        }

        //각 버튼에 대한 리스너
        station1_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (station1_editxt.getText() != null) {
                    station_list = new StationList();
                    String station_name1 = station1_editxt.getText().toString().trim();
                    TAG = "Station1";
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", TAG);
                    bundle.putString("query", station_name1);
                    bundle.putIntegerArrayList("resId",listResId);
                    station_list.setArguments(bundle);
//                    fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
//                    Fragment myFragment = getSupportFragmentManager().findFragmentByTag("Station List");
//                    getSupportFragmentManager().beginTransaction().detach((Fragment)myFragment).attach((Fragment)station_list).commit();
                    variousInfo.setText("["+station1_editxt.getText().toString().trim()+"]"+" 결과입니다.");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, station_list).addToBackStack(null).commit();
                }
                else {
                    Toast.makeText(context,"Enter string", Toast.LENGTH_SHORT);
                }
            }
        });
        station2_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (station2_editxt.getText() != null) {
                    station_list = new StationList();
                    String station_name2 = station2_editxt.getText().toString().trim();
                    TAG = "Station2";
                    Bundle bundle = new Bundle();
                    bundle.putString("TAG", TAG);
                    bundle.putString("query", station_name2);
                    bundle.putIntegerArrayList("resId",listResId);
                    station_list.setArguments(bundle);
                    fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
//                    getSupportFragmentManager().beginTransaction().detach(fragment).attach((Fragment)station_list).commit();
                    variousInfo.setText("["+station2_editxt.getText().toString().trim()+"]"+" 결과입니다.");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, station_list).addToBackStack(null).commit();
                }
                else {
                    Toast.makeText(context,"Enter string", Toast.LENGTH_SHORT);
                }
            }
        });
//        default_btn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
        swap_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String tmpStation2 = station2_editxt.getText().toString();
                station2_editxt.setText(station1_editxt.getText().toString());
                station1_editxt.setText(tmpStation2);
                String tmpCurrentLati = currentLatitude;
                String tmpCurrentLong = currentLongitude;
                currentLatitude = destLatitude;
                currentLongitude = destLongitude;
                destLatitude = tmpCurrentLati;
                destLongitude = tmpCurrentLong;
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                station_route = new StationRoute();
                Bundle bundle = new Bundle();
//                bundle.putString("TAG", TAG);
                bundle.putString("startLatitude", currentLatitude);
                bundle.putString("startLongitude", currentLongitude);
                bundle.putString("destLatitude", destLatitude);
                bundle.putString("destLongitude", destLongitude);
                bundle.putIntegerArrayList("resId",listResId);
                station_route.setArguments(bundle);
//                fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
//                getSupportFragmentManager().beginTransaction().detach((Fragment)fragment).attach((Fragment)station_route).commit();
//                getSupportFragmentManager().beginTransaction().detach((Fragment)station_list).attach((Fragment)station_route).commit();
                variousInfo.setText("길찾기 결과입니다.");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, station_route).addToBackStack(null).commit();
            }
        });
    }

    private void init() {
        context = this;
//        sp_api = (Spinner) findViewById(R.id.sp_api);
//        rg_object_type = (RadioGroup) findViewById(R.id.rg_object_type);
//        bt_api_call = (Button) findViewById(R.id.bt_api_call);
//        rb_json = (RadioButton) findViewById(R.id.rb_json);
//        rb_map = (RadioButton) findViewById(R.id.rb_map);
//        tv_data = (TextView) findViewById(R.id.tv_data);
//        sp_api.setSelection(0);

        ApplicationInfo appInfo = null;
        try {
            appInfo = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle aBundle = appInfo.metaData;
        String ODISAY_API_KEY = aBundle.getString("com.odisay.AppKey");

        odsayService = ODsayService.init(PublicTransportationActivity.this, ODISAY_API_KEY);
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
//
//        bt_api_call.setOnClickListener(onClickListener);
//        sp_api.setOnItemSelectedListener(onItemSelectedListener);
//        rg_object_type.setOnCheckedChangeListener(onCheckedChangeListener);

        station1_btn = (Button) findViewById(R.id.station1_btn);
        station2_btn = (Button) findViewById(R.id.station2_btn);
        station1_txtview = (TextView) findViewById(R.id.station1_txtview);
        station2_txtview = (TextView) findViewById(R.id.station2_txtview);
        station1_editxt = (EditText) findViewById(R.id.station1_editxt);
        station2_editxt = (EditText) findViewById(R.id.station2_editxt);
        swap_btn = (Button) findViewById(R.id.swap_btn);
        variousInfo = (TextView) findViewById(R.id.various_info);
        search_btn = (Button) findViewById(R.id.search_btn);

        station_list = new StationList();
        station_route = new StationRoute();
        station_arrival = new StationArrival();
    }

    // station list 에서 받아온 데이터
    @Override
    public void onDataPass(ArrayList<String> data) {
        if (data.get(0).contentEquals("Default") || data.get(0).contentEquals("Station1"))
            station1_editxt.setText(data.get(1));
        else
            station2_editxt.setText(data.get(1));
        destLatitude = data.get(2);
        destLongitude = data.get(3);
    }

    // station route 에서 받아온 데이터
    @Override
    public void onDataPass1(ArrayList<TransferInfoItem> data) {
        for(int i=0; i<data.size(); i++) {
            if (data.get(i).getPathType().contentEquals("Subway")) {
                pathType = 1;
                subwayId = data.get(i).getTransferStartStationId();
                subwayName = data.get(i).getLane();
                odsayService.requestSubwayStationInfo(subwayId, onResultCallbackListener);
                break;
            }
            else if (data.get(i).getPathType().contentEquals("Bus")) {
                pathType = 2;
                busId = data.get(i).getTransferStartStationId();
                String tmpName = data.get(i).getLane();
                String delimiter = "\n";
                busName = tmpName.split(delimiter);
                odsayService.requestBusStationInfo(busId, onResultCallbackListener);
                break;
            }
        }

    }

//    //라디오 버튼이 눌렸을 때 동작
//    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//            //json 버튼을 눌렀을 때 동작
//            if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
//                tv_data.setText(jsonObject.toString());//텍스트뷰에 텍스트 json으로 설정
//            } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
//                tv_data.setText(mapObject.toString());
//            }
//        }
//    };
//    //spinner 아이템 중 어떤 것이 선택되었나 판별
//    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view,
//                                   int position, long id) {
//            spinnerSelectedName = (String) parent.getItemAtPosition(position);
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    };
    //성공적으로 불렸는지 안불렸는지 확인
    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson(); //json으로 받아온 데이터 저장
            String arsId;
            ArrayList<String> stationInfo = new ArrayList<>();
            String stationName;
            String stationId;
            String laneName;
            try {
                jsonObject = oDsayData.getJson().getJSONObject("result");
                station_arrival = new StationArrival();
                if (pathType == 1) {    //지하철 도착시간
                    stationName = jsonObject.getString("stationName");
                    stationId = jsonObject.getString("stationID");
                    laneName = jsonObject.getString("laneName");
                    Log.d("DDD", laneName);
                    stationInfo.add(stationName);
                    stationInfo.add(stationId);
                    stationInfo.add(laneName);
                    JSONObject extraOBJ = jsonObject.getJSONObject("exOBJ");
                    if (extraOBJ.length() != 0) {
                        JSONArray station = (JSONArray)extraOBJ.get("station");
                        for (int i=0; i<station.length();i++) {
                            JSONObject tmp = station.getJSONObject(i);
                            stationName = tmp.getString("stationName");
                            stationId = tmp.getString("stationID");
                            laneName = tmp.getString("laneName");
                            stationInfo.add(stationName);
                            stationInfo.add(stationId);
                            stationInfo.add(laneName);
                        }
                    }
                    Log.d("DDD", laneName);
                    Bundle bundle = new Bundle();
                    bundle.putString("pathType", "Subway");
                    bundle.putStringArrayList("stationInfo",stationInfo);
                    station_arrival.setArguments(bundle);
                    variousInfo.setText("정류장 도착정보 입니다.");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, station_arrival).addToBackStack(null).commit();
                }
                else {  //버스 도착시간
                    arsId = jsonObject.getString("arsID");
                    String delimiter = "-";
                    String[] tmp = arsId.split(delimiter);
                    arsId = tmp[0] + tmp[1];
                    Bundle bundle = new Bundle();
                    bundle.putString("pathType", "Bus");
                    bundle.putStringArray("busName", busName);
                    bundle.putString("arsID", arsId);
                    station_arrival.setArguments(bundle);
                    variousInfo.setText("정류장 도착정보 입니다.");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, station_arrival).addToBackStack(null).commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            tv_data.setText(jsonObject.toString());
//            String arsID = "";
//            if (rg_object_type.getCheckedRadioButtonId() == rb_json.getId()) {
////                Log.d(TAG, spinnerSelectedName);
//                if (spinnerSelectedName.contentEquals("대중교통 정류장 검색")) {
//                    try {
//                        tv_data.setText(jsonObject.toString());
//                        jsonObject = oDsayData.getJson().getJSONObject("result");
//                        jsonArray = (JSONArray)jsonObject.get("station");
//                        for(int i=0; i<jsonArray.length(); i++)
//                        {
//                            JSONObject tmp = jsonArray.getJSONObject(i);
//                            arsID += tmp.getString("arsID") + ",";
//                        }
////                        tv_data.setText(arsID);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else if (spinnerSelectedName.contentEquals("반경내 대중교통 POI 검색")){
//                    try {
//                        jsonObject = oDsayData.getJson().getJSONObject("result");
//                        jsonArray = (JSONArray)jsonObject.get("station");
//                        for(int i=0; i<jsonArray.length(); i++)
//                        {
//                            JSONObject tmp = jsonArray.getJSONObject(i);
//                            arsID += tmp.getString("arsID") + ",";
//                        }
//                        tv_data.setText(arsID);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                     tv_data.setText(jsonObject.toString());
//                }
//            } else if (rg_object_type.getCheckedRadioButtonId() == rb_map.getId()) {
//                tv_data.setText(mapObject.toString());
//            }
        }

        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.d("DDD", "API : " + api.name() + "\n" + errorMessage);
        }
    };
//    //api버튼이 눌렸는데 어떤 api를 콜할건지 스피너셀렉티드를 확인하여 호출
//    private View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (spinnerSelectedName) {
//                case "버스 노선 조회":
//                    odsayService.requestSearchBusLane("150", "1000", "no", "10", "1", onResultCallbackListener);
//                    break;
//                case "버스노선 상세정보 조회":
//                    odsayService.requestBusLaneDetail("12018", onResultCallbackListener);
//                    break;
//                case "버스정류장 세부정보 조회":
//                    odsayService.requestBusStationInfo("107475", onResultCallbackListener);
//                    break;
//                case "열차•KTX 운행정보 검색":
//                    odsayService.requestTrainServiceTime("3300128", "3300108", onResultCallbackListener);
//                    break;
//                case "고속버스 운행정보 검색":
//                    odsayService.requestExpressServiceTime("4000057", "4000030", onResultCallbackListener);
//                    break;
//                case "시외버스 운행정보 검색":
//                    odsayService.requestIntercityServiceTime("4000022", "4000255", onResultCallbackListener);
//                    break;
//                case "항공 운행정보 검색":
//                    odsayService.requestAirServiceTime("3500001", "3500003", "6", onResultCallbackListener);
//                    break;
//                case "운수회사별 버스노선 조회":
//                    odsayService.requestSearchByCompany("792", "100", onResultCallbackListener);
//                    break;
//                case "지하철역 세부 정보 조회":
//                    odsayService.requestSubwayStationInfo("130", onResultCallbackListener);
//                    break;
//                case "지하철역 전체 시간표 조회":
//                    odsayService.requestSubwayTimeTable("130", "1", onResultCallbackListener);
//                    break;
//                case "노선 그래픽 데이터 검색":
//                    odsayService.requestLoadLane("0:0@12018:1:-1:-1", onResultCallbackListener);
//                    break;
//                case "대중교통 정류장 검색":
//                    odsayService.requestSearchStation("삼성역", "1000", "1:2", "10", "1", "127.0363583:37.5113295", onResultCallbackListener);
//                    break;
//                case "반경내 대중교통 POI 검색":
////                    odsayService.requestPointSearch("126.933361407195", "37.3643392278118", "250", "1:2", onResultCallbackListener);
//                    //현재위치기반으로 주변 정류소 찾기
////                    gpsTracker = new GpsTracker(PublicTransportationActivity.this);
////                    String latitude = "" + gpsTracker.getLatitude();
////                    String longitude = "" + gpsTracker.getLongitude();
////                    odsayService.requestPointSearch(longitude, latitude, "500", "1:2", onResultCallbackListener);
//                    break;
//                case "지도 위 대중교통 POI 검색":
//                    odsayService.requestBoundarySearch("127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "127.045478316811:37.68882830829:127.055063420699:37.6370465749586", "1:2", onResultCallbackListener);
//                    break;
//                case "지하철 경로검색 조회(지하철 노선도)":
//                    odsayService.requestSubwayPath("1000", "201", "222", "1", onResultCallbackListener);
//                    break;
//                case "대중교통 길찾기":
//                    odsayService.requestSearchPubTransPath("126.926493082645", "37.6134436427887", "127.126936754911", "37.5004198786564", "0", "0", "0", onResultCallbackListener);
////                    odsayService.requestSearchPubTransPath("126.933361407195", "37.3643392278118", "127.126936754911", "37.5004198786564", "0", "0", "0", onResultCallbackListener);
//                    break;
//                case "지하철역 환승 정보 조회":
//                    odsayService.requestSubwayTransitInfo("133", onResultCallbackListener);
//                    break;
//                case "고속버스 터미널 조회":
//                    odsayService.requestExpressBusTerminals("1000", "서울", onResultCallbackListener);
//                    break;
//                case "시외버스 터미널 조회":
//                    odsayService.requestIntercityBusTerminals("1000", "서울", onResultCallbackListener);
//                    break;
//                case "도시코드 조회":
//                    odsayService.requestSearchCID("서울", onResultCallbackListener);
//                    break;
//            }
//        }
//    };

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if ( check_result ) {
                //위치 값을 가져올 수 있음
                //현재위치기반으로 주변 정류소 찾기
                gpsTracker = new GpsTracker(PublicTransportationActivity.this);
                currentLatitude = Double.toString(gpsTracker.getLatitude());
                currentLongitude = Double.toString(gpsTracker.getLongitude());

                TAG = "Default";
                Bundle bundle = new Bundle();
                bundle.putString("TAG", TAG);
                bundle.putString("latitude", currentLatitude);
                bundle.putString("longitude", currentLongitude);
                bundle.putIntegerArrayList("resId", listResId);

                station_list.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, station_list).commit();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(PublicTransportationActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(PublicTransportationActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission(){
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(PublicTransportationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(PublicTransportationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
            //현재위치기반으로 주변 정류소 찾기
            gpsTracker = new GpsTracker(PublicTransportationActivity.this);
            currentLatitude = Double.toString(gpsTracker.getLatitude());
            currentLongitude = Double.toString(gpsTracker.getLongitude());

            TAG = "Default";
            Bundle bundle = new Bundle();
            bundle.putString("TAG", TAG);
            bundle.putString("latitude", currentLatitude);
            bundle.putString("longitude", currentLongitude);
            bundle.putIntegerArrayList("resId", listResId);

            station_list.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, station_list).commit();
        } else {
            //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(PublicTransportationActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(PublicTransportationActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(PublicTransportationActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(PublicTransportationActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PublicTransportationActivity.this);
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
                        Log.d("DDD", "onActivityResult : GPS 활성화 되있음");
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

    // 툴바 뒤로가기 버튼 활성화
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
