package com.example.mjhwa.chat.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.adapters.EmoticonListAdapter;
import com.example.mjhwa.chat.customviews.RecyclerViewItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by eomtaeyoon on 2017. 11. 25..
 */

public class EmoticonFragment extends Fragment {

    RecyclerView mRecyclerView;

    Button btMakeEmoticon;

    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private DatabaseReference mFriendDBRef;
    private DatabaseReference mUserDBRef;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mEmoticonDBRef;
    private StorageReference mEmoticonStorageRef;

    private EmoticonListAdapter emoticonListAdapter;
    private static final int TAKE_EMOTICON_REQUEST_CODE = 202;

public EmoticonFragment() {

}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View emoticonView = inflater.inflate(R.layout.fragment_emoticon, container, false);
        mRecyclerView = (RecyclerView) emoticonView.findViewById(R.id.emoticonRecyclerView);
        btMakeEmoticon = (Button) emoticonView.findViewById(R.id.makeBt);
        btMakeEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ChatnavActivity)getActivity()).addEmoticon();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        mEmoticonDBRef = mFirebaseDB.getReference("users").child(mFirebaseUser.getUid()).child("emoticons");
        // 데이터베이스에서 이모티콘목록 가져옴
        addEmoticonListener();

        emoticonListAdapter = new EmoticonListAdapter();
        mRecyclerView.setAdapter(emoticonListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String emoticonUrl = emoticonListAdapter.getItem(position);
                ((ChatnavActivity)getActivity()).onEmoticonSendEvent(view, emoticonUrl);
                ((ChatnavActivity)getActivity()).mEmoticonLayout.setVisibility(View.GONE);
            }
        }));

        return emoticonView;

    }

    @Override
    public void onResume() {
        super.onResume();
        //addEmoticonListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_EMOTICON_REQUEST_CODE) {
            if (resultCode == -1) {
                if (data != null) {
                    mEmoticonStorageRef = FirebaseStorage.getInstance().getReference("/emoticons/").child(((ChatnavActivity) getActivity()).getmChatId());

                    mEmoticonStorageRef.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            mEmoticonDBRef.push().setValue(task.getResult().getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    Snackbar.make(mRecyclerView, "이모티콘 등록이 완료되었습니다.", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }
        }

    }

    private void addEmoticonListener() {
        mEmoticonDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String emoticonUrl = dataSnapshot.getValue(String.class);

                drawUI(emoticonUrl);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawUI(String emoticonUrl) {
        emoticonListAdapter.addItem(emoticonUrl);

    }
}
