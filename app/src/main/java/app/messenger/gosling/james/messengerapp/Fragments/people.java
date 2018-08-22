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

import java.util.ArrayList;

import app.messenger.gosling.james.messengerapp.Adapters.peopleAdapter;
import app.messenger.gosling.james.messengerapp.Model.user_model;
import app.messenger.gosling.james.messengerapp.R;
import app.messenger.gosling.james.messengerapp.inboxActivity;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;


public class people extends Fragment {

    RecyclerView peopleList;
    peopleAdapter ob;
    ArrayList<user_model> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);

        peopleList = (RecyclerView) view.findViewById(R.id.recViewer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        peopleList.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        ob = new peopleAdapter(arrayList, new peopleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String operation) {

                if (operation.equals("addToList")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", arrayList.get(position).getName().trim());
                    bundle.putString("key", arrayList.get(position).getKey().trim());
                    Intent intent = new Intent(getActivity(), inboxActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        peopleList.setAdapter(ob);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Call your Fragment functions that uses getActivity()
        readUsers();
    }

    public void readUsers() {
        if (arrayList != null) {
            arrayList.clear();
        }
        //preferenceManager.getkey(getActivity()).trim()
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot data, String s) {
                final user_model userObj = data.getValue(user_model.class);
                if (!preferenceManager.getkey(getActivity()).trim().equals(userObj.getKey().trim())) {
                    arrayList.add(userObj);
                    ob.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String nodeAddress = dataSnapshot.child("key").getValue().toString();
                for (user_model user : arrayList) {
                    if (user.getKey().equals(nodeAddress)) {
                        arrayList.remove(user);
                        ob.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.i("123123", "method called");

    }
}
