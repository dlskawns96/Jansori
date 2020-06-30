package my.project.jansoriproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ArrivalRecyclerAdapter extends RecyclerView.Adapter<ArrivalRecyclerAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<ArrivalItem> listData;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private ArrivalRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(ArrivalRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public ArrivalRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_arrival, parent, false);
        return new ArrivalRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArrivalRecyclerAdapter.ItemViewHolder holder, int position) {
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

        private TextView laneName;
        private TextView arrivalTime1;
        private TextView arrivalTime2;

        ItemViewHolder(View itemView) {
            super(itemView);

            laneName = itemView.findViewById(R.id.laneName);
            arrivalTime1 = itemView.findViewById(R.id.arrivalTime1);
            arrivalTime2 = itemView.findViewById(R.id.arrivalTime2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // TODO : use pos.
                        ArrivalItem item = listData.get(pos);

                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

        void onBind(ArrivalItem items) {
            if(items.getPathType().contentEquals("Subway")) {
                laneName.setText(items.getLaneName());
                arrivalTime1.setText(items.getArrmsg1());
                arrivalTime2.setText(items.getArrmsg2());
            }
            else {
                laneName.setText(items.getLaneName()+"번");
                arrivalTime1.setText(items.getArrmsg1());
                arrivalTime2.setText(items.getArrmsg2());
            }
        }
    }

    ArrivalRecyclerAdapter(Context context, ArrayList<ArrivalItem> transferInfoItems) {
        listData = transferInfoItems;
        mContext = context;
    }
}
