package com.example.mjhwa.chat.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.mjhwa.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {
    private String mChatId;

    @BindView(R.id.senderBtn)
    ImageView mSemderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat);

        ButterKnife.bind(this);
        String uid = getIntent().getStringExtra("uid");
        mChatId = getIntent().getStringExtra("chat_id");
        String [] uids = getIntent().getStringArrayExtra("uids");

        if(uid != null) {
            uids = new String[] {uid};
        }

        if (uids != null) {
        }
    }

}
