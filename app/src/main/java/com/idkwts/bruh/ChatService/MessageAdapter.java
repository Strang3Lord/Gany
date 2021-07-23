package com.idkwts.bruh.ChatService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.reactbutton.ReactButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.idkwts.bruh.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Chat> messageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    String seenDateTime;
    Context context;


    public MessageAdapter(List<Chat> messageList) {
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        final Chat messages = messageList.get(position);

        String fromUserID = messages.getSender();
        String fromMessageType = messages.getType();

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.seenRight.setVisibility(View.GONE);
        holder.seenLeft.setVisibility(View.GONE);
        holder.reaction2.setVisibility(View.GONE);
        holder.reaction1.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);

        if (fromMessageType.equals("text")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.reaction2.setVisibility(View.VISIBLE);
                holder.senderMessageText.setText(messages.getMessage());
            } else {
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
                holder.reaction1.setVisibility(View.VISIBLE);
            }
        }


        else if (fromMessageType.equals("image")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
            } else {
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
            }
        }


        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")) {
            if (fromUserID.equals(messageSenderId)) {
                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messageList.get(position).getMessage()));
                        holder.itemView.getContext().startActivity(intent);
                    }
                });
            } else {
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
            }
        }

        holder.rightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.seenRight.setText(messages.getTime() + " - " + messages.getDate());
                holder.seenRight.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.seenRight.setVisibility(View.GONE);                        }
                }, 5000);
            }
        });




//        holder.reaction2.setReactions(FbReactions.reactions);
//        holder.reaction2.setDefaultReaction(FbReactions.defaultReact);
//
//        holder.reaction2.setOnReactionChangeListener(new ReactButton.OnReactionChangeListener() {
//            @Override
//            public void onReactionChange(Reaction reaction) {
//                Log.d(TAG, "onReactionChange: " + reaction.getReactText());
//                FirebaseDatabase.getInstance().getReference().child("Messages") .child(userMessagesList.get(position).getSender())
//                        .child(userMessagesList.get(position).getReceiver())
//                        .child(userMessagesList.get(position).getMessageID()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        userMessagesList.get(position).setReaction(reaction.getReactText());
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });


        holder.leftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.seenLeft.setText(messages.getTime() + " - " + messages.getDate());
                holder.seenLeft.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.seenLeft.setVisibility(View.GONE);                        }
                }, 5000);
            }
        });




        holder.senderSideReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (messageList.get(position).getType().equals("text")){
            holder.leftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    //creating a popup menu
                    final PopupMenu popup = new PopupMenu(view.getContext(), holder.leftLayout);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.text_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copy:
                                    setClipboard(view.getContext(), holder.receiverMessageText.getText().toString());
                                    break;
                                case R.id.delete:
                                    deleteMessageForEveryOne(position,holder);
                                    break;
                                case R.id.edit:
                                   // updateMessage(position,holder);                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            });

        }


            if (messageList.get(position).getType().equals("text")){
            holder.leftLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    final PopupMenu popup = new PopupMenu(view.getContext(), holder.leftLayout);
                    popup.inflate(R.menu.text_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copy:
                                    setClipboard(view.getContext(), holder.receiverMessageText.getText().toString());
                                    break;
                                case R.id.delete:
                                    break;

                            }
                            return false;
                        }
                    });
                        popup.getMenu().findItem(R.id.delete).setVisible(false);
                        popup.getMenu().findItem(R.id.edit).setVisible(false);

                    popup.show();
                    return true;
                }
            });
        }else if(messageList.get(position).getType().equals("img")){

            holder.leftLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    final PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                    popup.inflate(R.menu.image_pdf_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    deleteMessageForEveryOne(position,holder);
                                    break;
                                case R.id.save:
                                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences("ImageView", MODE_PRIVATE). edit();
                                    editor. putString("imageUri", messageList.get(position).getMessage());
                                    editor. apply();
//                                    String imageUri = userMessagesList.get(position).getMessage();
//
//                                    File rootPath = new File(Environment.getExternalStorageDirectory(), "'file_name'");
//                                    if(!rootPath.exists()) {
//                                        rootPath.mkdirs();
//                                    }
//                                    final File localFile = new File(rootPath,"imageName.txt");
//
//                                    final StorageReference downloaded_pictures = FirebaseStorage.getInstance().getReference("Image Files/");
//
//                                    downloaded_pictures.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception exception) {
//                                            Log.e("firebase ",";local tem file not created  created " +exception.toString());
//                                        }
//                                    });
                                    break;

                            }
                            return false;
                        }
                    });
                    popup.show();
                    return true;
                }
            });

        }
        if (messageList.get(position).getType().equals("text")){
            holder.rightLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {

                    final PopupMenu popup = new PopupMenu(view.getContext(), holder.rightLayout);
                    popup.inflate(R.menu.text_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copy:
                                    setClipboard(view.getContext(), holder.senderMessageText.getText().toString());
                                    break;
                                case R.id.delete:
                                    deleteMessageForEveryOne(position,holder);
                                    break;
                                case R.id.edit:
//                                    String temp = holder.senderMessageText.getText().toString();
                                    holder.senderMessageText.setVisibility(View.GONE);
                                    holder.updateMessage.setVisibility(View.VISIBLE);
                                    holder.done.setVisibility(View.VISIBLE);
                                    holder.updateMessage.setText(holder.senderMessageText.getText().toString());
                                    holder.updateMessage.requestFocus();

                                    holder.done.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                            rootRef.child("Messages")
                                                    .child(messageList.get(position).getSender())
                                                    .child(messageList.get(position).getReceiver())
                                                    .child(messageList.get(position).getMessageID()).child("message")
                                                    .setValue(holder.updateMessage.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        rootRef.child("Messages")
                                                                .child(messageList.get(position).getReceiver())
                                                                .child(messageList.get(position).getSender())
                                                                .child(messageList.get(position).getMessageID()).child("message")
                                                                .setValue(holder.updateMessage.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {

                                                                    holder.senderMessageText.setVisibility(View.VISIBLE);
                                                                    holder.updateMessage.setVisibility(View.GONE);
                                                                    holder.done.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });



                                        }
                                    });

                                    break;
                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return false;
                }
            });
        }else if(messageList.get(position).getType().equals("img")){

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    //creating a popup menu
                    final PopupMenu popup = new PopupMenu(view.getContext(), holder.itemView);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.image_pdf_menu);
                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    deleteMessageForEveryOne(position,holder);
                                    break;
                                case R.id.save:
                                    SharedPreferences.Editor editor = view.getContext().getSharedPreferences("ImageView", MODE_PRIVATE). edit();
                                    editor. putString("imageUri", messageList.get(position).getMessage());
                                    editor. apply();
