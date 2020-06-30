package my.project.jansoriproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import my.project.jansoriproject.calendar.EventItem;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<EventItem> listData;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // 리스너 객체 참조를 저장하는 변수
    private CalendarEventAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(CalendarEventAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public CalendarEventAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_event_list, parent, false);
        return new CalendarEventAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventAdapter.ItemViewHolder holder, int position) {
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
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView title;
        private TextView location;
        private TextView description;
        private TextView time;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView_title);
            location = itemView.findViewById(R.id.textView_location);
            description = itemView.findViewById(R.id.textView_description);
            time = itemView.findViewById(R.id.textView_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // TODO : use pos.
                        EventItem item = listData.get(pos);

                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정해둡니다.
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 4. 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:
                        break;

                    case 1002:

                        // 5. 삭제 항목을 선택시
                        // 6. ArratList에서 해당 데이터를 삭제하고
                        listData.remove(getAdapterPosition());
                        // 7. 어댑터에서 RecyclerView에 반영하도록 합니다.
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), listData.size());
                        break;
                }
                return true;
            }
        };

        void onBind(EventItem items) {
            title.setText(items.getTitle());
            location.setText(items.getLocation());
            description.setText(items.getDescription());
            time.setText(items.getStartHour()+"시"+items.getStartMinute()+"분 - "+items.getEndHour()+"시"+ items.getEndMinute()+"분");
        }
    }

    CalendarEventAdapter(Context context, ArrayList<EventItem> eventItems) {
        listData = eventItems;
        mContext = context;
    }
}
