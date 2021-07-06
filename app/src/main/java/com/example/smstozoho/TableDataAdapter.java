package com.example.smstozoho;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TableDataAdapter extends RecyclerView.Adapter<TableDataAdapter.ViewHolder> {
    private Context context;
    private List<TableData> arrayList;

    public TableDataAdapter(Context context, List<TableData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TableDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableDataAdapter.ViewHolder holder, int position) {
        holder.no.setText(arrayList.get(position).getId());
        holder.mes.setText(arrayList.get(position).getMessage());
        holder.amm.setText(arrayList.get(position).getAmount());
        holder.type.setText(arrayList.get(position).getType());
        holder.time.setText(arrayList.get(position).getTime());
        holder.descri.setText(arrayList.get(position).getDescription());
        holder.status.setText(arrayList.get(position).getStatus());
        holder.per.setText(arrayList.get(position).getPermission());
        holder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = arrayList.get(position).getMessage();
                String time = arrayList.get(position).getTime();
                Intent intent = new Intent(context,PopupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id","50");
                intent.putExtra("msg",msg);
                intent.putExtra("time",time);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView no,mes,amm,type,descri,time,per,status;
        private ImageView ivArrow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            no =    itemView.findViewById(R.id.textViewNO);
            mes =   itemView.findViewById(R.id.msg);
            amm=    itemView.findViewById(R.id.textViewAmm);
            type=   itemView.findViewById(R.id.textViewType);
            time=   itemView.findViewById(R.id.textViewTime);
            descri= itemView.findViewById(R.id.textViewDescrip);
            status= itemView.findViewById(R.id.textViewStatus);
            per =   itemView.findViewById(R.id.textViewPermission);
            ivArrow = itemView.findViewById(R.id.imageViewArrow);
        }
    }
}
