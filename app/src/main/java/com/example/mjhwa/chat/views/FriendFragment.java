package com.example.mjhwa.chat.views;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.adapters.FriendListAdapter;
import com.example.mjhwa.chat.customviews.RecyclerViewItemClickListener;
import com.example.mjhwa.chat.models.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


    //@BindView(R.id.search_area)
    LinearLayout mSearchArea;

    EditText edtEmail;

    Button btFind;

    RecyclerView mRecyclerView;

    private FirebaseUser mFirebaseUser;

    private FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDB;

    private DatabaseReference mFriendDBRef;

    private DatabaseReference mUserDBRef;

    private FriendListAdapter friendListAdapter;

    private FirebaseAnalytics mFirebaseAnalytics;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.fragment_friend, container, false);
        mSearchArea = (LinearLayout) friendView.findViewById(R.id.search_area);
        edtEmail = (EditText) friendView.findViewById(R.id.edtContent);
        btFind = (Button) friendView.findViewById(R.id.findBtn);
        mRecyclerView = (RecyclerView) friendView.findViewById(R.id.friendRecyclerView);
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        mFriendDBRef = mFirebaseDB.getReference("users").child(mFirebaseUser.getUid()).child("friends");
        mUserDBRef = mFirebaseDB.getReference("users");
        // 1. 리얼타임데이터베이스에서 나의 친구목록 가져옴
        addFriendListener();
        // 2. 가져온 데이터로 recyclerview의 아답터에 아이템 추가 (UI)갱신
        friendListAdapter = new FriendListAdapter();
        mRecyclerView.setAdapter(friendListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 3. 아이템별로 (친구) 클릭이벤트를 주어 선택한 친구와 대화
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final User friend = friendListAdapter.getItem(position);
                //if(friendListAdapter.getSelectionMode() == FriendListAdapter.UNSELECTION_MODE) {

                if(friend.getName() == null) {
                    Snackbar.make(view, friend.getEmail() + "님과 대화를 하시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getActivity(), ChatnavActivity.class);
                            chatIntent.putExtra("uid", friend.getUid());
                            startActivityForResult(chatIntent, ChatFragment.JOIN_ROOM_REQUEST_CODE);
                        }
                    }).show();
                } else {
                    Snackbar.make(view, friend.getName() + "님과 대화를 하시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getActivity(), ChatnavActivity.class);
                            chatIntent.putExtra("uid", friend.getUid());
                            startActivityForResult(chatIntent, ChatFragment.JOIN_ROOM_REQUEST_CODE);
                        }
                    }).show();
                }
                /*}/else {
                    friend.setSelection(friend.isSelection() ? false : true);
                    int selectedUserCount = friendListAdapter.getSelectionUsersCount();
                    Snackbar.make(view, selectedUserCount + "명과 대화를 하시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(getActivity(), ChatnavActivity.class);
                            chatIntent.putExtra("uids", friendListAdapter.getSelectedUids());
                            startActivity(chatIntent);
                        }
                    }).show();
                }*/
            }
        }));

        return friendView;
    }

    public void toggleSearchBar() {
        mSearchArea.setVisibility(mSearchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /*public void toggleSelectionMode() {
        friendListAdapter
                .setSelectionMode(friendListAdapter.getSelectionMode() == FriendListAdapter.SELECTION_MODE ? FriendListAdapter.UNSELECTION_MODE : FriendListAdapter.SELECTION_MODE);
    }*/

    private void addFriend() {

        // 1. 입력된 이메일 가져오기
        final String inputEmail = edtEmail.getText().toString();
        // 2. 이메일이 입력되지 않았다면 이메일을 입력하라는 메시지 띄움
        if(inputEmail.isEmpty()) {
            Snackbar.make(mSearchArea, "이메일을 입력해주세요", Snackbar.LENGTH_LONG).show();
            return;
        }
        // 3. 자기 자신을 입력했을 때 메시지 띄움
        if(inputEmail.equals(mFirebaseUser.getEmail())) {
            Snackbar.make(mSearchArea, "자기 자신은 친구로 등록할 수 없습니다", Snackbar.LENGTH_LONG).show();
            return;
        }
        // 4. 이메일이 정상이라면 나의 정보조회하여 이미 등록된 친구인지 확인
        mFriendDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> friendsIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> friendsIterator = friendsIterable.iterator();

                while(friendsIterator.hasNext()) {
                    User user = friendsIterator.next().getValue(User.class);

                    if(user.getEmail().equals(inputEmail)) {
                        Snackbar.make(mSearchArea, "이미 등록된 친구입니다", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
                // 5. users db에 존재하지않는 이메일이라면 가입하지 않은 친구라는 메시지 띄움

                mUserDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> userIterator = dataSnapshot.getChildren().iterator();
                        int userCount = (int)dataSnapshot.getChildrenCount();
                        int loopCount = 1;

                        while(userIterator.hasNext()) {
                            final User currentUser = userIterator.next().getValue(User.class);

                            if(inputEmail.equals(currentUser.getEmail())) {
                                //친구 등록 로직
                                // 6. users/{myuid}/friends/{someone_uid}/firebasePush/상대정보등록
                                mFriendDBRef.push().setValue(currentUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        // 7. users/{someone_uid}/friends/{myuid}/상대정보등록

                                        // 나의 정보를 가져온다
                                        mUserDBRef.child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                mUserDBRef.child(currentUser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(mSearchArea, "친구등록이 완료되었습니다", Snackbar.LENGTH_LONG).show();

                                                Bundle bundle = new Bundle();
                                                bundle.putString("me", mFirebaseUser.getEmail());
                                                bundle.putString("friend", currentUser.getEmail());
                                                mFirebaseAnalytics.logEvent("addFriend", bundle);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });


                                return;
                            }
                            if(loopCount++ >= userCount) {
                                Snackbar.make(mSearchArea, "가입되지 않은 이메일입니다", Snackbar.LENGTH_LONG).show();
                                return;
                            }
                            // 총 사용자의 명수 == loopCount
                            // 등록된 사용자가 없다는 메시지 출
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addFriendListener() {
        mFriendDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // 1. 리얼타임데이터베이스에서 나의 친구목록 가져옴
                User friend = dataSnapshot.getValue(User.class);
                // 2. 가져온 데이터로 recyclerview의 아답터에 아이템 추가 (UI)갱신
                drawUI(friend);
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
    private void drawUI(User friend) {
        friendListAdapter.addItem(friend);

    }
}
