package com.idkwts.bruh.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idkwts.bruh.Adapter.PhotoAdapter;
import com.idkwts.bruh.ChatService.ChatRoom;
import com.idkwts.bruh.Model.Post;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.PostActivity;
import com.idkwts.bruh.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TrendingFragment extends Fragment {


    private RecyclerView recyclerViewPosts;
    private PhotoAdapter photoAdapter;
    private List<Post> postList;
    private ImageView goToSearch,post,pfp;
    private String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_trending, container, false);

        post = view.findViewById(R.id.post);
        goToSearch = view.findViewById(R.id.gotosearch);

        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        pfp = view.findViewById(R.id.pfp);
        photoAdapter = new PhotoAdapter(getContext(), postList,true);
        recyclerViewPosts.setAdapter(photoAdapter);

        goToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChatRoom.class));            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PostActivity.class));            }
        });

        SharedPreferences   prefs = getContext().getSharedPreferences("FORPFP", MODE_PRIVATE);
        String name = prefs.getString("name", "");

        Glide.with(getContext()).load(name).into(pfp);

//        protected void onCreate(Bundle savedInstanceState) {
//            final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
//            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    refreshData(); // your code
//                    pullToRefresh.setRefreshing(false);
//                }
//            });
//        }

        readPosts();

//        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
//        String profileId = prefs.getString("profileid", "none");
//        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                assert user != null;
//                Picasso.get().load(user.getImageurl()).into(pfp);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

    private void readPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                User user = new User();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                            postList.add(post);
                }
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}