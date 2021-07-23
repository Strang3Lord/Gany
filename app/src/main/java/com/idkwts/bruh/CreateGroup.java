package com.idkwts.bruh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.idkwts.bruh.Adapter.GroupMemberAdapter;
import com.idkwts.bruh.ChatService.ChatList;
import com.idkwts.bruh.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGroup extends AppCompatActivity {
    private RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    private Button createGroup;
    private GroupMemberAdapter grpMembersAdapter;
    DatabaseReference reference;
    String grpId;


    private List<User> mUsers, mMembers;

    FirebaseUser fuser;
    private EditText grpName;
    private List<ChatList> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        grpName = findViewById(R.id.edTxtGrpName);

        recyclerView = findViewById(R.id.recycler_select_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        findViewById(R.id.btnCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference grpRef;
                grpRef = FirebaseDatabase.getInstance().getReference("Groups");
                grpRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        grpId = grpRef.push().getKey();
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("GroupId", grpId);
                        map.put("GroupName", grpName.getText());
                        assert grpId != null;
                        grpRef.child(grpId).setValue(map);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mMembers = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final User user = snapshot.getValue(User.class);
                            assert user != null;
                            if (user.isSelected()){
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child("Members");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("members", user.getId());
                                        ref.child(grpId).setValue(hashMap);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


//                            user.setSelected(false);
                        }
                    }
                    //     GrpMembersAdapter.notifyDataSetChanged();
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//
//                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("userid", grpId);
//                       startActivity(intent);


            }

        });

        //readusers
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
                mUsers = new ArrayList<>();
                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            for (ChatList chatList : userList) {
                                if (user != null && user.getId() != null && user.getId().equals(chatList.getId()))
                                {
                                    mUsers.add(user);
                                }
                            }
                        }
                        grpMembersAdapter = new GroupMemberAdapter(CreateGroup.this, mUsers);
                        recyclerView.setAdapter(grpMembersAdapter);
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

    private void SelectFunction() {


    }
}


