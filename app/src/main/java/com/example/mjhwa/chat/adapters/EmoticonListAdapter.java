package com.example.mjhwa.chat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mjhwa.chat.R;

import java.util.ArrayList;

/**
 * Created by eomtaeyoon on 2017. 11. 25..
 */

public class EmoticonListAdapter extends RecyclerView.Adapter<EmoticonListAdapter.EmoticonHolder> {

    private ArrayList<String> emoticonList;

    public EmoticonListAdapter() {
        emoticonList = new ArrayList<>();
    }

    public void addItem(String emoticon) {
        emoticonList.add(emoticon);
        notifyDataSetChanged();
    }

    public String getItem(int position){
        return this.emoticonList.get(position);
    }

    @Override
    public EmoticonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_emoticon_item, parent, false);
        EmoticonHolder emoticonHolder = new EmoticonHolder(view);
        return emoticonHolder;
    }

    @Override
    public void onBindViewHolder(EmoticonHolder holder, int position) {
        String emoticon = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(emoticon)
                .override(200,200)
                .into(holder.mEmoticonView);
    }

    @Override
    public int getItemCount() {
        return emoticonList.size();
    }

    public static class EmoticonHolder extends RecyclerView.ViewHolder {
        ImageView mEmoticonView;
        TextView mEmoticonNameView;

        private EmoticonHolder(View v) {
            super(v);
            mEmoticonView = (ImageView) v.findViewById(R.id.emoticon);
        }
    }
}
