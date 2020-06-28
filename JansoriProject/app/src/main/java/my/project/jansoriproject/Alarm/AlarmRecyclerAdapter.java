package my.project.jansoriproject.Alarm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import my.project.jansoriproject.R;

public class AlarmRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime;
        TextView activeDays;

        AlarmViewHolder(View view) {
            super(view);
            alarmTime = view.findViewById(R.id.alarmTime);
            activeDays = view.findViewById(R.id.activeDays);
        }
    }

    private ArrayList<AlarmItem> alarmItemArrayList;
    AlarmRecyclerAdapter(ArrayList<AlarmItem> alarmItemArrayList) {
        this.alarmItemArrayList = alarmItemArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_recycler_item, parent, false);

        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlarmViewHolder alarmViewHolder = (AlarmViewHolder) holder;

        alarmViewHolder.alarmTime.setText(alarmItemArrayList.get(position).hour + ":" + alarmItemArrayList.get(position).minute);
        Log.i("TAG", alarmItemArrayList.get(position).activeDays.get(0) + "");
        String activeDays = "";
        for (String day : alarmItemArrayList.get(position).activeDays) {
            Log.i("TAG", day);
            activeDays = activeDays + day + " ";
        }
        alarmViewHolder.activeDays.setText(activeDays);
    }

    @Override
    public int getItemCount() {
        return alarmItemArrayList.size();
    }
}
