package com.idkwts.bruh;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.idkwts.bruh.Adapter.CommentAdapter;
import com.idkwts.bruh.Model.Comment;
import com.idkwts.bruh.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView CommentrecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private EditText addComment;
    private CircleImageView commentImageProfile;
    private TextView post;

    private String postId;
    private String authorId;

    public ImageView imageProfile;
    public ImageView postImage;
    public ImageView like;
    public ImageView comment;
    public ImageView save;
    public ImageView more;

    public TextView username;
    public TextView noOfLikes;
    //        public TextView author;
    public TextView noOfComments;
    TextView description;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        SharedPreferences prefs = getSharedPreferences("Position", MODE_PRIVATE);
        postId = prefs.getString("postId", "");

        CommentrecyclerView = findViewById(R.id.comment_recycler_view);
        CommentrecyclerView.setHasFixedSize(true);
        CommentrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);
//        postList = new ArrayList<>();
//        postAdapter = new PostAdapter(this, postList);
        CommentrecyclerView.setAdapter(commentAdapter);

        imageProfile = findViewById(R.id.image_profile);
        postImage = findViewById(R.id.post_image);
        like = findViewById(R.id.like);
        comment = findViewById(R.id.comment);
        save = findViewById(R.id.save);
        more = findViewById(R.id.more);

        username = findViewById(R.id.username);
        noOfLikes = findViewById(R.id.no_of_likes);
        noOfComments = findViewById(R.id.no_of_comments);
        description = findViewById(R.id.description);

        addComment = findViewById(R.id.add_comment);
        commentImageProfile = findViewById(R.id.comment_image_profile);
        post = findViewById(R.id.post);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();
        getPostDetail();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addComment.getText().toString())) {
                    Toast.makeText(CommentActivity.this, "No comment added!", Toast.LENGTH_SHORT).show();
                } else {
                    putComment();
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void putComment() {

        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id = ref.push().getKey();
        map.put("id", id);
        map.put("comment", addComment.getText().toString());
        map.put("publisher", fUser.getUid());

        addComment.setText("");

        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Comment added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getPostDetail(){

        SharedPreferences prefs = getSharedPreferences("Position", MODE_PRIVATE);
        String des = prefs.getString("des", "");
        String postImg = prefs.getString("postImg", "");
        if(!postImg.equals("null")){
            postImage.setVisibility(View.VISIBLE);
            Picasso.get().load(postImg).into(postImage);
        }else{
            postImage.setVisibility(View.GONE);
        }
        description.setText(des);
    }

    private void getUserImage() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                if (user.getImageurl().equals("default")) {
                    commentImageProfile.setImageResource(R.mipmap.ic_launcher);
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Picasso.get().load(user.getImageurl()).into(commentImageProfile);
                    username.setText(user.getUsername());
                    Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
