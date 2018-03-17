package com.example.mjhwa.chat.views;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.adapters.MessageListAdapter;
import com.example.mjhwa.chat.models.Chat;
import com.example.mjhwa.chat.models.EmoticonMessage;
import com.example.mjhwa.chat.models.LocationMessage;
import com.example.mjhwa.chat.models.Message;
import com.example.mjhwa.chat.models.PhotoMessage;
import com.example.mjhwa.chat.models.TextMessage;
import com.example.mjhwa.chat.models.User;
import com.example.mjhwa.chat.views.ChatFragment;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.OnClick;

public class ChatnavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String mChatId;
    private ImageView mSenderButton;
    private ImageView mPhotoSend;
    private ImageView mEmoticonSend;
    private EditText mMessageText;
    private Toolbar mToolbar;
    private RecyclerView mChatRecyclerView;
    LinearLayout mEmoticonLayout;

    private MessageListAdapter messageListAdapter;

    private FirebaseDatabase mFirebaseDb;

    private DatabaseReference mChatRef;
    private DatabaseReference mChatMemberRef;
    private DatabaseReference mChatMessageRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mEmoticonDBRef;
    public ArrayList<DatabaseReference> mEmoticonDBArrayRef;
    private FirebaseUser mFirebaseUser;
    private static final int TAKE_PHOTO_REQUEST_CODE = 201;
    private static final int MY_LOCATION_REQUEST_CODE = 202;
    private static final int MY_LOCATION2_REQUEST_CODE = 203;
    private static final int TAKE_EMOTICON_REQUEST_CODE = 204;
    private static final int DELAY_SENT = 205;
    private StorageReference mImageStorageRef;
    private StorageReference mEmoticonStorageRef;
    private FirebaseAnalytics mFirebaseAnalytics;

    SearchView searchView;
    MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatnav);
        //setTheme(android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSenderButton = (ImageView) findViewById(R.id.senderBtn);
        mPhotoSend = (ImageView) findViewById(R.id.photoSend);
        mEmoticonSend = (ImageView) findViewById(R.id.emoticonSend);
        mMessageText = (EditText) findViewById(R.id.edtContent);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_rec_view);
        mEmoticonLayout = (LinearLayout) findViewById(R.id.emoticonLayout);

        mChatId = getIntent().getStringExtra("chat_id");
        mFirebaseDb = FirebaseDatabase.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef = mFirebaseDb.getReference("users");
        mToolbar.setTitleTextColor(Color.WHITE);

        if(mChatId != null) {
            mChatRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("chats").child(mChatId);
            mChatMessageRef = mFirebaseDb.getReference("chat_messages").child(mChatId);
            mChatMemberRef = mFirebaseDb.getReference("chat_members").child(mChatId);
            ChatFragment.JOINED_ROOM = mChatId;

        } else {
            mChatRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("chats");
        }
        mEmoticonDBRef = mUserRef.child(mFirebaseUser.getUid()).child("emoticons");
        messageListAdapter = new MessageListAdapter();
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setAdapter(messageListAdapter);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mSenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendEvent(view);
            }
        });

        mPhotoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoSendEvent(view);
            }
        });

        mEmoticonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmoticonLayout.setVisibility(mEmoticonLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater(); // searchview 기능
        inflater.inflate(R.menu.search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.et_search)); // Date 형식은 11/27 처럼 MM/DD 형식으로
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) { // 날짜 검색 시 날짜 쓰고 나서 검색 버튼 누르면 필터링 되도록
                Toast.makeText(getApplicationContext(), "날짜 검색을 완료했습니다", Toast.LENGTH_LONG).show();
                messageListAdapter.getFilter().filter(s.toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) { // 날짜 검색 시 쓰는 도중에 필터링 되도록
                Toast.makeText(getApplicationContext(), "검색할 날짜를 입력해주세요", Toast.LENGTH_LONG).show();
                messageListAdapter.getFilter().filter(s.toString());
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // 네비게이션 뷰에서 옵션 선택했을 때
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) { // 내 위치 전송하기 위해서 지도 띄울때 사용
            Intent mapIntent = new Intent(ChatnavActivity.this, MapsActivity.class);
            startActivityForResult(mapIntent, MY_LOCATION_REQUEST_CODE);
        } else if (id == R.id.nav_map2) { // 내 위치 근처의 카페, 학교 등의 정보 전송할 때 사용
            Intent map2Intent = new Intent(ChatnavActivity.this, MapsActivity.class);
            startActivityForResult(map2Intent, MY_LOCATION2_REQUEST_CODE);
        } else if (id == R.id.nav_geocoding) { // 경도, 위도 정보 받아온 뒤 주소정보로 바꾸거나 지도에 띄울때 사용
            Intent geoIntent = new Intent(ChatnavActivity.this, GeoActivity.class);
            startActivity(geoIntent);
        } else if (id == R.id.nav_delay) { // 메신저 예약 전송
            Intent delayIntent = new Intent(ChatnavActivity.this, DelayActivity.class);
            startActivityForResult(delayIntent, DELAY_SENT);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String getmChatId() {
        return mChatId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mChatId != null) {
            removeMessageListener();
        }
        ChatFragment.JOINED_ROOM = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mChatId != null) {
            // 총 메세지 카운터를 가져온다
            // onchildadded 호출한 변수의 값이 총메세지의 값과 크거나 같다면, 포커스를 맨아래로 내려줍니다.
            mChatMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long totalMessageCount = dataSnapshot.getChildrenCount();
                    mMessageEventListener.setTotalMessageCount(totalMessageCount);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            messageListAdapter.clearItem();
            addChatListener();
            addMessageListener();
        }
        mEmoticonLayout.setVisibility(View.GONE);
    }

    MessageEventListener mMessageEventListener = new MessageEventListener();

    private void addChatListener() {
        mChatRef.child("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title = dataSnapshot.getValue(String.class);
                if(title != null) {
                    mToolbar.setTitle(title);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMessageListener() {
        mChatMessageRef.addChildEventListener(mMessageEventListener);
    }


    private void removeMessageListener() {
        mChatMessageRef.removeEventListener(mMessageEventListener);
    }

    private class MessageEventListener implements ChildEventListener {

        private long totalMessageCount;

        private long callCount = 1;

        public void setTotalMessageCount(long totalMessageCount) {
            this.totalMessageCount = totalMessageCount;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            // 신규메세지
            Message item = dataSnapshot.getValue(Message.class);
            if(item.getMessageType() == Message.MessageType.TEXT) { // 메세지 타입이 텍스트일 경우
                TextMessage textMessage = dataSnapshot.getValue(TextMessage.class);
                messageListAdapter.addItem(textMessage); // 어댑터에 텍스트 추가
            } else if(item.getMessageType() == Message.MessageType.PHOTO) { // 메세지 타입이 사진일 경우
                PhotoMessage photoMessage = dataSnapshot.getValue(PhotoMessage.class);
                messageListAdapter.addItem(photoMessage); // 어댑터에 사진 추가
            } else if(item.getMessageType() == Message.MessageType.EMOTICON) {
                EmoticonMessage emoticonMessage = dataSnapshot.getValue(EmoticonMessage.class);
                messageListAdapter.addItem(emoticonMessage);
            } else if(item.getMessageType() == Message.MessageType.LOCATION) {
                LocationMessage locationMessage = dataSnapshot.getValue(LocationMessage.class);
                messageListAdapter.addItem(locationMessage);
            }

            if(totalMessageCount <= callCount) {
                //스크롤을 내린다
                mChatRecyclerView.scrollToPosition(messageListAdapter.getItemCount() - 1);
            }
            callCount++;
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            // 변경된 메세지
            Message item = dataSnapshot.getValue(Message.class);

            if(item.getMessageType() == Message.MessageType.TEXT) {
                TextMessage textMessage = dataSnapshot.getValue(TextMessage.class);
                messageListAdapter.addItem(textMessage);
            } else if(item.getMessageType() == Message.MessageType.PHOTO) {
                PhotoMessage photoMessage = dataSnapshot.getValue(PhotoMessage.class);
                messageListAdapter.addItem(photoMessage);
            } else if(item.getMessageType() == Message.MessageType.EXIT) {
                messageListAdapter.addItem(item);
            } else if(item.getMessageType() == Message.MessageType.EMOTICON) {
                EmoticonMessage emoticonMessage = dataSnapshot.getValue(EmoticonMessage.class);
                messageListAdapter.addItem(emoticonMessage);
            } else if(item.getMessageType() == Message.MessageType.LOCATION) {
                LocationMessage locationMessage = dataSnapshot.getValue(LocationMessage.class);
                messageListAdapter.addItem(locationMessage);
            }
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
    }

    public void onSendEvent(View v) { // chat id가 있으면 메세지 전송
        if(mChatId != null) {
            sendMessage();
        } else {
            createChat();
        }
    }

    public void onPhotoSendEvent(View v) {
        // 안드로이드 갤러리 오픈
        // request 코드 201
        //TAKE_PHOTO_REQUEST_CODE

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    public void addEmoticon() {

        Intent intent = new Intent(this, MakeActivity.class);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, TAKE_EMOTICON_REQUEST_CODE);
    }

    private String mEmoticonUrl = null;
    public void onEmoticonSendEvent(View v, String url) {
        mEmoticonUrl = url;
        mMessageType = Message.MessageType.EMOTICON;
        sendMessage();
    }

    private String mLatitude = null;
    private String mLontitude = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if(data != null) {
                // 업로드 이미지를 처리합니다
                // 이미지 업로드가 완료된 경우
                // 실제 web에 업로드 된 주소를 받아서 photoUrl로 저장
                // 그다음 포토메세지 전송
                uploadImage(data.getData());
            }
        }
        else if(requestCode == MY_LOCATION_REQUEST_CODE) { // 내 위치 경도,위도 전송
            if(resultCode == RESULT_OK && data != null) {
                if(resultCode == RESULT_OK && data != null) {
                    String lat = data.getStringExtra("lat");
                    String lon = data.getStringExtra("lon");
                    mLatitude = data.getStringExtra("lat");
                    mLontitude = data.getStringExtra("lon");
                    mMessageType = Message.MessageType.LOCATION;
                    sendMessage();
                }
            }
        }
        else if(requestCode == MY_LOCATION2_REQUEST_CODE) { // snippet 정보 전송
            if(resultCode == RESULT_OK && data != null) {
                sendSnippet(data.getStringExtra("title") + ", " + data.getStringExtra("info"));
            }
        }
        else if(requestCode == DELAY_SENT) { // 예약 전송
            if (resultCode == RESULT_OK && data != null) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        sendDelay(data.getStringExtra("text"));
                    }
                },1000 * data.getIntExtra("time",0));
            }
        }
        else if (requestCode == TAKE_EMOTICON_REQUEST_CODE) {

            if (data != null) {
                byte[] bytes = data.getByteArrayExtra("byte");

                mEmoticonDBArrayRef = new ArrayList<>();
                mEmoticonStorageRef = FirebaseStorage.getInstance().getReference("/emoticons/").child(bytes.toString());

                mEmoticonStorageRef.putBytes(data.getByteArrayExtra("byte")).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        mEmoticonDBRef.push().setValue(task.getResult().getDownloadUrl().toString(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                Snackbar.make(mEmoticonLayout, "이모티콘 등록이 완료되었습니다.", Snackbar.LENGTH_LONG).show();
                                mEmoticonDBArrayRef.add(databaseReference);
                            }
                        });
                    }
                });
            }
        }
    }

    private String mPhotoUrl = null;
    private Message.MessageType mMessageType = Message.MessageType.TEXT;

    private void uploadImage(Uri data) {
        // firebase Storage
        if(mImageStorageRef == null) {
            mImageStorageRef = FirebaseStorage.getInstance().getReference("/chats").child(mChatId);
        }

        mImageStorageRef.putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    mPhotoUrl = task.getResult().getDownloadUrl().toString();
                    mMessageType = Message.MessageType.PHOTO;
                    sendMessage();
                }
            }
        });

    }

    private Message message = new Message();
    private void sendMessage() {
        // 메세지 키 생성
        mChatMessageRef = mFirebaseDb.getReference("chat_messages").child(mChatId);
        // chat_message>{chat_id}>{message_id}>messageInfo
        String messageId = mChatMessageRef.push().getKey();
        String messageText = mMessageText.getText().toString();

        final Bundle bundle = new Bundle();
        bundle.putString("me", mFirebaseUser.getEmail());
        bundle.putString("roomId", mChatId);

        if(mMessageType == Message.MessageType.TEXT) {
            if(messageText.isEmpty()) {
                return;
            }
            message = new TextMessage();
            ((TextMessage)message).setMessageText(messageText);
            bundle.putString("messageType", Message.MessageType.TEXT.toString());
        } else if(mMessageType == Message.MessageType.PHOTO) {
            message = new PhotoMessage();
            ((PhotoMessage)message).setPhotoUrl(mPhotoUrl);
            bundle.putString("messageType", Message.MessageType.PHOTO.toString());
        } else if(mMessageType == Message.MessageType.EMOTICON) {
            message = new EmoticonMessage();
            ((EmoticonMessage)message).setEmoticonUrl(mEmoticonUrl);
            //bundle.putString("messageType", Message.MessageType.EMOTICON.toString());
        } else if(mMessageType == Message.MessageType.LOCATION) {
            message = new LocationMessage();
            ((LocationMessage)message).setLatitude(mLatitude);
            ((LocationMessage)message).setLontitude(mLontitude);
            ((LocationMessage)message).setMessageText(mLatitude + " , " + mLontitude);
            //bundle.putString("messageType", Message.MessageType.LOCATION.toString());
        }

        message.setMessageDate(new Date());
        message.setChatId(mChatId);
        message.setMessageId(messageId);
        message.setMessageType(mMessageType);
        message.setMessageUser(new User(mFirebaseUser.getUid(), mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName()));

        mMessageText.setText("");
        mMessageType = Message.MessageType.TEXT;

        mChatMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mChatMessageRef.child(message.getMessageId()).setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        mFirebaseAnalytics.logEvent("sendMessage", bundle);

                        Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                        while(memberIterator.hasNext()) {
                            User chatMember = memberIterator.next().getValue(User.class);
                            mUserRef.child(chatMember.getUid())
                                    .child("chats")
                                    .child(mChatId)
                                    .child("lastMessage")
                                    .setValue(message);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendSnippet(String sni) {
        // 메세지 키 생성
        mChatMessageRef = mFirebaseDb.getReference("chat_messages").child(mChatId);
        // chat_message>{chat_id}>{message_id}>messageInfo
        String messageId = mChatMessageRef.push().getKey();
        String messageText = sni.toString();

        final Bundle bundle = new Bundle();
        bundle.putString("me", mFirebaseUser.getEmail());
        bundle.putString("roomId", mChatId);

        if(mMessageType == Message.MessageType.TEXT) {
            if(messageText.isEmpty()) {
                return;
            }
            message = new TextMessage();
            ((TextMessage)message).setMessageText(messageText);
            bundle.putString("messageType", Message.MessageType.TEXT.toString());
        }

        message.setMessageDate(new Date());
        message.setChatId(mChatId);
        message.setMessageId(messageId);
        message.setMessageType(mMessageType);
        message.setMessageUser(new User(mFirebaseUser.getUid(), mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName()));

        mMessageText.setText("");
        mMessageType = Message.MessageType.TEXT;

        mChatMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mChatMessageRef.child(message.getMessageId()).setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        mFirebaseAnalytics.logEvent("sendMessage", bundle);

                        Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                        while(memberIterator.hasNext()) {
                            User chatMember = memberIterator.next().getValue(User.class);
                            mUserRef.child(chatMember.getUid())
                                    .child("chats")
                                    .child(mChatId)
                                    .child("lastMessage")
                                    .setValue(message);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendLocation(String lat, String lon) {
        // 메세지 키 생성
        mChatMessageRef = mFirebaseDb.getReference("chat_messages").child(mChatId);
        // chat_message>{chat_id}>{message_id}>messageInfo
        String messageId = mChatMessageRef.push().getKey();
        String messageText = lat;

        final Bundle bundle = new Bundle();
        bundle.putString("me", mFirebaseUser.getEmail());
        bundle.putString("roomId", mChatId);

            if(messageText.isEmpty()) {
                return;
            }
        message = new LocationMessage();
        ((LocationMessage)message).setLatitude(lat);
        ((LocationMessage)message).setLontitude(lon);
        bundle.putString("messageType", Message.MessageType.TEXT.toString());

        message.setMessageDate(new Date());
        message.setChatId(mChatId);
        message.setMessageId(messageId);
        message.setMessageType(Message.MessageType.LOCATION);
        message.setMessageUser(new User(mFirebaseUser.getUid(), mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName()));

        mChatMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mChatMessageRef.child(message.getMessageId()).setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        mFirebaseAnalytics.logEvent("sendLocation", bundle);

                        Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                        while(memberIterator.hasNext()) {
                            User chatMember = memberIterator.next().getValue(User.class);
                            mUserRef.child(chatMember.getUid())
                                    .child("chats")
                                    .child(mChatId)
                                    .child("lastMessage")
                                    .setValue(message);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendDelay(String text) {
        // 메세지 키 생성
        mChatMessageRef = mFirebaseDb.getReference("chat_messages").child(mChatId);
        // chat_message>{chat_id}>{message_id}>messageInfo
        String messageId = mChatMessageRef.push().getKey();
        String messageText = text;

        final Bundle bundle = new Bundle();
        bundle.putString("me", mFirebaseUser.getEmail());
        bundle.putString("roomId", mChatId);

        if(mMessageType == Message.MessageType.TEXT) {
            if(messageText.isEmpty()) {
                return;
            }
            message = new TextMessage();
            ((TextMessage)message).setMessageText(messageText);
            bundle.putString("messageType", Message.MessageType.TEXT.toString());
        } else if(mMessageType == Message.MessageType.PHOTO) {
            message = new PhotoMessage();
            ((PhotoMessage)message).setPhotoUrl(mPhotoUrl);
            bundle.putString("messageType", Message.MessageType.PHOTO.toString());
        }

        message.setMessageDate(new Date());
        message.setChatId(mChatId);
        message.setMessageId(messageId);
        message.setMessageType(mMessageType);
        message.setMessageUser(new User(mFirebaseUser.getUid(), mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName()));

        mMessageText.setText("");
        mMessageType = Message.MessageType.TEXT;

        mChatMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                mChatMessageRef.child(message.getMessageId()).setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        mFirebaseAnalytics.logEvent("sendLocation", bundle);

                        Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                        while(memberIterator.hasNext()) {
                            User chatMember = memberIterator.next().getValue(User.class);
                            mUserRef.child(chatMember.getUid())
                                    .child("chats")
                                    .child(mChatId)
                                    .child("lastMessage")
                                    .setValue(message);

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean isSentMessage = false;
    private void createChat() {
        // 방 생성
        // 0. 방 정보 설정 <--- 기존 방이어야 가능
        final Chat chat = new Chat();
        mChatRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("chats");
        mChatId = mChatRef.push().getKey();
        mChatMemberRef = mFirebaseDb.getReference("chat_members").child(mChatId);

        chat.setChatId(mChatId);
        chat.setCreateDate(new Date());

        String uid = getIntent().getStringExtra("uid");
        String[] uids = getIntent().getStringArrayExtra("uids");
        if(uid != null) {
            //1:1
            uids = new String[]{uid};

        }
        // 1. 대화 상대에 내가 선택한 사람 추가
        List<String> uidList = new ArrayList<>(Arrays.asList(uids));
        uidList.add(mFirebaseUser.getUid());


        for(String userId : uidList) {
            // uid > userInfo
            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    User member = dataSnapshot.getValue(User.class);

                    mChatMemberRef.child(member.getUid())
                            .setValue(member, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    //USERS>uid>chats>{chat_id}>chatinfo
                                    dataSnapshot.getRef().child("chats").child(mChatId).setValue(chat);
                                    if(!isSentMessage) {
                                        sendMessage();
                                        addChatListener();
                                        addMessageListener();
                                        isSentMessage = true;

                                        Bundle bundle = new Bundle();
                                        bundle.putString("me", mFirebaseUser.getEmail());
                                        bundle.putString("roomId", mChatId);
                                        mFirebaseAnalytics.logEvent("createChat", bundle);
                                        ChatFragment.JOINED_ROOM = mChatId;
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        // 2. 각 상대별 chats에 추가
        // 3. 메세지 정보 중 읽은 사람에 내 정보 추가
        // 4. 첫 메세지 전송

    }


}

