package com.indresh.mywhatsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.indresh.mywhatsapp.Model.Chat;
import com.indresh.mywhatsapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    static final int MSG_TYPE_LEFT=0;
    static final int MSG_TYPE_RIGHT=1;

    Context context;
    List<Chat> mChat;
    String imageURL;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> mChat, String imageURL) {
        this.context = context;
        this.mChat = mChat;
        this.imageURL = imageURL;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view=LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat=mChat.get(position);
        holder.show_Message.setText(chat.getMessage());
        if(imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }
        if(position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setText("seen");
            }else{
                holder.txt_seen.setText("Delivered");
            }
        }
        else {
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView show_Message;
        ImageView profile_image;
        TextView txt_seen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_Message=itemView.findViewById(R.id.show_msg);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}
