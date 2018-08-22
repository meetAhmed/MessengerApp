package app.messenger.gosling.james.messengerapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app.messenger.gosling.james.messengerapp.Adapters.InboxAdapter;
import app.messenger.gosling.james.messengerapp.Model.messageModel;
import app.messenger.gosling.james.messengerapp.R;
import app.messenger.gosling.james.messengerapp.inboxActivity;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;


public class Inbox extends Fragment {

    RecyclerView peopleList;
    InboxAdapter ob;
    ArrayList<messageModel> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        arrayList = new ArrayList<>();
        peopleList = (RecyclerView) view.findViewById(R.id.recViewer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        peopleList.setLayoutManager(layoutManager);
        ob = new InboxAdapter(arrayList, new InboxAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String name, String operation) {

                if (operation.equals("openConvo")) {

                    String key = null;
                    if (preferenceManager.getkey(getActivity()).trim().equals(arrayList.get(position).getSender())) {
                        key = arrayList.get(position).getReceiver().trim();
                    } else {
                        key = arrayList.get(position).getSender().trim();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("key", key);
                    Intent intent = new Intent(getActivity(), inboxActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        peopleList.setAdapter(ob);
        return view;
    }

    public void readUsers() {
        if (arrayList != null) {
            arrayList.clear();
        }
        //preferenceManager.getkey(getActivity()).trim()
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("messages")
                .child(preferenceManager.getkey(getActivity()));
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot data, String s) {


                final Query lastQuery = databaseReference.child(data.getKey()).orderByKey().limitToLast(1);
                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            messageModel messageModelObject = data.getValue(messageModel.class);
                            Log.i("123123", messageModelObject.getNodeAddress() + "");
                            arrayList.add(messageModelObject);
                            ob.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Handle possible errors.
                    }
                });

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

    @Override
    public void onStart() {
        super.onStart();

        //Call your Fragment functions that uses getActivity()
        readUsers();
    }
}
