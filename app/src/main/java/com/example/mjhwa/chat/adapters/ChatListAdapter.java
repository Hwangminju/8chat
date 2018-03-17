package com.example.mjhwa.chat.adapters;

/**
 * Created by mjhwa on 2017-11-25.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.customviews.RoundedImageView;
import com.example.mjhwa.chat.models.Chat;
import com.example.mjhwa.chat.models.Message;
import com.example.mjhwa.chat.views.ChatFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder>{

    private ArrayList<Chat> mChatList;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd\naa hh:mm");
    private ChatFragment mChatFragment;

    public ChatListAdapter() {
        mChatList = new ArrayList<>();
    }

    public void setFragment(ChatFragment chatFragment) {
        this.mChatFragment = chatFragment;
    }

    public void addItem(Chat chat) {
        mChatList.add(chat);
        notifyDataSetChanged();
    }

    public void removeItem(Chat chat) {
        int position = getItemPosition(chat.getChatId());
        if(position > -1) {
            mChatList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void updateItem(Chat chat) {
        int changedItemPosition = getItemPosition(chat.getChatId());
        mChatList.set(changedItemPosition, chat);
        notifyItemChanged(changedItemPosition);
    }

    public Chat getItem(int position) {
        return this.mChatList.get(position);
    }

    private int getItemPosition(String chatId) {
        int position = 0;
        for( Chat currItem : mChatList ) {
            if(currItem.getChatId().equals(chatId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public Chat getItem(String chatId) {
        return getItem(getItemPosition(chatId));
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_item, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {

        final Chat item = getItem(position);

        if(item.getLastMessage() != null) {
            if(item.getLastMessage().getMessageType() == Message.MessageType.TEXT) {
                holder.lastMessageView.setText(item.getLastMessage().getMessageText());
            } else if(item.getLastMessage().getMessageType() == Message.MessageType.PHOTO) {
                holder.lastMessageView.setText("(사진)");
            } else if(item.getLastMessage().getMessageType() == Message.MessageType.EXIT) {
                holder.lastMessageView.setText(String.format("%s님이 방에서 나가셨습니다.", item.getLastMessage().getMessageUser().getName()));
            } else if(item.getLastMessage().getMessageType() == Message.MessageType.EMOTICON) {
                holder.lastMessageView.setText("(이모티콘)");
            } else if(item.getLastMessage().getMessageType() == Message.MessageType.LOCATION) {
                holder.lastMessageView.setText("(현재위치)");
            }
            holder.lastMessageDateView.setText(sdf.format(item.getCreateDate()));
        }

        holder.titleView.setText(item.getTitle());
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mChatFragment != null) {
                    mChatFragment.leaveChat(item);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder{

        //RoundedImageView chatThumbnailView;

        TextView titleView;

        TextView lastMessageView;

        TextView lastMessageDateView;

        LinearLayout rootView;

        public ChatHolder(View v) {
            super(v);
            //chatThumbnailView = (RoundedImageView) v.findViewById(R.id.thumb);
            titleView = (TextView) v.findViewById(R.id.title);
            lastMessageView = (TextView) v.findViewById(R.id.lastmessage);
            lastMessageDateView = (TextView) v.findViewById(R.id.lastMsgDate);
            rootView = (LinearLayout) v.findViewById(R.id.rootView);

        }
    }
}

