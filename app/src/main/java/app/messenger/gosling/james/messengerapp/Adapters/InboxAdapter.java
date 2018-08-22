package app.messenger.gosling.james.messengerapp.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import app.messenger.gosling.james.messengerapp.Model.messageModel;
import app.messenger.gosling.james.messengerapp.R;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;
import app.messenger.gosling.james.messengerapp.storeRoom;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.myHolder> {

    Context context;
    ArrayList<messageModel> modelArrayList, sortedList;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String name, String operation);
    }

    public InboxAdapter(ArrayList<messageModel> modelArrayList, OnItemClickListener onItemClickListener) {
        this.modelArrayList = modelArrayList;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_outbox_layer, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        myHolder obj = new myHolder(v);
        return obj;
    }

    @Override
    public void onBindViewHolder(final myHolder holder, final int position) {

        if (modelArrayList.get(position).getSender().trim().equals(preferenceManager.getkey(context))) {
            FirebaseDatabase.getInstance().getReference().child("users").child(modelArrayList.get(position).getReceiver()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Log.i("wee123213", dataSnapshot.getValue() + "");
                        String name = dataSnapshot.child("name").getValue().toString();
                        if (name.charAt(0) != '@') {
                            name = "@" + name;
                        }
                        holder.username.setText(name);
                        String profileAddress = dataSnapshot.child("profileAddress").getValue().toString();
                        if (!profileAddress.equalsIgnoreCase("none")) {
                            Glide.with(context).load(profileAddress).into(holder.userDP);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            // receiver
            FirebaseDatabase.getInstance().getReference().child("users").child(modelArrayList.get(position).getSender()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    holder.username.setText(name);
                    String profileAddress = dataSnapshot.child("profileAddress").getValue().toString();
                    if (!profileAddress.equalsIgnoreCase("none")) {
                        Glide.with(context).load(profileAddress).into(holder.userDP);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        holder.text.setText(modelArrayList.get(position).getMessageText());
        holder.date.setText(storeRoom.getTimeForMessages(modelArrayList.get(position).getMessageTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position, holder.username.getText().toString().trim(), "openConvo");
            }
        });


    }

    @Override
    public int getItemCount() {
        Collections.sort(modelArrayList, messageModel.COMPARE_BY_TIME);
        Collections.reverse(modelArrayList);
        return modelArrayList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        TextView username, date, text;
        ImageView userDP;

        public myHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            username = (TextView) itemView.findViewById(R.id.username);
            text = (TextView) itemView.findViewById(R.id.text);
            date = (TextView) itemView.findViewById(R.id.date);
            userDP = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}