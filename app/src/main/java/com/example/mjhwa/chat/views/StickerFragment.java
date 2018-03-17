package com.example.mjhwa.chat.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.adapters.StickerListAdapter;
import com.example.mjhwa.chat.customviews.RecyclerViewItemClickListener;

/**
 * Created by eomtaeyoon on 2017. 12. 2..
 */

public class StickerFragment extends Fragment {

    RecyclerView mRecyclerView;

    private StickerListAdapter stickerListAdapter;

    public StickerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stickerView = inflater.inflate(R.layout.fragment_sticker, container, false);
        mRecyclerView = (RecyclerView) stickerView.findViewById(R.id.stickerRecyclerView);

        stickerListAdapter = new StickerListAdapter();
        mRecyclerView.setAdapter(stickerListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int stickerPng = stickerListAdapter.getPng(position);

                ((MakeActivity)getActivity()).clickStickerEvent(stickerPng);
            }
        }));

        return stickerView;
    }
}
