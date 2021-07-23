package com.idkwts.bruh.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.idkwts.bruh.CommentActivity;
import com.idkwts.bruh.Fragments.ProfileFragment;
import com.idkwts.bruh.MainActivity;
import com.idkwts.bruh.Model.Post;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    public Boolean isFragment;
    private FirebaseUser firebaseUser;
    public Boolean check = false;


    public PhotoAdapter(Context mContext, List<Post> mPosts, Boolean isFragment) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return  new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPosts.get(position);

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("FORPFP", MODE_PRIVATE). edit();
                editor. putString("name", user.getImageurl());
                editor. apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        String imgOrNot = null;
        if (!post.getImageurl().equals("null")){
            holder.postImage.setVisibility(View.VISIBLE);
            imgOrNot = post.getImageurl();
        }
        Picasso.get().load(imgOrNot).placeholder(R.mipmap.ic_launcher).into(holder.postImage);


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){


                            case R.id.edit:

                                holder.description.setVisibility(View.GONE);
                                holder.updateDescription.setVisibility(View.VISIBLE);
                                holder.done.setVisibility(View.VISIBLE);
                                holder.updateDescription.setText(holder.description.getText().toString());
                                holder.updateDescription.requestFocus();

                                holder.done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                                .child(post.getPostid()).child("description")
                                                .setValue(holder.updateDescription.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {

                                                    holder.description.setVisibility(View.VISIBLE);
                                                    holder.updateDescription.setVisibility(View.GONE);
                                                    holder.done.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                });

                                return true;

                            case R.id.delete:
                                deletePost(post.getPostid());
                                return true;


                            case R.id.report:
                                if (checkReport(post.getPostid())){
                                    report(post.getPostid());
                                } else {
                                    Toast.makeText(mContext, "Post Already Reported.", Toast.LENGTH_SHORT).show();
                                }
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                }
                popupMenu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCommentActivity(post);
            }
        });

        holder.description.setText(post.getDescription());
        nrLikes(holder.nrOfLikes, post.getPostid());
        nrOfComments(holder.nrComment,post.getPostid());
        isLiked(post.getPostid(), holder.like);
        isSaved(post.getPostid(), holder.save);
        authorInfo(holder.imageProfile, holder.username, post.getPublisher(), post.getAnonymous());


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostid(), post.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        if (post.getAnonymous()){

        } else {
            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("authorid", post.getPublisher());
                        mContext.startActivity(intent);
                    }
            });

            holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFragment){
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                    } else{
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("authorid", post.getPublisher());
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });


    }

    private void sendToCommentActivity(Post post) {
        mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();
        mContext.startActivity(new Intent(mContext, CommentActivity.class));


        String imageUriFromAdapter = null,postDescriptionfromAdapter;

        if (!post.getImageurl().equals("null")){
            imageUriFromAdapter = post.getImageurl();
        }else{
            imageUriFromAdapter = "null";
        }
        postDescriptionfromAdapter = post.getDescription();

        SharedPreferences.Editor editor1 = mContext.getSharedPreferences("Position", MODE_PRIVATE). edit();
        editor1. putString("des", postDescriptionfromAdapter);
        editor1. putString("postImg", imageUriFromAdapter);
        editor1. putString("postId", post.getPostid());
        editor1. apply();
    }


    private Boolean checkReport(final String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                check = !dataSnapshot.child(postid).exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return check;
    }

    private void report(String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reports").child(postid);
        reference.setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(mContext, "Post Reported !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deletePost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Delete Post?");

        alertDialog.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
                        reference.removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Post Deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImage,more;
        public CircleImageView imageProfile;
        public ImageView like;
        public TextView nrOfLikes,nrComment;
        public ImageView comment;
        public ImageView save;
        public Button done;
        public EditText updateDescription;
        public TextView description,username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            more = itemView.findViewById(R.id.more);
            nrComment = itemView.findViewById(R.id.no_of_comments);
            nrOfLikes = itemView.findViewById(R.id.no_of_likes);
            postImage = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.description);
            done = itemView.findViewById(R.id.done);
            updateDescription = itemView.findViewById(R.id.editinput);
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
        }
    }

    private void authorInfo(final ImageView imageProfile, final TextView username, String publisher, final Boolean anonym) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisher);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(anonym){
                    Glide.with(mContext).load(R.mipmap.ic_launcher).into(imageProfile);
                    username.setText("anonymous");
                } else{
                    assert user != null;
                    Glide.with(mContext).load(user.getImageurl()).into(imageProfile);
                    username.setText(user.getUsername());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNotification(String postId, String publisherId) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("userid", publisherId);
        map.put("text", "liked your post.");
        map.put("postid", postId);
        map.put("isPost", true);

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
    }

    private void isSaved (final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    image.setImageResource(R.drawable.ic_save_black);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrOfComments(final TextView nrComment, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nrComment.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
