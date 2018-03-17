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
 * Created by eomtaeyoon on 2017. 12. 3..
 */

public class StickerListAdapter extends RecyclerView.Adapter<StickerListAdapter.StickerHolder> {

    private ArrayList<Integer> stickerList;

    public StickerListAdapter() {
        stickerList = new ArrayList<>();
        Init();
    }

    public void Init() {
        stickerList.add(R.mipmap.sticker_1);
        stickerList.add(R.mipmap.sticker_2);
        stickerList.add(R.mipmap.sticker_3);
        stickerList.add(R.mipmap.sticker_4);
        stickerList.add(R.mipmap.sticker_5);
        stickerList.add(R.mipmap.sticker_6);
        stickerList.add(R.mipmap.sticker_7);
        stickerList.add(R.mipmap.sticker_8);
        stickerList.add(R.mipmap.sticker_9);
        stickerList.add(R.mipmap.sticker_10);
        stickerList.add(R.mipmap.sticker_11);
        stickerList.add(R.mipmap.sticker_12);
        stickerList.add(R.mipmap.sticker_13);
        stickerList.add(R.mipmap.sticker_14);
        stickerList.add(R.mipmap.sticker_15);
        stickerList.add(R.mipmap.sticker_16);
        stickerList.add(R.mipmap.sticker_17);
        stickerList.add(R.mipmap.sticker_18);
        stickerList.add(R.mipmap.sticker_19);
        stickerList.add(R.mipmap.sticker_20);
        stickerList.add(R.mipmap.sticker_21);
        stickerList.add(R.mipmap.sticker_22);
        stickerList.add(R.mipmap.sticker_23);
        stickerList.add(R.mipmap.sticker_24);
        stickerList.add(R.mipmap.sticker_25);
        stickerList.add(R.mipmap.sticker_26);
        stickerList.add(R.mipmap.sticker_27);
        stickerList.add(R.mipmap.sticker_28);
        stickerList.add(R.mipmap.sticker_29);
        stickerList.add(R.mipmap.sticker_30);
        stickerList.add(R.mipmap.sticker_31);
        stickerList.add(R.mipmap.sticker_32);
        stickerList.add(R.mipmap.sticker_33);
        stickerList.add(R.mipmap.sticker_34);
        stickerList.add(R.mipmap.sticker_35);
        stickerList.add(R.mipmap.sticker_36);
        stickerList.add(R.mipmap.sticker_37);
        stickerList.add(R.mipmap.sticker_38);
        stickerList.add(R.mipmap.sticker_39);
        stickerList.add(R.mipmap.sticker_40);
        stickerList.add(R.mipmap.sticker_41);
        stickerList.add(R.mipmap.sticker_42);
        stickerList.add(R.mipmap.sticker_43);
        stickerList.add(R.mipmap.sticker_44);
        stickerList.add(R.mipmap.sticker_45);
        stickerList.add(R.mipmap.sticker_46);
        stickerList.add(R.mipmap.sticker_47);
        stickerList.add(R.mipmap.sticker_48);
        stickerList.add(R.mipmap.sticker_49);
        stickerList.add(R.mipmap.sticker_50);
        stickerList.add(R.mipmap.sticker_51);
        stickerList.add(R.mipmap.sticker_52);
    }

    public int getPng(int position) {
        if(position == 0 ) return R.mipmap.sticker_1;
        else if(position == 1 ) return R.mipmap.sticker_2;
        else if(position == 2 ) return R.mipmap.sticker_3;
        else if(position == 3 ) return R.mipmap.sticker_4;
        else if(position == 4 ) return R.mipmap.sticker_5;
        else if(position == 5 ) return R.mipmap.sticker_6;
        else if(position == 6 ) return R.mipmap.sticker_7;
        else if(position == 7 ) return R.mipmap.sticker_8;
        else if(position == 8 ) return R.mipmap.sticker_9;
        else if(position == 9 ) return R.mipmap.sticker_10;
        else if(position == 10 ) return R.mipmap.sticker_11;
        else if(position == 11 ) return R.mipmap.sticker_12;
        else if(position == 12 ) return R.mipmap.sticker_13;
        else if(position == 13 ) return R.mipmap.sticker_14;
        else if(position == 14 ) return R.mipmap.sticker_15;
        else if(position == 15 ) return R.mipmap.sticker_16;
        else if(position == 16 ) return R.mipmap.sticker_17;
        else if(position == 17 ) return R.mipmap.sticker_18;
        else if(position == 18 ) return R.mipmap.sticker_19;
        else if(position == 19 ) return R.mipmap.sticker_20;
        else if(position == 20 ) return R.mipmap.sticker_21;
        else if(position == 21 ) return R.mipmap.sticker_22;
        else if(position == 22 ) return R.mipmap.sticker_23;
        else if(position == 23 ) return R.mipmap.sticker_24;
        else if(position == 24 ) return R.mipmap.sticker_25;
        else if(position == 25 ) return R.mipmap.sticker_26;
        else if(position == 26 ) return R.mipmap.sticker_27;
        else if(position == 27 ) return R.mipmap.sticker_28;
        else if(position == 28 ) return R.mipmap.sticker_29;
        else if(position == 29 ) return R.mipmap.sticker_30;
        else if(position == 30 ) return R.mipmap.sticker_31;
        else if(position == 31 ) return R.mipmap.sticker_32;
        else if(position == 32 ) return R.mipmap.sticker_33;
        else if(position == 33 ) return R.mipmap.sticker_34;
        else if(position == 34 ) return R.mipmap.sticker_35;
        else if(position == 35 ) return R.mipmap.sticker_36;
        else if(position == 36 ) return R.mipmap.sticker_37;
        else if(position == 37 ) return R.mipmap.sticker_38;
        else if(position == 38 ) return R.mipmap.sticker_39;
        else if(position == 39 ) return R.mipmap.sticker_40;
        else if(position == 40 ) return R.mipmap.sticker_41;
        else if(position == 41 ) return R.mipmap.sticker_42;
        else if(position == 42 ) return R.mipmap.sticker_43;
        else if(position == 43 ) return R.mipmap.sticker_44;
        else if(position == 44 ) return R.mipmap.sticker_45;
        else if(position == 45 ) return R.mipmap.sticker_46;
        else if(position == 46 ) return R.mipmap.sticker_47;
        else if(position == 47 ) return R.mipmap.sticker_48;
        else if(position == 48 ) return R.mipmap.sticker_49;
        else if(position == 49 ) return R.mipmap.sticker_50;
        else if(position == 50 ) return R.mipmap.sticker_51;
        else if(position == 51 ) return R.mipmap.sticker_52;
        return -1;
    }
    public int getItem(int position){
        return this.stickerList.get(position);
    }

    @Override
    public StickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sticker_item, parent, false);
        StickerHolder stickerHolder = new StickerHolder(view);
        return stickerHolder;
    }

    @Override
    public void onBindViewHolder(StickerHolder holder, int position) {
        int sticker = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(sticker)
                .override(50,50)
                .into(holder.mStickerView);
    }

    @Override
    public int getItemCount() {
        return stickerList.size();
    }

    public static class StickerHolder extends RecyclerView.ViewHolder {
        ImageView mStickerView;
        TextView mEmoticonNameView;

        private StickerHolder(View v) {
            super(v);
            mStickerView = (ImageView) v.findViewById(R.id.sticker);
        }
    }
}
