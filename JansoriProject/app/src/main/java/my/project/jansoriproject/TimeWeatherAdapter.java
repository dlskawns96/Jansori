package my.project.jansoriproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TimeWeatherAdapter extends RecyclerView.Adapter<TimeWeatherAdapter.ViewHolder> {

    private ArrayList<TimeWeatherItem> listData = new ArrayList<>();

    private ArrayList<String> times;
    private ArrayList<String> temps;
    private ArrayList<String> rainchances;

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

        data = items.getTemp();
        holder.currentTemp.setText(data);

        data = items.getRainchance();
        holder.rainchance.setText(data);

        //이미지 받아오
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        ImageView weatherImage;
        TextView rainchance;
        TextView currentTemp;

        ViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.timeWeatherTime);
            weatherImage = itemView.findViewById(R.id.timeWeatherImage);
            rainchance = itemView.findViewById(R.id.timeWeatherRainChance);
            currentTemp = itemView.findViewById(R.id.timeWeatherTemp);
        }
    }

    TimeWeatherAdapter(ArrayList<TimeWeatherItem> timeWeatherItems) {
        listData = timeWeatherItems;
    }
}
