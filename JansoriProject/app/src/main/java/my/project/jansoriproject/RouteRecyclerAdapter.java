package my.project.jansoriproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<RouteListItem> listData;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private RouteRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(RouteRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public RouteRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_route, parent, false);
        return new RouteRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteRecyclerAdapter.ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView totalTime;
        TextView arrivalTime;
        TextView payment;
        TextView totalWalk;
        TextView transitCount;
        TextView firstStartStation;
        TextView lastEndStation;
        ImageView pathType;
        RecyclerView transferInfo;

        ItemViewHolder(View itemView) {
            super(itemView);

            totalTime = itemView.findViewById(R.id.totalTime);
            arrivalTime = itemView.findViewById(R.id.arrivalTime);
            payment = itemView.findViewById(R.id.payment);
            totalWalk = itemView.findViewById(R.id.totalWalk);
            transitCount = itemView.findViewById(R.id.transitCount);
            firstStartStation = itemView.findViewById(R.id.startStation);
            lastEndStation = itemView.findViewById(R.id.endStation);
            pathType = itemView.findViewById(R.id.pathType);
            transferInfo = itemView.findViewById(R.id.transferInfo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // TODO : use pos.
                        RouteListItem item = listData.get(pos);

                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

        void onBind(RouteListItem items) {
            pathType.setImageResource(items.getResId());
            totalTime.setText(items.getTotalTime());
            arrivalTime.setText(items.getArrivalTime());
            payment.setText(""+items.getPayment()+" 원");
            totalWalk.setText(""+items.getTotalWalk()+" 걸음");
            transitCount.setText(""+items.getTransitCount()+ "번의 환승");
            firstStartStation.setText(items.getFirstStartStation());
            lastEndStation.setText(items.getLastEndStation());

            TransferRecyclerAdapter transferRecyclerAdapter = new TransferRecyclerAdapter(mContext, items.getItem());
            transferInfo.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            transferInfo.setAdapter(transferRecyclerAdapter);
        }
    }

    RouteRecyclerAdapter(Context context, ArrayList<RouteListItem> routeListItems) {
        listData = routeListItems;
        mContext = context;
    }
}
