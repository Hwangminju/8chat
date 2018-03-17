package com.example.mjhwa.chat.adapters;

/**
 * Created by mjhwa on 2017-11-25.
 */


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mjhwa.chat.R;
import com.example.mjhwa.chat.models.EmoticonMessage;
import com.example.mjhwa.chat.models.LocationMessage;
import com.example.mjhwa.chat.models.Message;
import com.example.mjhwa.chat.models.PhotoMessage;
import com.example.mjhwa.chat.models.TextMessage;
import com.example.mjhwa.chat.views.GeoActivity;
import com.example.mjhwa.chat.views.MakeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder> implements Filterable {

    LayoutInflater inflater;
    private List<Message> orig;
    private ArrayList<Message> mMessageList;
    private ArrayList<Message> mMessageListFiltered;
    private SimpleDateFormat messageDateFormat = new SimpleDateFormat("MM/dd\n hh:mm");

    private String userId;

    public MessageListAdapter(Context context, ArrayList<Message> message) {
        inflater = LayoutInflater.from(context);
        this.mMessageList = message;
    }

    public MessageListAdapter() {
        mMessageList = new ArrayList<>();
        mMessageListFiltered = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void addItem(Message item) {
        mMessageList.add(item);
        notifyDataSetChanged();
    }
    public void updateItem(Message item) {
        int position = getItemPosition(item.getMessageId());
        if(position < 0) {
            return;
        }
        mMessageList.set(position, item);
        notifyItemChanged(position);
    }

    public void clearItem() {
        mMessageList.clear();
    }

    private int getItemPosition(String messageId) {
        int position = 0;

        for(Message message : mMessageList) {
            if(message.getMessageId().equals(messageId)) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public Message getItem(int position) {
        return mMessageList.get(position);
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message_item, parent, false);
        // viewHolder를 이용한 뷰홀더 리턴
        return new MessageViewHolder(view);
    }

    public Message getFilteredItem(int position) {return mMessageListFiltered.get(position); }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        // 전달받은 뷰 홀더를 이용한 뷰 구현
        Message item = getItem(position);

        TextMessage textMessage = null;
        PhotoMessage photoMessage = null;
        EmoticonMessage emoticonMessage = null;
        LocationMessage locationMessage = null;

        if(item instanceof TextMessage) {
            textMessage = (TextMessage) item;
        }
        else if(item instanceof PhotoMessage) {
            photoMessage = (PhotoMessage) item;
        }
        else if(item instanceof EmoticonMessage) {
            emoticonMessage = (EmoticonMessage) item;
        }
        else if(item instanceof LocationMessage) {
            locationMessage = (LocationMessage) item;
        }

        // 내가 보낸 메세지인지 받은 메세지인지 판별
        if(userId.equals(item.getMessageUser().getUid())) {
            // 내가보낸 메세지 구현
            // 텍스트 메세지인지 포토 메세지인지 구별
            if(item.getMessageType() == Message.MessageType.TEXT) {
                holder.sendTxt.setText(textMessage.getMessageText());
                holder.sendTxt.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.GONE);
                holder.sendEmoticon.setVisibility(View.GONE);

            }
            else if(item.getMessageType() == Message.MessageType.PHOTO) {

                Glide.with(holder.sendArea.getContext())
                        .load(photoMessage.getPhotoUrl())
                        .override(300, 300)
                        .into(holder.sendImage);

                holder.sendTxt.setVisibility(View.GONE);
                holder.sendImage.setVisibility(View.VISIBLE);
                holder.sendEmoticon.setVisibility(View.GONE);
            }
            else if(item.getMessageType() == Message.MessageType.EMOTICON) {

                Glide.with(holder.sendArea.getContext())
                        .load(emoticonMessage.getEmoticonUrl())
                        .override(400, 450)
                        .into(holder.sendEmoticon);

                holder.sendTxt.setVisibility(View.GONE);
                holder.sendImage.setVisibility(View.GONE);
                holder.sendEmoticon.setVisibility(View.VISIBLE);
            }
            else if(item.getMessageType() == Message.MessageType.LOCATION) {
                holder.sendTxt.setText(locationMessage.getMessageText());
                holder.sendTxt.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.GONE);
                holder.sendEmoticon.setVisibility(View.GONE);
            }
            holder.sendDate.setText(messageDateFormat.format(item.getMessageDate()));

            holder.yourArea.setVisibility(View.GONE);
            holder.sendArea.setVisibility(View.VISIBLE);
        } else {
            //상대방이 보낸 경우
            if(item.getMessageType() == Message.MessageType.TEXT) {

                holder.rcvTextView.setText(textMessage.getMessageText());
                holder.rcvTextView.setVisibility(View.VISIBLE);
                holder.rcvImage.setVisibility(View.GONE);
                holder.rcvEmoticon.setVisibility(View.GONE);
                holder.rcvLocationBtn.setVisibility(View.GONE);

            }
            else if(item.getMessageType() == Message.MessageType.PHOTO) {
                Glide.with(holder.yourArea.getContext())
                        .load(photoMessage.getPhotoUrl())
                        .override(300, 300)
                        .into(holder.rcvImage);

                holder.rcvTextView.setVisibility(View.GONE);
                holder.rcvLocationBtn.setVisibility(View.GONE);
                holder.rcvImage.setVisibility(View.VISIBLE);
                holder.rcvEmoticon.setVisibility(View.GONE);
            }
            else if(item.getMessageType() == Message.MessageType.EXIT) {
                holder.exitTextView.setText(String.format("%s님이 방에서 나가셨습니다.", item.getMessageUser().getName()));
                holder.exitArea.setVisibility(View.VISIBLE);
            }
            else if(item.getMessageType() == Message.MessageType.EMOTICON) {
                Glide.with(holder.yourArea.getContext())
                        .load(emoticonMessage.getEmoticonUrl())
                        .override(400, 450)
                        .into(holder.rcvEmoticon);

                holder.rcvTextView.setVisibility(View.GONE);
                holder.rcvImage.setVisibility(View.GONE);
                holder.rcvEmoticon.setVisibility(View.VISIBLE);
                holder.rcvLocationBtn.setVisibility(View.GONE);
            }
            else if(item.getMessageType() == Message.MessageType.LOCATION) {
                final String latitude = locationMessage.getLatitude();
                final String lontitude = locationMessage.getLontitude();

                holder.rcvLocationBtn.setVisibility(View.VISIBLE);
                holder.rcvImage.setVisibility(View.GONE);
                holder.rcvTextView.setVisibility(View.GONE);
                holder.rcvEmoticon.setVisibility(View.GONE);
                holder.rcvLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), GeoActivity.class);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("lontitude", lontitude);
                        v.getContext().startActivity(intent);
                    }
                });
            }

            if(item.getMessageType() == Message.MessageType.EXIT) {
                holder.yourArea.setVisibility(View.GONE);
                holder.sendArea.setVisibility(View.GONE);
                holder.exitArea.setVisibility(View.VISIBLE);
            } else {
                holder.rcvDate.setText(messageDateFormat.format(item.getMessageDate()));
                holder.yourArea.setVisibility(View.VISIBLE);
                holder.sendArea.setVisibility(View.GONE);
            }
            // 텍스트 메세지인지 포토 메세지인지 구별

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout yourArea;
        LinearLayout sendArea;
        LinearLayout exitArea;
        ImageView rcvProfileView;
        TextView exitTextView;
        TextView rcvTextView;
        ImageView rcvImage;
        ImageView rcvEmoticon;
        Button rcvLocationBtn;
        TextView rcvDate;
        TextView sendDate;
        TextView sendTxt;
        ImageView sendImage;
        ImageView sendEmoticon;

        public MessageViewHolder(View v) {
            super(v);
            yourArea = (LinearLayout) v.findViewById(R.id.yourChatArea);
            sendArea = (LinearLayout) v.findViewById(R.id.myChatArea);
            exitArea = (LinearLayout) v.findViewById(R.id.exitArea);
            rcvProfileView = (ImageView) v.findViewById(R.id.rcvProfile);
            exitTextView = (TextView) v.findViewById(R.id.exitTxt);
            rcvTextView = (TextView) v.findViewById(R.id.rcvTxt);
            rcvImage = (ImageView) v.findViewById(R.id.rcvImage);
            rcvEmoticon = (ImageView) v.findViewById(R.id.rcvEmoticon);
            rcvLocationBtn = (Button) v.findViewById(R.id.rcvLocationButton);
            rcvDate = (TextView) v.findViewById(R.id.rcvDate);
            sendDate = (TextView) v.findViewById(R.id.sendDate);
            sendTxt = (TextView) v.findViewById(R.id.sendTxt);
            sendImage = (ImageView) v.findViewById(R.id.sendImage);
            sendEmoticon = (ImageView) v.findViewById(R.id.sendEmoticon);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filterResults = new FilterResults();
                final List<Message> results = new ArrayList<Message>();
                if (orig == null)
                    orig = mMessageList;
                    if (constraint != null) {
                        if (orig != null & orig.size() > 0) {
                            for (final Message g : orig) {
                                if (messageDateFormat.format(g.getMessageDate()).toString().toLowerCase().contains(constraint.toString()))
                                    results.add(g);
                            }
                        }
                        filterResults.values = results;
                    }
                    return filterResults;
                }

                /*if (constraint == null || constraint.length() == 0) {
                    mMessageListFiltered = mMessageList;
                } else {
                    ArrayList<Message> messageListFiltered = new ArrayList<>();

                    for (Message s : mMessageList) {
                        if (s.getMessageDate().toString().contains(charString)) {

                            messageListFiltered.add(s);
                        }
                    }

                    mMessageListFiltered = messageListFiltered;
                }


                filterResults.values = mMessageListFiltered;
                return filterResults;*/

                @Override
                protected void publishResults (CharSequence constraint, FilterResults results){
                    mMessageList = (ArrayList<Message>) results.values;
                    notifyDataSetChanged();
                }
            };
    }
}