//                                    String imageUri = userMessagesList.get(position).getMessage();
//
//                                    File rootPath = new File(Environment.getExternalStorageDirectory(), "'file_name'");
//                                    if(!rootPath.exists()) {
//                                        rootPath.mkdirs();
//                                    }
//                                    final File localFile = new File(rootPath,"imageName.txt");
//
//                                    final StorageReference downloaded_pictures = FirebaseStorage.getInstance().getReference("Image Files/");
//
//                                    downloaded_pictures.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                            Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception exception) {
//                                            Log.e("firebase ",";local tem file not created  created " +exception.toString());
//                                        }
//                                    });
                                    break;

                            }
                            return false;
                        }
                    });
                    //displaying the popup
                    popup.show();
                    return true;
                }
            });

        }

        //                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "Download and View this Document",
//                                        "Cancel",
//                                        "Delete For EveryOne"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteSentMessage(position,holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//
//                                } else if (position == 1) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                } else if (position == 3) {
//                                    deleteMessageForEveryOne(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    } else if (userMessagesList.get(position).getType().equals("text")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "Cancel",
//                                        "Delete For EveryOne"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteSentMessage(position,holder);
//
//                                }else if (position == 2) {
//                                    deleteMessageForEveryOne(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    } else if (userMessagesList.get(position).getType().equals("image")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "View This Image",
//                                        "Cancel",
//                                        "Delete For EveryOne"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteSentMessage(position,holder);
//
//                                } else if (position == 1) {
//                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
//                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(intent);
//                                } else if (position == 3) {
//                                    deleteMessageForEveryOne(position,holder);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
////                    return true;
//                }
//            });
//
//            holder.leftLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(final View view) {
//                    if (userMessagesList.get(position).getType().equals("pdf") || userMessagesList.get(position).getType().equals("docx")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "Download and View this Document",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteReceiverMessage(position,holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//
//                                } else if (position == 1) {
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                            }
//                        });
//                        builder.show();
//                    } else if (userMessagesList.get(position).getType().equals("text")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteReceiverMessage(position,holder);
//                                    Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                            }
//                        });
//                        builder.show();
//                    } else if (userMessagesList.get(position).getType().equals("image")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete For me",
//                                        "View This Image",
//                                        "Cancel"
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                        builder.setTitle("Delete Message");
//
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int position) {
//                                if (position == 0) {
//                                    deleteReceiverMessage(position,holder);
//                                } else if (position == 1) {
//                                    Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
//                                    intent.putExtra("url",userMessagesList.get(position).getMessage());
//                                    holder.itemView.getContext().startActivity(intent);
//                                }
//                            }
//                        });
//                        builder.show();
//                    }
//                    return true;
//                }
//            });

        }




    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText,seenLeft,seenRight,senderSideReply;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public LinearLayout rightLayout,leftLayout,emojiBar;
        public ReactButton reaction1,reaction2;
        public EditText updateMessage;
        public Button done;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

             reaction1 = (ReactButton) itemView.findViewById(R.id.rection1);
             reaction2 = (ReactButton) itemView.findViewById(R.id.rection2);

            done = (Button) itemView.findViewById(R.id.done);
            senderSideReply = (TextView) itemView.findViewById(R.id.senderSideReply);
            updateMessage = (EditText) itemView.findViewById(R.id.editinput);
            seenLeft = (TextView) itemView.findViewById(R.id.txt_seen_left);
            seenRight = (TextView) itemView.findViewById(R.id.txt_seen_right);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.leftLayout);
            rightLayout = (LinearLayout) itemView.findViewById(R.id.rightLayout);

        }
    }


    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }


    private void deleteMessageForEveryOne(final int position, final MessageViewHolder holder)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(messageList.get(position).getSender())
                .child(messageList.get(position).getReceiver())
                .child(messageList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages")
                            .child(messageList.get(position).getReceiver())
                            .child(messageList.get(position).getSender())
                            .child(messageList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            { }
                        }
                    });
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                }
            }
        });
}
}
