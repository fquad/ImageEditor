package com.example.imagegallery;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ToolsRecyclerView extends RecyclerView.Adapter<ToolsRecyclerView.ViewHolder> {

    private List<Integer> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ToolsRecyclerView(Context context, List<Integer> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tools_button, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int iconId = mData.get(position);
        holder.myBtn.setImageResource(iconId);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageButton myBtn;

        ViewHolder(View itemView) {
            super(itemView);
            myBtn = itemView.findViewById(R.id.btn_tool);
            myBtn.setOnClickListener((v) -> onClick());
        }

        public void onClick() {
            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    /* getItem(int id) {
        return mData.get(id);
    }*/

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }
}