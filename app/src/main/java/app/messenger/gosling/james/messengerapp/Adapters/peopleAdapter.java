package app.messenger.gosling.james.messengerapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import app.messenger.gosling.james.messengerapp.Model.user_model;
import app.messenger.gosling.james.messengerapp.R;
import app.messenger.gosling.james.messengerapp.storeRoom;


public class peopleAdapter extends RecyclerView.Adapter<peopleAdapter.myHolder> {

    Context context;
    ArrayList<user_model> modelArrayList;
    String currentIterationUID;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position, String operation);
    }

    public peopleAdapter(ArrayList<user_model> modelArrayList, OnItemClickListener onItemClickListener) {
        this.modelArrayList = modelArrayList;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_single_row, null);
        v.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        myHolder obj = new myHolder(v);
        return obj;
    }

    @Override
    public void onBindViewHolder(final myHolder holder, final int position) {
        String name = modelArrayList.get(position).getName();
        holder.username.setText(name);
        holder.date.setText(storeRoom.getDate(modelArrayList.get(position).getJoiningDate()));

        if (!modelArrayList.get(position).getProfileAddress().equalsIgnoreCase("none")) {
            Glide.with(context).load(modelArrayList.get(position).getProfileAddress()).into(holder.userDP);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position, "addToList");
            }
        });
    }


    @Override
    public int getItemCount() {
        Collections.sort(modelArrayList, user_model.COMPARE_BY_NAME);
        return modelArrayList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        TextView username, date;
        ImageView userDP;

        public myHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            username = (TextView) itemView.findViewById(R.id.username);
            date = (TextView) itemView.findViewById(R.id.date);
            userDP = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}