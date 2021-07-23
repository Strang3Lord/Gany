package com.idkwts.bruh.ChatService;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.reactbutton.ReactButton;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.idkwts.bruh.MainActivity;
import com.idkwts.bruh.Model.User;
import com.idkwts.bruh.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username,replybar;

    ImageView file_send;
    private Toolbar ChatToolBar;
    FirebaseUser fuser;
    DatabaseReference reference;
    private ProgressDialog loadingBar;

    Intent intent;
//    APIService apiService;
    Boolean notify = false;
    String replyto;
    //for sending messages
    ImageButton btn_send;
    EditText text_send;
    private Uri fileUri;
    private String saveCurrentTime, saveCurrentDate;


    private List<Chat> messagesList;
    private MessageAdapterr messageAdapter;

    //for displaying messages
//    MessageAdapter messageAdapter;
//    List<Chat> mchat;
    private String checker = "",myUrl="",messageSenderID;
    RecyclerView recyclerView;
    private StorageTask uploadTask;


    DatabaseReference RootRef;
    String userid;
    //if the user sees the message
    ValueEventListener seenListener;
    Boolean isReply;

//    //for notification
//    APIService apiService;
//    Boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messagesList = new ArrayList<>();
        //for the toolbar design
        Toolbar toolbar = findViewById(R.id.toolbar_message_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        messageSenderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadingBar = new ProgressDialog(this);
        RootRef = FirebaseDatabase.getInstance().getReference();

        ChatToolBar = (Toolbar) findViewById(R.id.toolbar_message_activity);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_messages_layout,null);
        actionBar.setCustomView(actionBarView);

        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("kk:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //for the message sending part
        btn_send = findViewById(R.id.btn_message_send);
        text_send = findViewById(R.id.text_message);
        file_send = findViewById(R.id.send_files_btn);
        replybar = findViewById(R.id.replybar);


        //for notifying
//        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        profile_image = findViewById(R.id.profile_image_message_activity);
        username = findViewById(R.id.username_message_activity);
        intent = getIntent();

        userid = intent.getStringExtra("userid");

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        file_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "Pdf Files",
                                "MS Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setTitle("Select the File");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {
                            checker = "image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),443);
                        }
                        if(i==1)
                        {
                            checker = "pdf";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select PDF"),443);
                        }
                        if(i==2)
                        {
                            checker = "docx";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select MSWORD FILE"),443);
                        }
                    }
                });
                builder.show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageurl().equals("default"))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        goToProfile();
                    }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });


    }

    private void goToProfile() {
        Intent intent = new Intent(MessageActivity.this, MainActivity.class);
        intent.putExtra("authorid", userid);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 443 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait, we are sending....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fileUri = data.getData();
            if(!checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef = "Messages/" + messageSenderID + "/" + userid;
                final String messageReceiverRef = "Messages/" + userid + "/" + messageSenderID;

                final DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(userid).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            if(checker.equals("pdf"))
                            {
                                messageTextBody.put("type", checker);
                            }
                            else
                            {
                                messageTextBody.put("type", checker);
                            }

                            messageTextBody.put("sender", messageSenderID);
                            messageTextBody.put("receiver", userid);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCurrentTime);
                            messageTextBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                            DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference();
                            RootRef.updateChildren(messageBodyDetails);
                            loadingBar.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        loadingBar.setMessage((int)p + "% Uploading...");

                    }
                });
            }

            else if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                final String messageSenderRef = "Messages/" + messageSenderID + "/" + userid;
                final String messageReceiverRef = "Messages/" + userid + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(userid).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        Map messageTextBody = new HashMap();
                        messageTextBody.put("message", myUrl);
                        messageTextBody.put("name",fileUri.getLastPathSegment());
                        messageTextBody.put("type", checker);
                        messageTextBody.put("sender", messageSenderID);
                        messageTextBody.put("receiver", userid);
                        messageTextBody.put("messageID", messagePushID);
                        messageTextBody.put("time", saveCurrentTime);
                        messageTextBody.put("date", saveCurrentDate);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                        FirebaseDatabase.getInstance().getReference().updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task)
                            {
                                if (!task.isSuccessful())
                                {
                                    Toast.makeText(MessageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                                loadingBar.dismiss();
                                text_send.setText("");
                            }
                        });
                    }
                });

            }
            else
            {
                loadingBar.dismiss();
                Toast.makeText(this,"nothing selected,error",Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void sendMessage()
    {

        if(replybar.getVisibility()==View.VISIBLE){
            replyto = replybar.getText().toString();
        }else{
            replyto="";
        }
        String messageText = text_send.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + userid;
            String messageReceiverRef = "Messages/" + userid + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(userid).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("sender", messageSenderID);
            messageTextBody.put("receiver", userid);
            messageTextBody.put("replyto", replyto);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails);

            final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userid);

            final DatabaseReference chatref2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(userid).child(fuser.getUid());

            addUserinChatLists(chatref,fuser.getUid(),userid);
            addUserinChatLists(chatref2,userid,fuser.getUid());

            text_send.setText("");
            replybar.setVisibility(View.GONE);
        }

    }

    private void addUserinChatLists(DatabaseReference databaseReference, String userId, String fuserId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(userId).child(fuserId);

        DatabaseReference finalDatabaseReference = databaseReference;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                finalDatabaseReference.child("id").setValue(fuserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        messagesList = new ArrayList<>();
        reference = RootRef.child("Messages").child(messageSenderID).child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    messagesList.add(chat);
                    messageAdapter = new MessageAdapterr(messagesList);
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    messageAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//    private void addUserinChatLists(DatabaseReference databaseReference,String userId,String fuserId) {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(userId).child(fuserId);
//
//        DatabaseReference finalDatabaseReference = databaseReference;
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                finalDatabaseReference.child("id").setValue(fuserId);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }


    }

    private class MessageAdapterr extends RecyclerView.Adapter<MessageAdapterr.MessageViewHolder> {
        private List<Chat> messageList;
        private FirebaseAuth mAuth;
        private DatabaseReference usersRef;
        String seenDateTime;
        Context context;


        public MessageAdapterr(List<Chat> messageList) {
            this.messageList = messageList;
        }


        @NonNull
        @Override
        public MessageAdapterr.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.custom_messages_layout, viewGroup, false);

            mAuth = FirebaseAuth.getInstance();
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
            String messageSenderId = mAuth.getCurrentUser().getUid();
            final Chat message = messageList.get(position);

            String fromUserID = message.getSender();
            String fromMessageType = message.getType();
            String replyg = message.getReplyto();

            holder.senderSideReply.setText(replyg);

