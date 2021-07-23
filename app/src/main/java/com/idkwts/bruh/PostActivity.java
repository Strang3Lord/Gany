package com.idkwts.bruh;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.idkwts.bruh.Model.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    private String imageUrl;
    Switch switch1,img_switch;
    Boolean anonym = false;
    private ImageView image_profile;
    private ImageView imageAdded;
    private TextView post,username;
    boolean imgOrNot;
    SocialAutoCompleteTextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        username = findViewById(R.id.username);
        switch1 = findViewById(R.id.switch1);
        img_switch = findViewById(R.id.img_switch);
        image_profile = findViewById(R.id.image_profile);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If the toggle is ON
                if(switch1.isChecked()){
                    username.setText("anonymous");      // set username to anonymous
                    Glide.with(getApplicationContext()).load(R.mipmap.ic_launcher).into(image_profile);
                    anonym = true;
                } else
                {
                    authorInfo(image_profile,username, FirebaseAuth.getInstance().getCurrentUser().getUid());   // get details of the user signed IN
                    anonym = false;
                }
            }
        });
        img_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If the toggle is ON
                if(img_switch.isChecked()){
                    imageAdded.setVisibility(View.VISIBLE);
                    imgOrNot = true;
                }else if(!img_switch.isChecked()) {
                    imageAdded.setVisibility(View.INVISIBLE);
                    imgOrNot = false;
                }
            }
        });

        authorInfo(image_profile,username, FirebaseAuth.getInstance().getCurrentUser().getUid());


        imageAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
                       .setInitialCropWindowPaddingRatio(0)
                       .setAspectRatio(1, 1)
                       .setGuidelines(CropImageView.Guidelines.ON)
                       .start(PostActivity.this);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

    }

    private void upload() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadtask = filePath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("description", description.getText().toString());
                    map.put("imageurl", imageUrl);
                    map.put("postid", postId);
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    map.put("anonymous", anonym);

                    ref.child(postId).setValue(map);

                    DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags = description.getHashtags();
                    if (!hashTags.isEmpty()) {
                        for (String tag : hashTags) {
                            map.clear();
                            map.put("tag", tag.toLowerCase());
                            map.put("postid", postId);

                            mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }
                    }

                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageUrl = "null";
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            String postId = ref.push().getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("description", description.getText().toString());
            map.put("imageurl", imageUrl);
            map.put("postid", postId);
            map.put("publisher", getU(username.getText().toString()));
            map.put("anonymous", anonym);

            ref.child(postId).setValue(map);

            DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
            List<String> hashTags = description.getHashtags();
            if (!hashTags.isEmpty()) {
                for (String tag : hashTags) {
                    map.clear();
                    map.put("tag", tag.toLowerCase());
                    map.put("postid", postId);

                    mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                }
            }

            pd.dismiss();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }

    private Object getU(String toString) {
        String result = "";
        result = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return result;
    }


    private void authorInfo(final ImageView image_profile, final TextView username, String userid){
        // get the database reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        // get a value event listener
        // when we get some value
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // get the user data from database to User class
                User user = dataSnapshot.getValue(User.class);
                // get the profile image with glide(library)
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());       // get the username

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageAdded.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    hashtagAdapter.add(new Hashtag(snapshot.getKey() , (int) snapshot.getChildrenCount()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        description.setHashtagAdapter(hashtagAdapter);
    }
}