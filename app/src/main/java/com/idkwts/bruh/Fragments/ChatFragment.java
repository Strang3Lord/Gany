package com.idkwts.bruh.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idkwts.bruh.ChatService.ChatList;
import com.idkwts.bruh.ChatService.ChatUserAdapter;
import com.idkwts.bruh.ChatService.SearchActivity;
import com.idkwts.bruh.CreateGroup;
import com.idkwts.bruh.MainActivity;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    private List<User> mUsers;
    FirebaseUser fuser;
    DatabaseReference reference;
    private List<ChatList> userList;
    FloatingActionButton fb;
    private RecyclerView recyclerViewChat,recyclerViewUsers;
    private ChatUserAdapter userAdapter;
    private List<User> mUser;
    CircleImageView profile_image;
    Button search;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerViewChat = view.findViewById(R.id.recycler_view_chat);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        fb = view.findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateGroup.class));
            }
        });

        userList = new ArrayList<>();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CreateGroup.class));
            }
        });
        //readusers
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
                chatList();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        profile_image = view.findViewById(R.id.pfp);
        search = view.findViewById(R.id.searchnewusers);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        mUser = new ArrayList<>();
        recyclerViewChat = view.findViewById(R.id.recycler_view_chat);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ChatFragment.class));
            }
        });

//        //for pfp
//        reference.addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ChatitUser user = dataSnapshot.getValue(ChatitUser.class);
//                if (Objects.requireNonNull(user).getImageURL().equals("default")) {
//                    profile_image.setImageResource(R.drawable.yawn);
//                } else {
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

    private void chatList(){

        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatList:userList){
                        if (user!=null && user.getId() != null && user.getId().equals(chatList.getId()))
//                            if (user.getId().equals(chatList.getId()))
                        {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new ChatUserAdapter(getContext(), mUsers,true);
                recyclerViewChat.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            startActivity(new Intent(getContext(), MainActivity.class));

        }
    }

    //to confirm weather the user is logged in to the app or not
    public void status(String status)
    {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }



     @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }
}