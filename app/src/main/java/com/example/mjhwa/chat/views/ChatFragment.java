package com.example.mjhwa.chat.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.adapters.ChatListAdapter;
import com.example.mjhwa.chat.customviews.RecyclerViewItemClickListener;
import com.example.mjhwa.chat.models.Chat;
import com.example.mjhwa.chat.models.ExitMessage;
import com.example.mjhwa.chat.models.Message;
import com.example.mjhwa.chat.models.Notification;
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

import java.util.Date;
import java.util.Iterator;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FirebaseUser mFirebaseUser;

    private FirebaseDatabase mFirebaseDb;

    private DatabaseReference mChatRef;

    private DatabaseReference mChatMemberRef;

    private DatabaseReference mChatMessageRef;

    private RecyclerView mChatRecyclerView;

    private ChatListAdapter mChatListAdapter;

    public static String JOINED_ROOM = "";

    public static final int JOIN_ROOM_REQUEST_CODE = 100;

    private Notification mNotification;

    private Context mContext;

    private FirebaseAnalytics mFirebaseAnalytics;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chatView = inflater.inflate(R.layout.fragment_chat, container, false);

        // 채팅방 리스너 부착
        // users/{나의 uid}/chats/
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDb = FirebaseDatabase.getInstance();
        mChatRef = mFirebaseDb.getReference("users").child(mFirebaseUser.getUid()).child("chats");
        mChatMemberRef = mFirebaseDb.getReference("chat_members");
        mChatMessageRef = mFirebaseDb.getReference("chat_messages");

        mChatRecyclerView = (RecyclerView) chatView.findViewById(R.id.chatRecylerView);

        mChatListAdapter = new ChatListAdapter();
        mChatListAdapter.setFragment(this);
        mChatRecyclerView.setAdapter(mChatListAdapter);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        mChatRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Chat chat = mChatListAdapter.getItem(position);
                Intent chatIntent = new Intent(getActivity(), ChatnavActivity.class);
                chatIntent.putExtra("chat_id", chat.getChatId());
                startActivityForResult(chatIntent, JOIN_ROOM_REQUEST_CODE);
            }
        }));
        mContext = getActivity();
        mNotification = new Notification(mContext);

        addChatListener();
        return chatView;
    }

    private void addChatListener() {
        mChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot chatDataSnapshot, String s) {
                // 방에 대한 정보를 얻어오고
                // ui 갱신시켜주는 메소드로 방 정보 전달
                drawUI(chatDataSnapshot, DrawType.ADD);
            }

            @Override
            public void onChildChanged(DataSnapshot chatDataSnapshot, String s) {

                // 나의 내가 보낸 메시지가 아닌경우와 마지막 메세지가 수정이 되었다면 -> 노티출력

                // 변경된 방의 정보를 수신
                // 변경된 포지션 확인. ( 채팅방 아이디로 기존의 포지션을 확인 합니다)
                // 그 포지션의 아이템중 unreadCount 변경이 되었다면 unreadCount 변경
                // lastMessage 입니다. last 메세지의 시각과 변경된 메세지의 last메세지 시간이 다르다면 -> 노티피케이션을 출력햅니다.
                // 현재 액티비티가 ChatnavActivity 이고 chat_id 가 같다면 노티는 해주지 않습니다.

                // ui 갱신 시켜주는 메서드로 방의 정보를 전달.
                // totalunread의 변경, title의 변경, lastmessage변경시에 호출이 됩니다.
                // 방에 대한 정보를 얻어오고

                drawUI(chatDataSnapshot, DrawType.UPDATE);
                final Chat updatedChat = chatDataSnapshot.getValue(Chat.class);

                if (updatedChat.getLastMessage() != null ) {
                    if(updatedChat.getLastMessage().getMessageType() == Message.MessageType.EXIT) {
                        return;
                    }
                    if ( !updatedChat.getLastMessage().getMessageUser().getUid().equals(mFirebaseUser.getUid())) {
                        if ( !updatedChat.getChatId().equals(JOINED_ROOM)) {
                            // 노티피케이션 알림
                            Intent chatIntent = new Intent(mContext, ChatnavActivity.class);
                            chatIntent.putExtra("chat_id", updatedChat.getChatId());
                            mNotification
                                    .setData(chatIntent)
                                    .setTitle(updatedChat.getLastMessage().getMessageUser().getName())
                                    .setText(updatedChat.getLastMessage().getMessageText())
                                    .notification();

                            Bundle bundle = new Bundle();
                            bundle.putString("friend", updatedChat.getLastMessage().getMessageUser().getEmail());
                            bundle.putString("me", mFirebaseUser.getEmail());
                            mFirebaseAnalytics.logEvent("notification", bundle);
                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // 방의 실시간 삭제
                Chat item = dataSnapshot.getValue(Chat.class);
                mChatListAdapter.removeItem(item);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void drawUI(final DataSnapshot chatDataSnapshot, final DrawType drawType) {

        final Chat chatRoom = chatDataSnapshot.getValue(Chat.class);
        mChatMemberRef.child(chatRoom.getChatId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long memberCount = dataSnapshot.getChildrenCount();
                Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                StringBuffer memberStringBuffer = new StringBuffer();

                /**
                 *  <추가 반영분>
                 *  방에 한명밖에 없는 경우 방을 사용하지 못하게 처리 합니다.
                 **/
                if ( memberCount <= 1 ) {
                    chatRoom.setTitle("대화상대가 없는 방입니다.");
                    chatDataSnapshot.getRef().child("title").setValue(chatRoom.getTitle());
                    chatDataSnapshot.getRef().child("disabled").setValue(true);
                    if ( drawType == DrawType.ADD) {
                        mChatListAdapter.addItem(chatRoom);
                    } else {
                        mChatListAdapter.updateItem(chatRoom);
                    }
                    return;
                }

                int loopCount = 1;
                while( memberIterator.hasNext()) {
                    User member = memberIterator.next().getValue(User.class);
                    if ( !mFirebaseUser.getUid().equals(member.getUid())) {
                        if(member.getName() == null) {
                            memberStringBuffer.append(member.getEmail());
                        }
                        else {
                            memberStringBuffer.append(member.getName());
                        }
                        if ( memberCount - loopCount > 1 ) {
                            memberStringBuffer.append(", ");
                        }
                    }
                    if ( loopCount == memberCount ) {
                        // users/uid/chats/{chat_id}/title
                        String title = memberStringBuffer.toString();
                        if ( chatRoom.getTitle() == null ) {
                            chatDataSnapshot.getRef().child("title").setValue(title);
                        } else if (!chatRoom.getTitle().equals(title)){
                            chatDataSnapshot.getRef().child("title").setValue(title);
                        }
                        chatRoom.setTitle(title);
                        if ( drawType == DrawType.ADD) {
                            mChatListAdapter.addItem(chatRoom);
                        } else {
                            mChatListAdapter.updateItem(chatRoom);
                        }
                    }
                    loopCount++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void leaveChat(final Chat chat) {
        final DatabaseReference messageRef = mFirebaseDb.getReference("chat_messages").child(chat.getChatId());
        Snackbar.make(getView(), "선택된 대화방을 나가시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 나의 대화방 목록에서 제거
                // users/{uid}/chats
                mChatRef.child(chat.getChatId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        // exit 메세지 발송
                        // chat_messages/{chat_id}/{message_id}/{ - 내용...~}
                        final ExitMessage exitMessage = new ExitMessage();

                        exitMessage.setMessageUser(new User(mFirebaseUser.getUid(), mFirebaseUser.getEmail(), mFirebaseUser.getDisplayName()));
                        exitMessage.setMessageDate(new Date());
                        exitMessage.setMessageId(chat.getChatId());
                        messageRef.push().setValue(exitMessage);

                        // 채팅 멤버 목록에서 제거
                        // chat_members/{chat_id}/{user_id} 제거
                        mChatMemberRef
                                .child(chat.getChatId())
                                .child(mFirebaseUser.getUid())
                                .removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        Bundle bundle = new Bundle();
                                        bundle.putString("me", mFirebaseUser.getEmail());
                                        bundle.putString("chatId", chat.getChatId());
                                        mFirebaseAnalytics.logEvent("leaveChat", bundle);

                                        // 채팅방의 멤버정보를 가져와서
                                        // 채팅방의 정보를 가져오고 (각각)
                                        // 그정보의 리스트 수정
                                        mChatMemberRef
                                                .child(chat.getChatId())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Iterator<DataSnapshot> chatMemberIterator = dataSnapshot.getChildren().iterator();

                                                        while (chatMemberIterator.hasNext()) {
                                                            User chatMember = chatMemberIterator.next().getValue(User.class);

                                                            // chats -> {uid} -> {chat_id} - { ... }
                                                            mFirebaseDb
                                                                    .getReference("users")
                                                                    .child(chatMember.getUid())
                                                                    .child("chats")
                                                                    .child(chat.getChatId())
                                                                    .child("lastMessage")
                                                                    .setValue(exitMessage);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                        mFirebaseDb.getReference("messages").child(chat.getChatId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                    }
                });

                // 메세지 unreadCount에서도 제거
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == JOIN_ROOM_REQUEST_CODE) {
            JOINED_ROOM = "";
        }
    }
    private enum DrawType  {
        ADD, UPDATE
    }
}


