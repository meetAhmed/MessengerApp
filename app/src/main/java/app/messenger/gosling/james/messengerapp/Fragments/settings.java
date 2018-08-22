package app.messenger.gosling.james.messengerapp.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import app.messenger.gosling.james.messengerapp.MainActivity;
import app.messenger.gosling.james.messengerapp.R;
import app.messenger.gosling.james.messengerapp.preferenceManager.preferenceManager;
import app.messenger.gosling.james.messengerapp.starterActivity;
import app.messenger.gosling.james.messengerapp.storeRoom;

import static android.app.Activity.RESULT_OK;

public class settings extends Fragment {

    ImageView logOut, done, user_dp;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText username;
    String name = null;
    Uri finalUri = null;
    String finalPicturePath = null;
    Integer RESULT_LOAD_IMG = 109;
    byte[] imgBytes;
    Bitmap bitmap = null;
    boolean taskProfile = false;
    boolean profileChosen = false;
    boolean profileShown = false;
    String profileAddressServer = "none";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        logOut = (ImageView) view.findViewById(R.id.logOut);
        done = (ImageView) view.findViewById(R.id.done);
        user_dp = (ImageView) view.findViewById(R.id.user_dp);
        username = (EditText) view.findViewById(R.id.username);
        return view;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Call your Fragment functions that uses getActivity()
        FirebaseDatabase.getInstance().getReference().child("users").child(preferenceManager.getkey(getActivity())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                username.setText(name);
                profileAddressServer = dataSnapshot.child("profileAddress").getValue().toString();
                if (!profileAddressServer.equalsIgnoreCase("none") && !profileChosen && !profileShown) {
                    Glide.with(getActivity()).load(profileAddressServer).into(user_dp);
                    profileShown = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                if (MainActivity.MainActivityObject != null) {
                    MainActivity.MainActivityObject.finish();
                }
                preferenceManager.setKey(getActivity(), "nothing");
                startActivity(new Intent(getActivity(), starterActivity.class));
            }
        });

        user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskProfile = false;
                if (isReadStorageAllowed()) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMG);
                } else {
                    Toast.makeText(getActivity(), "We do not have permission to look into your Gallery", Toast.LENGTH_LONG).show();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            try {
                Uri imageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                bitmap = BitmapFactory.decodeFile(picturePath);
                finalUri = imageUri;
                finalPicturePath = picturePath;
                bitmap = storeRoom.resize(bitmap, 1200, 1200);
                imgBytes = storeRoom.getBytes(bitmap);
                user_dp.setImageBitmap(bitmap);
                profileChosen = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // if ends here
    }

    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    public void uploadProfile() {

        if (name != null) {
            if (!username.getText().toString().trim().equals(name.trim()) && username.getText().toString().trim().length() > 0) {
                FirebaseDatabase.getInstance().getReference().child("users").child(preferenceManager.getkey(getActivity()))
                        .child("name").setValue(username.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                        preferenceManager.writeUserName(getActivity(), username.getText().toString().trim());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to update name\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (taskProfile) {
                Toast.makeText(getActivity(), "Picture already uploaded", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (imgBytes == null || finalUri == null) {
            return;
        }
        if (taskProfile) {
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Uploading profile picture");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference().child("display-pictures");
        final String name = System.currentTimeMillis() + "-dp." + storeRoom.getFileType(finalUri, getActivity());

        firebaseStorage.child(name).putBytes(imgBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                firebaseStorage.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("users").child(preferenceManager.getkey(getActivity()))
                                .child("profileAddress").setValue(uri.toString());
                    }
                });
                taskProfile = true;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Picture uploaded", Toast.LENGTH_LONG).show();
            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + " %");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error occurred while uploading picture", Toast.LENGTH_LONG).show();
            }
        });
    }
}