//            if(holder.senderSideReply.getText().equals("")){
//                holder.senderSideReply.setVisibility(View.GONE);
//            }else{
//                holder.senderSideReply.setVisibility(View.VISIBLE);
//            }

            holder.recieverSideReply.setText(replyg);

//            if(holder.recieverSideReply.getText().equals("")){
//                holder.recieverSideReply.setVisibility(View.GONE);
//            }else{
//                holder.recieverSideReply.setVisibility(View.VISIBLE);
//            }

            holder.replyLeft.setVisibility(View.GONE);
            holder.replyRight.setVisibility(View.GONE);
            holder.senderSideReply.setVisibility(View.GONE);
            holder.recieverSideReply.setVisibility(View.GONE);
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
//                    holder.reaction2.setVisibility(View.VISIBLE);
                    holder.senderMessageText.setText(message.getMessage());
                    holder.replyRight.setVisibility(View.VISIBLE);
                    if(!replyg.equals("")){
                        holder.senderSideReply.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.receiverMessageText.setVisibility(View.VISIBLE);
                    holder.receiverMessageText.setText(message.getMessage());
//                    holder.reaction1.setVisibility(View.VISIBLE);
                    holder.replyLeft.setVisibility(View.VISIBLE);
                    if(!replyg.equals("")){
                        holder.recieverSideReply.setVisibility(View.VISIBLE);
                    }
                }
            }
            else if (fromMessageType.equals("image")) {
                if (fromUserID.equals(messageSenderId)) {
                    holder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(message.getMessage()).into(holder.messageSenderPicture);
                } else {
                    holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(message.getMessage()).into(holder.messageReceiverPicture);
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
                    holder.seenRight.setText(message.getTime() + " - " + message.getDate());
                    holder.seenRight.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.seenRight.setVisibility(View.GONE);                        }
                    }, 5000);
                }
            });


            holder.leftLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.seenLeft.setText(message.getTime() + " - " + message.getDate());
                    holder.seenLeft.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.seenLeft.setVisibility(View.GONE);                        }
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



            holder.replyRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.receiverMessageText.setBackgroundResource(R.drawable.bg_toolbar);
                    replybar.setVisibility(View.VISIBLE);
                    replybar.setText(message.getMessage().toString());
                    text_send.requestFocus();
                }
            });

            holder.replyLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.receiverMessageText.setBackgroundResource(R.drawable.bg_toolbar);
                    replybar.setVisibility(View.VISIBLE);
                    replybar.setText(message.getMessage().toString());
                    text_send.requestFocus();
                }
            });



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
            public TextView senderMessageText, receiverMessageText,seenLeft,seenRight,senderSideReply,recieverSideReply;
            public ImageView messageSenderPicture, messageReceiverPicture;
            public LinearLayout rightLayout,leftLayout,emojiBar;
            public ReactButton reaction1,reaction2;
            public EditText updateMessage;
            public Button done,replyRight,replyLeft;


            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);

                reaction1 = (ReactButton) itemView.findViewById(R.id.rection1);
                reaction2 = (ReactButton) itemView.findViewById(R.id.rection2);

                done = (Button) itemView.findViewById(R.id.done);
                replyLeft = (Button) itemView.findViewById(R.id.replyLeft);
                replyRight = (Button) itemView.findViewById(R.id.replyRight);
                senderSideReply = (TextView) itemView.findViewById(R.id.senderSideReply);
                recieverSideReply = (TextView) itemView.findViewById(R.id.recieverSideReply);
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

}