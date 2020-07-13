package com.example.makecomment.Activities;

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

import java.text.DateFormat;
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
        Log.d(TAG, "ezhelkadir "+ channelNumber);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment.setVisibility(View.INVISIBLE);

                DatabaseReference dRef = mDb.getReference("Channels").child(channelNumber).child("Comment").push();
                String contentOfComment = commentField.getText().toString();
                String uid = mUser.getUid();
                String uImg = mUser.getPhotoUrl().toString();
                String uName = mUser.getDisplayName();

                Comment comment = new Comment(contentOfComment, uid,uImg,uName);

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
        //GET DATA from ParseAdapter class
        String imagePicasso = getIntent().getExtras().getString("imageUrl");
        String title = getIntent().getExtras().getString("titleName");
        String time = getIntent().getExtras().getString("starttime");
        String duration = getIntent().getExtras().getString("durationMinute");
        Log.d(TAG, "gadirel "+time);
        Log.d(TAG, "gadire"+duration);

        Picasso.get().load(imagePicasso).into(imgOfShow);//head photo of tv program
        nameOfShow.setText(title);

        if(signInAccount != null){
            Picasso.get().load(signInAccount.getPhotoUrl()).into(userImg);
            userName.setVisibility(View.VISIBLE);
            userName.setText("@"+signInAccount.getDisplayName());
        }



/*        long cutoff = new Date().getTime() - TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS);
        Query oldItems = mDb.orderByChild("timestamp").endAt(cutoff);
        oldItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    itemSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });*/

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date d = null;
        try {
            d = df.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, Integer.valueOf(duration));
        String newTime = df.format(cal.getTime());

        Log.d(TAG, "suresiNeBitissssssssssssss "+ newTime);

        Calendar c = Calendar.getInstance();
        Date date=c.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String getCurrentTime=dateFormat.format(date);
        Log.d(TAG, "suresiNeBitissssssssssssss: "+ getCurrentTime);

        if (getCurrentTime.compareTo(newTime) > 0)
        {
            Toast.makeText(TvDetails.this, "BAZI YORUMLAR SİLİNDİ", Toast.LENGTH_SHORT).show();//SÜRESİ GEÇİMİŞ
            FirebaseDatabase.getInstance().getReference("Channels").child(channelNumber).child("Comment").removeValue();
        }


        initCommentRV();
    }

    private void initCommentRV() {

        commentRV.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "ezhel "+ channelNumber);

        DatabaseReference commentRef = mDb.getReference("Channels").child(channelNumber).child("Comment");
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
