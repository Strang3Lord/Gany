package com.idkwts.bruh.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.idkwts.bruh.Adapter.PhotoAdapter;
import com.idkwts.bruh.EditProfileActivity;
import com.idkwts.bruh.FollowersActivity;
import com.idkwts.bruh.Model.Post;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.OptionsActivity;
import com.idkwts.bruh.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerViewSaves;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPosts;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPostList;

    private Uri mImageUri;
    private CircleImageView imageProfile;
    private ImageView options;
    private TextView followers;
    private TextView following;
    private TextView posts;
    private TextView bio;
    private TextView username;
    private Button myPictures;
    private androidx.appcompat.widget.Toolbar bg;
    private Button savedPictures,tagged;
    private Button editProfile,follow;
    private FirebaseUser fUser;
    private AdView mAdView;
    String profileId,data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

//        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profileId = prefs.getString("profileid", "none");


        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        if (data.equals("none")) {
//            profileId = fUser.getUid();
//        } else {
//            profileId = data;
//            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
//        }

        imageProfile = view.findViewById(R.id.image_profile);
        tagged = view.findViewById(R.id.tagged);
        follow = view.findViewById(R.id.Follow);
//        options = view.findViewById(R.id.options);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        bio = view.findViewById(R.id.bio);
        bg = view.findViewById(R.id.bg);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recucler_view_pictures);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        myPostList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), myPostList ,true);
        recyclerView.setAdapter(photoAdapter);
        recyclerViewSaves = view.findViewById(R.id.recucler_view_saved);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewSaves.setLayoutManager(linearLayoutManager1);
        mySavedPosts = new ArrayList<>();
        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPosts,true);
        recyclerViewSaves.setAdapter(postAdapterSaves);

        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPosts();
        getSavedPosts();


        if (profileId.equals(fUser.getUid())) {
            editProfile.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
        }
        else {
            editProfile.setVisibility(View.GONE);
            follow.setVisibility(View.VISIBLE);
            checkFollowingStatus();
            savedPictures.setVisibility(View.GONE);
            tagged.setVisibility(View.GONE);
            myPictures.setVisibility(View.GONE);
        }


        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow.getText().toString().equals(("Follow"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(fUser.getUid()).child("following").child(profileId).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(profileId).child("followers").child(fUser.getUid()).setValue(true);

                    follow.setText("Following ✓");
                    addNotification(profileId);

                } else if (follow.getText().toString().equals(("Following ✓"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((fUser.getUid())).child("following").child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(profileId).child("followers").child(fUser.getUid()).removeValue();


                    follow.setText("Follow");
                }
            }
        });


        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });


        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "followings");
                startActivity(intent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });

        return view;
    }

    private void getSavedPosts() {

        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        mySavedPosts.clear();
                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            Post post = snapshot1.getValue(Post.class);

                            for (String id : savedIds) {
                                if (post.getPostid().equals(id)) {
                                    mySavedPosts.add(post);
                                }
                            }
                        }

                        postAdapterSaves.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPostList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   Post post = snapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPublisher().equals(profileId)) {
                        myPostList.add(post);
                    }
                }
                Collections.reverse(myPostList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void checkFollowingStatus() {

        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()) {
                    editProfile.setText("following");
                } else {
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPostCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)) counter ++;
                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowersAndFollowingCount() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);

        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if(user.getImageurl().equals("default")){
                    Picasso.get().load(R.drawable.ic_launcher_foreground).into(imageProfile);
                }else {
                    Picasso.get().load(user.getImageurl()).into(imageProfile);

                }
                username.setText(user.getUsername());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addNotification(String userId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", userId);
        map.put("text", "started following you.");
        map.put("postid", "");
        map.put("isPost", false);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(fUser.getUid()).push().setValue(map);
    }

    private void uploadImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        StorageTask uploadTask;

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return  fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("Bg").setValue(url);
                        pd.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Upload failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        } else {
            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}

