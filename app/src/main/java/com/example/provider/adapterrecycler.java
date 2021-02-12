package com.example.provider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapterrecycler extends RecyclerView.Adapter<adapterrecycler.holderAdapter>{
    Context context;
    ArrayList<item> mArray;
    adapterrecycler(Context context){
        this.context=context;
        mArray=new ArrayList<>();
    }

    void add(item it){
        mArray.add(it);
        notifyItemInserted(mArray.indexOf(it));
    }

    @NonNull
    @Override
    public holderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new holderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holderAdapter holder, int position) {
        item it=mArray.get(position);
        holder.txt.setText(it.getName());
        holder.red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(context,editcode.class);
                in.putExtra("id",it.getId());
                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArray.size();
    }

    class holderAdapter extends RecyclerView.ViewHolder {
        TextView txt;
        View red;
        public holderAdapter(@NonNull View itemView) {
            super(itemView);
            txt=itemView.findViewById(R.id.item_name);
            red=itemView;
        }
    }
}
