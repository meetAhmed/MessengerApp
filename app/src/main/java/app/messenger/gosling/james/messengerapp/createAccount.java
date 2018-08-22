package app.messenger.gosling.james.messengerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.messenger.gosling.james.messengerapp.Model.user_model;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;

public class createAccount extends AppCompatActivity {

    EditText email, password, name;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        getSupportActionBar().setTitle("Sign Up");

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_access, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            accessAccount();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            if (starterActivity.starterActivityObject != null) {
                starterActivity.starterActivityObject.finish();
            }
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }


    public void accessAccount() {

        if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Provide all data", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating account");
        dialog.show();

        auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                    String key = ref.push().getKey();
                    user_model objUser = new user_model(name.getText().toString(), email.getText().toString(), password.getText().toString(), key);
                    ref.child(key).setValue(objUser);
                    dialog.dismiss();
                    preferenceManager.setKey(getApplicationContext(), key);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

}
