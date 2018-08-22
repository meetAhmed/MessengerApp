package app.messenger.gosling.james.messengerapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import app.messenger.gosling.james.messengerapp.Adapters.inboxChatAdapter;
import app.messenger.gosling.james.messengerapp.Model.messageModel;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;

public class inboxActivity extends AppCompatActivity {

    String suspectKey, suspectName;
    RecyclerView recyclerView;
    inboxChatAdapter inboxChatAdapterObject;
    ArrayList<messageModel> messageList;
    EditText messageText;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        messageText = (EditText) findViewById(R.id.et_message);
        suspectKey = getIntent().getExtras().getString("key");
        suspectName = getIntent().getExtras().getString("name");
        getSupportActionBar().setTitle(suspectName);
        databaseRef = FirebaseDatabase.getInstance().getReference().child("messages");
        readAllMessages();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        inboxChatAdapterObject = new inboxChatAdapter(messageList, preferenceManager.getkey(getApplicationContext()));
        recyclerView.setAdapter(inboxChatAdapterObject);
    }

    private void readAllMessages() {
        messageList = new ArrayList<>();
        databaseRef.child(preferenceManager.getkey(getApplicationContext())).child(suspectKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final messageModel messageModelObject = dataSnapshot.getValue(messageModel.class);
                messageList.add(messageModelObject);
                inboxChatAdapterObject.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(View view) {
        String msg = messageText.getText().toString().trim();
        if (!TextUtils.isEmpty(msg)) {
            String key = databaseRef.child(preferenceManager.getkey(getApplicationContext())).child(suspectKey).push().getKey();
            String otherUserKey = databaseRef.child(preferenceManager.getkey(getApplicationContext())).child(suspectKey).child(preferenceManager.getkey(getApplicationContext())).push().getKey();
            messageModel messageModelObject = new messageModel(msg, preferenceManager.getkey(getApplicationContext()), suspectKey, "text", "none", key);
            messageModel messageModelObjectOther = new messageModel(msg, preferenceManager.getkey(getApplicationContext()), suspectKey, "text", "none", otherUserKey);
            Log.i("12321321",otherUserKey);
            databaseRef.child(preferenceManager.getkey(getApplicationContext())).child(suspectKey).child(key).setValue(messageModelObject);
            databaseRef.child(suspectKey).child(preferenceManager.getkey(getApplicationContext())).child(otherUserKey).setValue(messageModelObjectOther);
            messageText.setText("");
            messageText.setHint("Enter your message");
        }
    }// ends here
}
