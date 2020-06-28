package my.project.jansoriproject.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import my.project.jansoriproject.R;

public class TimeWeatherAdapter extends RecyclerView.Adapter<TimeWeatherAdapter.ViewHolder> {

    private ArrayList<TimeWeatherItem> listData = new ArrayList<>();

    @NonNull
    @Override
    public TimeWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.timeweather_item, parent, false);
        TimeWeatherAdapter.ViewHolder viewHolder = new TimeWeatherAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeWeatherItem items = listData.get(position);
        String data = items.getTime();
        holder.time.setText(data);
        Log.d("DDD", items.getSkyCode());
        String iconUrl = "http://openweathermap.org/img/wn/" + items.getSkyCode() + "@2x.png";
        Picasso.get().load(iconUrl).into(holder.weatherImage);
        data = items.getTemp();
        holder.currentTemp.setText(data + "°");
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView weatherImage;
        TextView currentTemp;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.timeWeatherTime);
            weatherImage = itemView.findViewById(R.id.timeWeatherImage);
            currentTemp = itemView.findViewById(R.id.timeWeatherTemp);
        }
    }

    public TimeWeatherAdapter(ArrayList<TimeWeatherItem> timeWeatherItems) {
        listData = timeWeatherItems;
    }
}
