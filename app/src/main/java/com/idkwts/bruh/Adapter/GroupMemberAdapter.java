package com.idkwts.bruh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.R;

import java.util.HashMap;
import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    FirebaseUser fUser;
    DatabaseReference reference ;
    boolean status;


    public GroupMemberAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_user_item, parent, false);
        return new GroupMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());


        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());
        final HashMap<String, Object> hashMap = new HashMap<>();

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                        status = true ;
                    hashMap.put("isSelected",status);
                    reference.updateChildren(hashMap);
                    }else {
                    status = false;
                    hashMap.put("isSelected",status);
                    reference.updateChildren(hashMap);
                }
                }
        });




    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

            TextView username ;
            CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.select_user_username);
            checkBox = itemView.findViewById(R.id.selected);
        }
    }
}
