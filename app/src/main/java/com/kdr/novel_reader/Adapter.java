package com.kdr.novel_reader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.novel_reader.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    //    implements Filterable {
    private String TAG = "Adapter";
    private Context mContext;
    private ArrayList<Data> mArrayList; //데이터를 담을 어레이리스트
//    private ArrayList<Data> filterList;

    public Adapter(Context context, ArrayList<Data> arrayList) {
        this.mArrayList = arrayList;
        this.mContext =context;
//        this.filterList=arrayList;
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter () {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String charString = charSequence.toString();
//                if(charString.isEmpty()) {
//                    filterList = mArrayList;
//                } else {
//                    ArrayList<Data> filteringList = new ArrayList<>();
//                    for(Data data : mArrayList) {
//                        if(data.getName ().toLowerCase().contains(charString.toLowerCase())) {
//                            filteringList.add(data);
//                        }
//                    }
//                    filterList = filteringList;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = filterList;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                filterList = (ArrayList<Data>)filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }

    //
    public void listFilter(ArrayList<Data> filteredList) {
        mArrayList = filteredList;
        notifyDataSetChanged();
    }

    //아이템 클릭리스너 인터페이스
    interface OnItemClickListener{
        void onItemClick(View v, int position); //뷰와 포지션값
        void onEditClick(View v, int position); //수정
        void onDeleteClick(View v, int position);//삭제
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    //리스트의 각 항목을 이루는 디자인(xml)을 적용.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate (R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder (view);
        return vh;
    }

    //리스트의 각 항목에 들어갈 데이터를 지정.
    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Data data = mArrayList.get (position);

        holder.tv_name.setText (data.getJa_name());
        holder.tv_number.setText (data.getKo_name());
    }

    //화면에 보여줄 데이터의 갯수를 반환.
    @Override
    public int getItemCount() {
        Log.d (TAG, "getItemCount: "+mArrayList.size ());
        return mArrayList.size ();
    }

    //뷰홀더 객체에 저장되어 화면에 표시되고, 필요에 따라 생성 또는 재활용 된다.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_number;
        Button btn_edit, btn_delete;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            this.tv_name = itemView.findViewById (R.id.tv_name);
            this.tv_number = itemView.findViewById (R.id.tv_number);
            this.btn_edit = itemView.findViewById (R.id.btn_edit);
            this.btn_delete = itemView.findViewById (R.id.btn_delete);

            itemView.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onItemClick (view,position);
                        }
                    }
                }
            });

            btn_edit.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onEditClick (view,position);
                        }
                    }
                }
            });

            btn_delete.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition ();
                    if (position!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            mListener.onDeleteClick(view,position);
                        }
                    }
                }
            });
        }
    }
}
