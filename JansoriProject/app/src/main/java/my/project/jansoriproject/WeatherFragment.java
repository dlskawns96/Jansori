package my.project.jansoriproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;


public class WeatherFragment extends Fragment implements View.OnClickListener {

    WeatherMaps weatherMaps;

    ImageView weatherImage;
    TextView currentWeatherName;
    TextView currentTemp;
    TextView currentLocation;
    Button fragmentButton;

    MainActivity.LocationInfo locationInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        initWidgets(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)  {
        super.onActivityCreated(savedInstanceState);

        weatherMaps = ((MainActivity)MainActivity.mainContext).getWeatherMaps();
        locationInfo = ((MainActivity)MainActivity.mainContext).getLocationInfo();
        fragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(intent);
            }
        });
        setCurrentWeather(weatherMaps.currentMap);
    }

    private void initWidgets(View view){
        weatherImage = view.findViewById(R.id.weatherFragmentImage);
        currentWeatherName = view.findViewById(R.id.weatherFragmentName);
        currentTemp = view.findViewById(R.id.weatherFragmentCurrent);
        currentLocation = view.findViewById(R.id.weatherFragmentLocationName);
        fragmentButton = view.findViewById(R.id.fragmentButton);
    }

    private void setCurrentWeather(HashMap<String, String> currentMap) {
        int imageID = getResources().getIdentifier("@drawable/image_" + currentMap.get("icon"), "drawable", getActivity().getPackageName());
        weatherImage.setImageResource(imageID);
        currentTemp.setText(currentMap.get("tc") + "°");
        currentWeatherName.setText("" + currentMap.get("name"));
        currentLocation.setText(locationInfo.address);
    }

    @Override
    public void onClick(View v) {

    }
}
