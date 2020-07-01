package my.project.jansoriproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransferRecyclerAdapter extends RecyclerView.Adapter<TransferRecyclerAdapter.ItemViewHolder>{
    // adapter에 들어갈 list 입니다.
    private ArrayList<TransferInfoItem> listData;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private TransferRecyclerAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(TransferRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public TransferRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_info, parent, false);
        return new TransferRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransferRecyclerAdapter.ItemViewHolder holder, int position) {
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

        private TextView pathType;
        private TextView lane;
        private TextView transferStart;
        private TextView transferEnd;
        private TextView transferStartStation;
        private TextView transferEndStation;
        private TextView sectionTime;

        ItemViewHolder(View itemView) {
            super(itemView);
            pathType = itemView.findViewById(R.id.pathType);
            lane = itemView.findViewById(R.id.lane);
            transferStart = itemView.findViewById(R.id.transferStart);
            transferEnd = itemView.findViewById(R.id.transferEnd);
            transferStartStation = itemView.findViewById(R.id.transferStartStation);
            transferEndStation = itemView.findViewById(R.id.transferEndStation);
            sectionTime = itemView.findViewById(R.id.sectionTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // TODO : use pos.
                        TransferInfoItem item = listData.get(pos);

                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

        void onBind(TransferInfoItem items) {
            if(items.getPathType().contentEquals("Subway")) {
                lane.setText(items.getLane());
                transferStart.setText("출발지");
                transferEnd.setText("도착지");
                transferStartStation.setText(items.getTransferStartStation());
                transferEndStation.setText(items.getTransferEndStation());
                sectionTime.setText(items.getSectionTime());
            }
            else if(items.getPathType().contentEquals("Bus")) {
                lane.setText(items.getLane());
                transferStart.setText("출발지");
                transferEnd.setText("도착지");
                transferStartStation.setText(items.getTransferStartStation());
                transferEndStation.setText(items.getTransferEndStation());
                sectionTime.setText(items.getSectionTime());
            }
            else {
                lane.setText(items.getLane());
                sectionTime.setText(items.getSectionTime());
            }
        }
    }

    TransferRecyclerAdapter(Context context, ArrayList<TransferInfoItem> transferInfoItems) {
        listData = transferInfoItems;
        mContext = context;
    }
}
