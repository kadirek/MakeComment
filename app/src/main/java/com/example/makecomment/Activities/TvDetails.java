package com.example.makecomment.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.makecomment.Adapters.CommentAdapter;
import com.example.makecomment.Models.Comment;
import com.example.makecomment.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TvDetails extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    //UI
    private TextView nameOfShow,userName;
    private ImageView userImg,imgOfShow;
    private EditText commentField;
    private Button sendComment;

    String channelNumber;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase mDb;

    RecyclerView commentRV;
    CommentAdapter commentAdapter;
    List<Comment> listOfComment;
    static String COMMENT_KEY = "CommentKey" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_details);

        //For full screen and hide actionbar
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getSupportActionBar().hide();

        nameOfShow = findViewById(R.id.titleOfShow);
        userName = findViewById(R.id.userName);
        userImg = findViewById(R.id.commentUserImg);
        imgOfShow = findViewById(R.id.imgOfShow);
        commentField = findViewById(R.id.commentField);
        sendComment = findViewById(R.id.commentSendButton);
        commentRV = findViewById(R.id.commentRV);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        if(mUser==null){
            userImg.setVisibility(View.GONE);
            commentField.setVisibility(View.GONE);
            sendComment.setVisibility(View.GONE);
        }
        channelNumber = getIntent().getExtras().getString("whichChannel");
        //GET DATA from ParseAdapter class
        String imagePicasso = getIntent().getExtras().getString("imageUrl");//show's image
        final String title = getIntent().getExtras().getString("titleName");//show's title
        final String time = getIntent().getExtras().getString("starttime");//show's start time
        String duration = getIntent().getExtras().getString("durationMinute");//show's duration (minute)

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment.setVisibility(View.INVISIBLE);

                DatabaseReference dRef = mDb.getReference("Comment").child(channelNumber).push();
                String contentOfComment = commentField.getText().toString();
                String uid = mUser.getUid();
                String uImg = mUser.getPhotoUrl().toString();
                String uName = mUser.getDisplayName();
                String showName = title;

                Comment comment = new Comment(contentOfComment, uid,uImg,uName,showName);

                dRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TvDetails.this, "Yorum eklendi", Toast.LENGTH_SHORT).show();
                        commentField.setText("");
                        sendComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TvDetails.this, "Hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        Picasso.get().load(imagePicasso).into(imgOfShow);//head photo of tv program
        nameOfShow.setText(title);

        if(signInAccount != null){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(userImg);
            userName.setVisibility(View.VISIBLE);
            userName.setText("@"+signInAccount.getDisplayName());
        }

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//adding duration to start time and finding the finish hour
        Date d = null;
        try {
            d = df.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, Integer.valueOf(duration));
        final String newTime = df.format(cal.getTime());

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Comment");
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(channelNumber)) {
                    Toast.makeText(TvDetails.this, "Burda yorum var", Toast.LENGTH_SHORT).show();
                    try {//Check if tv show finished or still live
                        Calendar now = Calendar.getInstance();
                        String current_hour = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                        String st_hour = time;
                        String en_hour = newTime;
                        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        Date sth = null;
                        sth = format.parse(st_hour);
                        Date enh = format.parse(en_hour);
                        Date nowh = format.parse(current_hour );
                        if (nowh != null) {
                            if (nowh.before(enh) && nowh.after(sth) && sth.before(enh)
                                    || enh.before(sth) && !(nowh.before(enh) && nowh.after(sth))) {
                                Log.d(TAG, "gecti");
                            } else {
                                Toast.makeText(TvDetails.this, "Yeni Program Başladı", Toast.LENGTH_SHORT).show();//saati geçti ve yorumlar silindi
                                FirebaseDatabase.getInstance().getReference("Comment").child(channelNumber).removeValue();
                            }
                        }
                    } catch (ParseException ignored) {
                    }



                    FirebaseDatabase.getInstance().getReference().child("Comment").child(channelNumber).orderByChild("showName").equalTo(title)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Toast.makeText(TvDetails.this, "selam", Toast.LENGTH_SHORT).show();//yorum var ancak program hala bitmedi
                                        Log.d(TAG, "kackadir "+ dataSnapshot.getRef());
                                        //bus number exists in Database
                                    } else {
                                        Toast.makeText(TvDetails.this, "Yeni Program Başladı", Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference("Comment").child(channelNumber).removeValue();//Programa özel yapılan yorumlar silindi

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        initCommentRV();
    }

    private void initCommentRV() {

        commentRV.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = mDb.getReference("Comment").child(channelNumber);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listOfComment = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listOfComment.add(comment) ;

                }

                commentAdapter = new CommentAdapter(getApplicationContext(),listOfComment);
                commentRV.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
