package com.idkwts.bruh.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.R;

import java.util.List;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.ViewHolder>  {

    private Context mContext;
    private List<User> mUsers;

    public SelectUserAdapter(Context mContext, List<User> mUsers, boolean ischat)
    {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.select_user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());

//        holder.checkBox.setChecked(mUsers.get(position).getSelected());
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer pos = (Integer) holder.checkBox.getTag();
//                if(mUsers.get(pos).getSelected()){
//                    mUsers.get(pos).setSelected(false);
//                }else{
//                    mUsers.get(pos).setSelected(true);
//                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public CheckBox checkBox;

        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            username = itemView.findViewById(R.id.select_user_username);
            checkBox  = itemView.findViewById(R.id.checkbox);
        }
    }

}